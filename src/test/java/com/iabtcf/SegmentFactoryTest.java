package com.iabtcf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SegmentFactoryTest {
    @Test
    public void testFcatory() throws IOException {
        LazySegmentFactory f = new LazySegmentFactory("hello.world");
        Supplier<InputStream> f1 = f.next();
        Supplier<InputStream> f2 = f.next();
        Supplier<InputStream> f3 = f.next();

        String text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        text = IOUtils.toString(f2.get(), StandardCharsets.US_ASCII);
        assertEquals("world", text);

        text = IOUtils.toString(f3.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);
    }

    @Test
    public void testFcatory_1() throws IOException {
        LazySegmentFactory f = new LazySegmentFactory("hello.world");
        Supplier<InputStream> f1 = f.next();
        Supplier<InputStream> f2 = f.next();
        Supplier<InputStream> f3 = f.next();

        String text = IOUtils.toString(f3.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);

        text = IOUtils.toString(f3.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);

        text = IOUtils.toString(f2.get(), StandardCharsets.US_ASCII);
        assertEquals("world", text);

        text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);
    }

    @Test
    public void testReset() throws IOException {
        LazySegmentFactory f = new LazySegmentFactory("hello.world");
        Supplier<InputStream> f1 = f.next();
        String text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        InputStream is = f1.get();
        assertEquals(0, is.available());
        is.reset();

        assertEquals(1, is.available());
        text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);
    }
}
