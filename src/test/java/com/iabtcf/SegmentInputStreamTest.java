package com.iabtcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SegmentInputStreamTest {

    private static SegmentInputStream newSegmentInputStream(String str) {
        return newSegmentInputStream(str, 0);
    }

    private static SegmentInputStream newSegmentInputStream(String str, int start) {
        return new SegmentInputStream(str, start);
    }

    @Test
    public void testSegment() throws IOException {
        String src = "hello world";
        SegmentInputStream s = newSegmentInputStream(src);

        String text = IOUtils.toString(s, StandardCharsets.UTF_8.name());
        assertEquals(src, text);
    }

    @Test
    public void testSegmentMid() throws IOException {
        String src = "hello world";
        SegmentInputStream s = newSegmentInputStream(src, 6);

        String text = IOUtils.toString(s, StandardCharsets.UTF_8.name());
        assertEquals("world", text);
    }

    @Test
    public void testSegmentEndMinusOne() throws IOException {
        String src = "hello world";
        SegmentInputStream s = newSegmentInputStream(src, src.length() - 1);

        assertEquals(1, s.available());

        String text = IOUtils.toString(s, StandardCharsets.UTF_8.name());
        assertEquals("d", text);
    }

    @Test
    public void testSegmentEnd() throws IOException {
        String src = "hello world";
        SegmentInputStream s = newSegmentInputStream(src, src.length());

        assertEquals(0, s.available());
        assertEquals(-1, s.read());
    }

    @Test
    public void testSegmentPast() throws IOException {
        String src = "hello world";
        SegmentInputStream s = newSegmentInputStream(src, src.length() + 1);

        assertEquals(0, s.available());
        assertEquals(-1, s.read());
    }

    @Test
    public void testMultipleSegments() throws IOException {
        String src = "hello.world";
        SegmentInputStream s = newSegmentInputStream(src);

        String text = IOUtils.toString(s, StandardCharsets.UTF_8.name());
        assertEquals("hello", text);

        assertEquals(0, s.available());
        assertEquals(-1, s.read());
        assertTrue(s.hasNextSegment());

        s = newSegmentInputStream(src, s.segmentEnd() + 1);
        assertEquals(1, s.available());

        text = IOUtils.toString(s, StandardCharsets.UTF_8.name());
        assertEquals("world", text);

        assertFalse(s.hasNextSegment());
        assertEquals(0, s.available());
        assertEquals(-1, s.read());
    }

    @Test
    public void testSegmentBase64() throws IOException {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        String src = encoder.encodeToString("hello".getBytes(StandardCharsets.US_ASCII)) + "."
                + encoder.encodeToString("world".getBytes(StandardCharsets.US_ASCII));
        Base64.Decoder decoder = Base64.getDecoder();
        SegmentInputStream sis = newSegmentInputStream(src);

        String text = IOUtils.toString(decoder.wrap(sis), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        assertTrue(sis.hasNextSegment());
        sis = newSegmentInputStream(src, sis.segmentEnd() + 1);

        text = IOUtils.toString(decoder.wrap(sis), StandardCharsets.US_ASCII);
        assertEquals("world", text);

        assertFalse(sis.hasNextSegment());

        sis = newSegmentInputStream(src, sis.segmentEnd() + 1);
        text = IOUtils.toString(decoder.wrap(sis), StandardCharsets.US_ASCII);
        assertEquals("", text);
    }

    @Test
    public void testSegmentENd() {
        String s1 = "hello";
        String s2 = "hello.";

        SegmentInputStream sis1 = newSegmentInputStream(s1);
        SegmentInputStream sis2 = newSegmentInputStream(s2);

        assertEquals(sis1.segmentEnd(), sis2.segmentEnd());
    }

    @Test
    public void testEmptySegments() throws IOException {
        String src = "..";
        SegmentInputStream sis1 = new SegmentInputStream(src, 0);
        assertTrue(sis1.hasNextSegment());
        assertEquals(-1, sis1.read());

        SegmentInputStream sis2 = new SegmentInputStream(src, sis1.segmentEnd() + 1);
        assertTrue(sis2.hasNextSegment());
        assertEquals(-1, sis2.read());


        SegmentInputStream sis3 = new SegmentInputStream(src, sis2.segmentEnd() + 1);
        assertFalse(sis3.hasNextSegment());
        assertEquals(-1, sis3.read());

        sis1.close();
        sis2.close();
        sis3.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCtor() throws IOException {
        try (SegmentInputStream v = new SegmentInputStream(null, 0)) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCtor() throws IOException {
        try (SegmentInputStream v = new SegmentInputStream("123", -1)) {
        }
    }

    @Test
    public void testUnicode() throws IOException {
        char c = 0x32F4;
        String s = new String(new char[] {c, c});
        
        assertEquals(s, IOUtils.toString(new StringReader(s)));

        String text = IOUtils.toString(new SegmentInputStream(s, 0), StandardCharsets.UTF_8);
        assertNotEquals(s, text);

        text = IOUtils.toString(new SegmentInputStream(s, 0), StandardCharsets.US_ASCII);
        assertNotEquals(s, text);
    }
}
