package com.iabtcf.encoder;

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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.BitSet;

import com.iabtcf.ByteBitVector;
import com.iabtcf.utils.ByteBitVectorUtils;
import org.junit.Test;

public class BitWriterTest {

    @Test
    public void testWriteSingleByte() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(1, 6);
        bitWriter.write(3, 2);
        byte[] expected = new byte[] { 0b00000111 };
        assertArrayEquals(expected, bitWriter.array());
    }

    @Test
    public void testWriteToMultipleBytes() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(1, 16);
        byte[] expected = new byte[] { 0, 1 };
        byte[] array = bitWriter.array();
        assertArrayEquals(expected, array);
    }

    @Test
    public void testWriteInstant() {
        Instant now = Instant.now();
        BitWriter bitWriter = new BitWriter();
        bitWriter.writeInstant(now);

        ByteBitVector reader = new ByteBitVector(bitWriter.array());
        long l = reader.readBits36(0);
        long actual = now.toEpochMilli() / 100;
        assertEquals(l, actual);
    }

    @Test
    public void testBitSet() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(1, 15);
        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(10);
        bitWriter.write(bitSet, 11);

        ByteBitVector reader = new ByteBitVector(bitWriter.array());
        BitSet readBitSet = reader.readBitSet(15, 11);
        assertEquals(bitSet, readBitSet);
    }

    @Test
    public void testStr() {
        BitWriter bitWriter = new BitWriter();
        String str = "DE";
        bitWriter.writeStr(str);

        ByteBitVector reader = new ByteBitVector(bitWriter.array());
        String actual = ByteBitVectorUtils.readStr2(reader, 0);
        assertEquals(str, actual);
    }

    @Test
    public void shouldIncreaseBufferCapacity() {
        int requestedLength = 1 << 11;
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(0, requestedLength);
        int length = bitWriter.array().length;
        assertTrue(length >= requestedLength / 8);
    }

    @Test
    public void testWriteSingleBit() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.writeBit(true);
        byte[] array = bitWriter.array();
        assertEquals(1, array.length);
        assertEquals((int) array[0] & 0xFF, 0b10000000);
    }

}
