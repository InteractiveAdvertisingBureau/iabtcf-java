package com.iabtcf;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.iabtcf.decoder.TCString;
import com.iabtcf.exceptions.ByteParseException;
import com.iabtcf.utils.BitReaderUtils;

public class BitReaderTest {
    Random r = new Random();

    @Test(expected = java.lang.AssertionError.class)
    public void testAssert() {
        BitReader bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b10000000}));
        assertTrue(bv.readBits1(FieldDefs.CORE_VERSION));
    }

    @Test
    public void testStreamReadBits() {
        BitReader bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b10000000}));
        assertTrue(bv.readBits1(0));

        bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b00000001}));
        assertTrue(bv.readBits1(7));

        bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b00000000, (byte) 0b10000000}));
        assertTrue(bv.readBits1(8));

        bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b00000000, (byte) 0b00000001}));
        assertTrue(bv.readBits1(15));

        bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b00000000, (byte) 0b00000001}));
        assertEquals(1, bv.readBits3(8 + 5));

        bv = new BitReader(new ByteArrayInputStream(new byte[] {(byte) 0b00000000, (byte) 0b00000001}));
        assertEquals(1, bv.readBits16(0));
    }

    @Test
    public void testReadBits1() {
        BitReader bv = new BitReader(new byte[] {(byte) 0b10000000});
        assertTrue(bv.readBits1(0));

        bv = new BitReader(new byte[] {(byte) 0b00000001});
        assertTrue(bv.readBits1(7));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b10000000});
        assertTrue(bv.readBits1(8));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00000001});
        assertTrue(bv.readBits1(15));
    }

    @Test
    public void testParseV1() {
        // String str = "BOvalCcOvZ7NhABABBAAABAAAAAAEA";
        String str = "BOvalCcOvZ7NaABABBAAABAAAAAAEA";

        byte[] bytes = Base64.getUrlDecoder().decode(str);

        BitReader bv = new BitReader(bytes);
        assertEquals(1, bv.readBits6(0));

        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(6) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);

        zdt = Instant.ofEpochMilli(bv.readBits36(6 + 36) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T18:38:01-08:00[America/Los_Angeles]"), zdt);

        // cmp
        assertEquals(1, bv.readBits12(6 + 36 + 36));

        // cmp version
        assertEquals(1, bv.readBits12(6 + 36 + 36 + 12));
    }

    @Test
    public void testReadBits2() {
        byte[] g = new byte[] {(byte) 0b10001000, (byte) 0b00000000, 0x00};

        for (int i = 0; i < 10; i++) {
            BitReader bv = new BitReader(g);
            assertEquals(2, bv.readBits2(i));

            shift(g);
        }
    }

    @Test
    public void testReadBits3() {
        byte[] g = new byte[] {(byte) 0b10101000, (byte) 0b00000000, 0x00};

        for (int i = 0; i < 10; i++) {
            BitReader bv = new BitReader(g);
            assertEquals(5, bv.readBits3(i));

            shift(g);
        }
    }

    @Test
    public void testReadBits6VersionField() {
        String str = "BOvalCcOvZ7NhABABBAAABAAAAAAEA";

        byte[] bytes = Base64.getUrlDecoder().decode(str);

        BitReader bv = new BitReader(bytes);
        assertEquals(1, bv.readBits6(0));

        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(6) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);

        zdt = Instant.ofEpochMilli(bv.readBits36(6 + 36) * 100).atZone(zoneId);

        assertEquals(1, bv.readBits12(6 + 36 + 36));
    }

    @Test
    public void testReadBits6_1() {
        BitReader bv = new BitReader(new byte[] {(byte) 0b00001000, (byte) 0b00000000});
        assertEquals(2, bv.readBits6(0));

        bv = new BitReader(new byte[] {(byte) 0b00000100, (byte) 0b00000000});
        assertEquals(2, bv.readBits6(1));

        bv = new BitReader(new byte[] {(byte) 0b00000010, (byte) 0b00000000});
        assertEquals(2, bv.readBits6(2));

        bv = new BitReader(new byte[] {(byte) 0b00000001, (byte) 0b00000000});
        assertEquals(2, bv.readBits6(3));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b10000000});
        assertEquals(2, bv.readBits6(4));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b01000000});
        assertEquals(2, bv.readBits6(5));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00100000});
        assertEquals(2, bv.readBits6(6));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00010000});
        assertEquals(2, bv.readBits6(7));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00001000});
        assertEquals(2, bv.readBits6(8));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00000100});
        assertEquals(2, bv.readBits6(9));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00000010});
        assertEquals(2, bv.readBits6(10));
    }

    @Test
    public void testReadBits6_2() {
        BitReader bv = new BitReader(new byte[] {(byte) 0b10001000, (byte) 0b00000000});
        assertEquals(34, bv.readBits6(0));

        bv = new BitReader(new byte[] {(byte) 0b01000100, (byte) 0b00000000});
        assertEquals(34, bv.readBits6(1));

        bv = new BitReader(new byte[] {(byte) 0b00100010, (byte) 0b00000000});
        assertEquals(34, bv.readBits6(2));

        bv = new BitReader(new byte[] {(byte) 0b00010001, (byte) 0b00000000});
        assertEquals(34, bv.readBits6(3));

        bv = new BitReader(new byte[] {(byte) 0b00001000, (byte) 0b10000000});
        assertEquals(34, bv.readBits6(4));

        bv = new BitReader(new byte[] {(byte) 0b00000100, (byte) 0b01000000});
        assertEquals(34, bv.readBits6(5));

        bv = new BitReader(new byte[] {(byte) 0b00000010, (byte) 0b00100000});
        assertEquals(34, bv.readBits6(6));

        bv = new BitReader(new byte[] {(byte) 0b00000001, (byte) 0b00010000});
        assertEquals(34, bv.readBits6(7));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b10001000});
        assertEquals(34, bv.readBits6(8));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b01000100});
        assertEquals(34, bv.readBits6(9));

        bv = new BitReader(new byte[] {(byte) 0b00000000, (byte) 0b00100010});
        assertEquals(34, bv.readBits6(10));
    }


    @Test
    public void testCapacity() {
        BitReader bv = new BitReader(new byte[] {8});
        assertEquals(2, bv.readBits6(0));
    }

    @Test
    public void testReadBits24_0() {
        BitReader bv = new BitReader(new byte[] {0b00000001, 0b00000001, 0b00000001});
        assertEquals(65793, bv.readBits24(0));
    }

    @Test
    public void testReadBits24_1() {
        BitReader bv =
                new BitReader(new byte[] {(byte) 0x00, (byte) 0b10000000, (byte) 0b10000000, (byte) 0b10000000});
        assertEquals(65793, bv.readBits24(1));
    }

    @Test
    public void testReadBits24_2() {
        BitReader bv;
        byte[] g = new byte[] {(byte) 0x00, (byte) 0b10000000, (byte) 0b10000000, (byte) 0b10000000};
        bv = new BitReader(g);
        assertEquals(65793, bv.readBits24(1));

        shift(g);

        bv = new BitReader(g);
        assertEquals(65793, bv.readBits24(2));
    }

    @Test
    public void testReadBits24N_1() {
        byte[] g = new byte[] {(byte) 0x00, (byte) 0b10000000, (byte) 0b10000000, (byte) 0b10000000, (byte) 0x00};
        for (int i = 0; i < 10; i++) {
            BitReader bv = new BitReader(g);
            assertEquals(String.format("%d", i), 65793, bv.readBits24(1 + i));

            shift(g);
        }
    }

    @Test
    public void testReadBits24N_2() {
        byte[] g = new byte[] {(byte) 0b0000001, (byte) 0b10000000, (byte) 0b10000000, (byte) 0b10000000, (byte) 0x00};
        for (int i = 0; i < 10; i++) {
            BitReader bv = new BitReader(g);
            assertEquals(String.format("%d", i), 196865, bv.readBits24(1 + i));

            shift(g);
        }
    }

    @Test
    public void testReadBits24N_3() {
        byte[] g = new byte[] {(byte) 0b0000001, (byte) 0b10010011, (byte) 0b10100011, (byte) 0b10100000, (byte) 0x00};
        for (int i = 0; i < 10; i++) {
            BitReader bv = new BitReader(g);
            assertEquals(String.format("%d", i), 206663, bv.readBits24(1 + i));

            shift(g);
        }
    }

    @Test
    public void testReadBits24N_Random() {
        for (int i = 0; i < 1000; i++) {
            checkTestReadBits24N_Random();
        }
    }

    private void checkTestReadBits24N_Random() {
        byte[] rb = new byte[4];
        r.nextBytes(rb);

        byte[] g = new byte[] {(byte) 0b0000001, rb[0], rb[1], rb[2], rb[3], (byte) 0x00, (byte) 0x00};

        BitReader bv = new BitReader(g);
        int expect = bv.readBits24(4);

        for (int i = 1; i < 16; i++) {
            shift(g);
            bv = new BitReader(g);
            assertEquals(String.format("%d", i), expect, bv.readBits24(4 + i));
        }
    }

    @Test
    public void testShift() {
        BitReader bv;
        byte[] g = new byte[] {(byte) 0x00, (byte) 0b10000000, (byte) 0b10000000, (byte) 0b10000000};
        bv = new BitReader(g);
        assertEquals(65793, bv.readBits24(1));

        shift(g);

        bv = new BitReader(g);
        assertEquals(65793, bv.readBits24(2));

        g = new byte[] {(byte) 0x00, (byte) 0b01000000, (byte) 0b01000000, (byte) 0b01000000};
        bv = new BitReader(g);
        assertEquals(65793, bv.readBits24(2));

    }

    @Test
    public void testReadBits36Msb() {
        BitReader bv = new BitReader(new byte[] {0, 0, 0, 0, (byte) 0x10});
        assertEquals(1, bv.readBits36(0));
    }

    @Test
    public void testReadBits36Msb_1() {
        BitReader bv = new BitReader(new byte[] {0, 0, 0x10, 0x10, 0x10});
        assertEquals(65793, bv.readBits36(0));
    }

    @Test
    public void testReadBits36Msb_2() {
        BitReader bv = new BitReader(new byte[] {0, 0, 0x1, 0x1, 0x1});
        assertEquals(65793, bv.readBits36(4));
    }

    @Test
    public void testReadBits36Msb_3() {
        byte[] c = new byte[] {(byte) 0b00000011, (byte) 0b10101111, (byte) 0b01101010, (byte) 0b01010000,
                (byte) 0b10011100};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(4) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_4() {
        byte[] c = new byte[] {(byte) 0b00000111, (byte) 0b01011110, (byte) 0b11010100, (byte) 0b10100001,
                (byte) 0b00111000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(3) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_5() {
        byte[] c = new byte[] {(byte) 0b00001110, (byte) 0b10111101, (byte) 0b10101001, (byte) 0b01000010,
                (byte) 0b01110000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(2) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_6() {
        byte[] c = new byte[] {(byte) 0b00011101, (byte) 0b01111011, (byte) 0b01010010, (byte) 0b10000100,
                (byte) 0b11100000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(1) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_7() {
        byte[] c = new byte[] {(byte) 0b00000001, (byte) 0b11010111, (byte) 0b10110101, (byte) 0b00101000,
                (byte) 0b01001110, (byte) 0b00000000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(5) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_8() {
        byte[] c = new byte[] {(byte) 0b00000000, (byte) 0b11101011, (byte) 0b11011010, (byte) 0b10010100,
                (byte) 0b00100111, (byte) 0b00000000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(6) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_9() {
        byte[] c = new byte[] {(byte) 0b00000000, (byte) 0b01110101, (byte) 0b11101101, (byte) 0b01001010,
                (byte) 0b00010011, (byte) 0b10000000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(7) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_10() {
        byte[] c = new byte[] {(byte) 0b00000000, (byte) 0b00111010, (byte) 0b11110110, (byte) 0b10100101,
                (byte) 0b00001001, (byte) 0b11000000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(8) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_10a() {
        byte[] c = new byte[] {(byte) 0b00111010, (byte) 0b11110110, (byte) 0b10100101,
                (byte) 0b00001001, (byte) 0b11000000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(0) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36Msb_11() {
        byte[] c = new byte[] {(byte) 0b00000000, (byte) 0b00011101, (byte) 0b01111011, (byte) 0b01010010,
                (byte) 0b10000100,
                (byte) 0b11100000};
        BitReader bv = new BitReader(c);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdt = Instant.ofEpochMilli(bv.readBits36(9) * 100).atZone(zoneId);
        assertEquals(ZonedDateTime.parse("2020-02-26T23:23:34-08:00[America/Los_Angeles]"), zdt);
    }

    @Test
    public void testReadBits36N_Random() {
        for (int i = 0; i < 1000; i++) {
            checkTestReadBits36N_Random();
        }
    }

    private void checkTestReadBits36N_Random() {
        byte[] rb = new byte[6];
        r.nextBytes(rb);

        byte[] g = new byte[] {(byte) 0b0000001, rb[0], rb[1], rb[2], rb[3], rb[4], rb[5], (byte) 0x00, (byte) 0x00};

        BitReader bv = new BitReader(g);
        long expect = bv.readBits36(4);

        for (int i = 1; i < 16; i++) {
            shift(g);
            bv = new BitReader(g);
            assertEquals(String.format("%d", i), expect, bv.readBits36(4 + i));
        }
    }

    @Test
    public void testReadBits12N_Random() {
        for (int i = 0; i < 1000; i++) {
            checkTestReadBits12N_Random();
        }
    }

    private void checkTestReadBits12N_Random() {
        byte[] rb = new byte[4];
        r.nextBytes(rb);

        byte[] g = new byte[] {(byte) 0b0000001, rb[0], rb[1], rb[2], rb[3], (byte) 0x00, (byte) 0x00};

        BitReader bv = new BitReader(g);
        int expect = bv.readBits12(4);

        for (int i = 1; i < 16; i++) {
            shift(g);
            bv = new BitReader(g);
            assertEquals(String.format("%d", i), expect, bv.readBits12(4 + i));
        }
    }

    @Test
    public void testReadBits16N_Random() {
        for (int i = 0; i < 1000; i++) {
            checkTestReadBits16N_Random();
        }
    }

    private void checkTestReadBits16N_Random() {
        byte[] rb = new byte[4];
        r.nextBytes(rb);

        byte[] g = new byte[] {(byte) 0b0000001, rb[0], rb[1], rb[2], rb[3], (byte) 0x00, (byte) 0x00};

        BitReader bv = new BitReader(g);
        int expect = bv.readBits16(4);

        for (int i = 1; i < 16; i++) {
            shift(g);
            bv = new BitReader(g);
            assertEquals(String.format("%d", i), expect, bv.readBits16(4 + i));
        }
    }

    @Test
    public void testBitset1() {
        BitReader bv = new BitReader(new byte[] {(byte) 0b11100001});

        BitSet bs = bv.readBitSet(0, 8);

        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(2));
        assertFalse(bs.get(3));
        assertFalse(bs.get(4));
        assertFalse(bs.get(5));
        assertFalse(bs.get(6));
        assertTrue(bs.get(7));
    }

    @Test
    public void testBitset() {
        byte[] g = new byte[] {(byte) 0b11100001, (byte) 0b11001100, (byte) 0x00, (byte) 0x00};

        for (int i = 0; i < 10; i++) {
            BitReader bv = new BitReader(g);

            BitSet bs = bv.readBitSet(i, 16);

            assertTrue(bs.get(0));
            assertTrue(bs.get(1));
            assertTrue(bs.get(2));
            assertFalse(bs.get(3));
            assertFalse(bs.get(4));
            assertFalse(bs.get(5));
            assertFalse(bs.get(6));
            assertTrue(bs.get(7));

            assertTrue(bs.get(8));
            assertTrue(bs.get(9));
            assertFalse(bs.get(10));
            assertFalse(bs.get(11));
            assertTrue(bs.get(12));
            assertTrue(bs.get(13));
            assertFalse(bs.get(14));
            assertFalse(bs.get(15));

            shift(g);
        }
    }

    @Test
    public void testBitsetVendors() {
        String str = "BOvalCcOvZ7NaABABBAAABAAAAAAxwkA";

        byte[] bytes = Base64.getUrlDecoder().decode(str);

        BitReader bv = new BitReader(bytes);
        BitSet bs = bv.readBitSet(173, 12);
        // 0b11100001 0b00100000

        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(2));
        assertFalse(bs.get(3));
        assertFalse(bs.get(4));
        assertFalse(bs.get(5));
        assertFalse(bs.get(6));
        assertTrue(bs.get(7));

        assertFalse(bs.get(8));
        assertFalse(bs.get(9));
        assertTrue(bs.get(10));
        assertFalse(bs.get(11));
    }

    /**
     * Test reading 36 bits with 5 and 6 bytes.
     */
    @Test
    public void testReadBits36_1() {
        // 5 bytes
        long largeValue5 = 68_719_476_735L;

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, largeValue5);
        byte[] arr5 = buffer.array();
        BitReader bv = new BitReader(arr5);
        long result = bv.readBits36(3 * 8 + 4);
        assertEquals(largeValue5, result);


        // 6 bytes

        // remove the least significant byte
        long largeValue = largeValue5 >> 8;

        buffer = ByteBuffer.allocate(Long.BYTES + Byte.BYTES);
        buffer.putLong(0, largeValue);

        // write the least significant byte subsequent byte
        buffer.put(8, (byte) (((1 << 8) - 1) & 0xFF));
        byte[] arr = buffer.array();

        bv = new BitReader(arr);

        // requires reading 6 bytes
        result = bv.readBits36(4 * 8 + 4);
        assertEquals(largeValue5, result);
    }

    @Test(expected = ByteParseException.class)
    public void testReadBeyondBuffer() {
        // bit pattern: 0000011
        TCString tcString = TCString.decode("Bg");
        tcString.getCreated();
    }

    @Test
    public void testCanReadSmallInt() {
        String bitString = "0000 1000 0000 0001";
        BitReader bitVector = fromBitString(bitString);
        assertEquals(2, bitVector.readBits6(0));
    }

    @Test
    public void tesCanReadInstantFromDeciSecond() {
        String bitString = "1 001110101101110010100111000111000100 1";
        BitReader bitVector = fromBitString(bitString);
        assertEquals(
                Instant.parse("2020-01-26T18:19:25.200Z"), Instant.ofEpochMilli(bitVector.readBits36(1) * 100));
    }

    @Test
    public void tesCanReadEpochInstantFromDeciSecond() {
        String bitString = Stream.generate(() -> "0").limit(36).collect(Collectors.joining());
        BitReader bitVector = fromBitString(bitString);
        assertEquals(Instant.EPOCH, Instant.ofEpochMilli(bitVector.readBits36(1) * 100));
    }

    @Test
    public void testCanReadBit() {
        String bitString = "10101010";
        BitReader bitVector = fromBitString(bitString);
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                assertTrue(bitVector.readBits1(i));
            } else {
                assertFalse(bitVector.readBits1(i));
            }
        }
    }

    @Test
    public void testReadSixBitString() {
        String bitString = "000000 000001";
        BitReader bitVector = fromBitString(bitString);
        assertEquals("AB", BitReaderUtils.readStr2(bitVector, 0));
    }

    private BitReader fromBitString(String bits) {
        BitReader bitVector = new BitReader(fromString(bits));
        return bitVector;
    }

    private byte[] fromString(String bitString) {
        String spaceTrimmed = bitString.replaceAll(" ", "");
        byte[] bytes = new byte[(int) Math.ceil(spaceTrimmed.length() / 8.0)];
        int j = 0;
        for (int i = 0; i < spaceTrimmed.length(); i += 8) {
            int endIndex;
            if (i + 8 < spaceTrimmed.length()) {
                endIndex = i + 8;
            } else {
                endIndex = spaceTrimmed.length();
            }
            String sub = spaceTrimmed.substring(i, endIndex);
            sub =
                    sub.length() == 8
                            ? sub
                            : sub
                                    + Stream.generate(() -> "0")
                                        .limit(8 - sub.length())
                                        .collect(Collectors.joining());
            bytes[j++] = (byte) (Integer.parseInt(sub, 2));
        }
        return bytes;
    }

    /**
     * performs a right shift operation on the entire byte array.
     */
    private static void shift(byte[] buffer) {
        byte n = (byte) (buffer[0] & 0x1);
        byte tmp;
        buffer[0] = (byte) ((buffer[0] & 0xFF) >>> 1);

        for (int i = 1; i < buffer.length; i++) {
            tmp = (byte) (buffer[i] & 0x1);
            buffer[i] = (byte) ((buffer[i] & 0xFF) >>> 1);
            buffer[i] |= (n << 7);
            n = tmp;
        }
    }

}
