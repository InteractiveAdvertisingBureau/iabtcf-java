package com.iabtcf.encoder;

/*-
 * #%L
 * IAB TCF Java Encoder Library
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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.BitSet;
import java.util.PrimitiveIterator.OfLong;

import com.iabtcf.encoder.exceptions.ValueOverflowException;
import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterator;

/**
 * Provides the ability to construct a byte array that is iabtcf compliant. The BitWriter provides
 * the ability to append bits of various types and sizes in an effort to construct the byte array.
 */
class BitWriter {
    private static final long[] LONG_MASKS = new long[Long.SIZE + 1];
    private static final long DAY_AS_DECISECONDS = 864_000; // 24 * 60 * 60 * 10

    static {
        for (int i = 0; i < Long.SIZE; i++) {
            LONG_MASKS[i] = (1L << i) - 1;
        }
        LONG_MASKS[Long.SIZE] = ~0L;
    }

    private final OfLongIterable buffer = new OfLongIterable();
    private int bitsRemaining = Long.SIZE;
    private long pending = 0L;
    private int precision = 0;

    public BitWriter() {
        this(0);
    }

    /**
     * The 'precision' parameter can be used for encoding fields that must occupy a fixed-number of
     * bits. These padding bits are only honored in {@link BitWriter#write(BitWriter)} and
     * {@link BitWriter#toByteArray()}
     */
    public BitWriter(int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision must be non-negative");
        }

        this.precision = precision;
    }

    public void write(boolean value) {
        write(value ? 1 : 0, 1);
    }

    public void write(boolean data, FieldDefs field) {
        assert (field.getLength() == 1);
        write(data);
    }

    /**
     * Writes an iabtcf encoded String..
     */
    public void write(String str) {
        assert Charset.forName("US-ASCII").newEncoder().canEncode(str);
        byte[] b = str.toUpperCase().getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < b.length; i++) {
            writeV(b[i] - 'A', FieldDefs.CHAR);
        }
    }

    /**
     * Writes an iabtcf encoded String of length specified by 'field'.
     */
    public void write(String str, FieldDefs field) {
        assert (field.getLength() / FieldDefs.CHAR.getLength()) == str.length();
        write(str);
    }

    /**
     * Writes a series of bits of 'field' length whose set bits are indicated by the position of the
     * ints in 'of'. The least significant bit starts at 1.
     *
     * @throws IndexOutOfBoundsException if 'of' contains an invalid index, <= 0.
     */
    public void write(IntIterable of, FieldDefs field) {
        write(of, field.getLength());
    }

    /**
     * Writes an iabtcf encoded instant value.
     */
    public void write(Instant i, FieldDefs field) {
        write(i.toEpochMilli() / 100, field);
    }

    /**
     * Writes an iabtcf encoded instant value, with Days precision only.
     */
    public void writeDays(Instant i, FieldDefs field) {
        long timeAsDeciseconds = i.toEpochMilli() / 100;
        long precisionToDrop = timeAsDeciseconds % DAY_AS_DECISECONDS;
        write(timeAsDeciseconds - precisionToDrop, field);
    }

    /**
     * Writes 'length' number of bits whose set bits are indicated by the position of the ints in 'of'.
     * The least significant bit starts at 1.
     *
     * @throws IndexOutOfBoundsException if 'of' contains an invalid index, <= 0 or length is < 0
     */
    public void write(IntIterable of, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }

        BitWriter bw = new BitWriter(length);
        BitSet bs = new BitSet();
        for (IntIterator i = of.intIterator(); i.hasNext();) {
            int nextInt = i.nextInt();
            if (nextInt <= 0) {
                throw new IndexOutOfBoundsException("invalid index: " + nextInt);
            }

            if (nextInt > length) {
                // we continue here, can't assume IntIterable is in sorted order
                continue;
            }

            bs.set(nextInt - 1);
        }
        for (int i = 0; i < length; i++) {
            bw.write(bs.get(i));
        }
        write(bw);
    }

    /**
     * Writes an iabtcf encoded instant value, FieldDefs.TIMESTAMP.
     */
    public void write(Instant i) {
        write(i, FieldDefs.TIMESTAMP);
    }

    /**
     * Writes up 'field' length number of bits from 'data'.
     */
    public void write(long data, FieldDefs field) {
        write(data, field.getLength());
    }

    /**
     * Writes up 'field' length number of bits from 'data', checking for boundary.
     *
     * @throws ValueOverflowException if i cannot be encoded by field#getLength number of bits.
     */
    public void writeV(long i, FieldDefs field) {
        Bounds.checkBounds(i, field);
        write(i, field);
    }

    /**
     * Writes up to 'length' number of bits from 'data'.
     */
    public void write(long data, int length) {
        if (length == 0) {
            return;
        }

        if (length < 0 || length > Long.SIZE) {
            throw new IllegalArgumentException("length is invalid: " + length);
        }

        data &= LONG_MASKS[length];
        bitsRemaining -= length;
        precision -= length;

        if (bitsRemaining > 0) {
            pending |= data << bitsRemaining;
        } else {
            buffer.add(pending | (data >>> -bitsRemaining));
            bitsRemaining += Long.SIZE;
            pending = bitsRemaining == Long.SIZE ? 0L : (data << bitsRemaining);
        }
    }

    /**
     * Writes bits encoded by the specified BitWriter. Padding bits, if any, are also appended.
     */
    public void write(BitWriter bw) {
        for (OfLong i = bw.buffer.longIterator(); i.hasNext();) {
            write(i.nextLong(), Long.SIZE);
        }
        write(bw.pending >>> bw.bitsRemaining, Long.SIZE - bw.bitsRemaining);

        enforcePrecision(bw.precision);
    }

    /**
     * Returns the number of bits, including any padding bits that are to be written.
     */
    public int length() {
        return buffer.size() * Long.SIZE + (Long.SIZE - bitsRemaining) + ((precision >= 0) ? precision : 0);
    }

    /**
     * Returns the byte array.
     */
    public byte[] toByteArray() {
        enforcePrecision();

        int bytesToWrite = (Long.SIZE + (Byte.SIZE - 1) - bitsRemaining) >>> 3;

        ByteBuffer bb = ByteBuffer.allocate(buffer.size() * (Long.SIZE / Byte.SIZE) + bytesToWrite);

        for (OfLong li = buffer.longIterator(); li.hasNext();) {
            bb.putLong(li.nextLong());
        }

        for (int i = 0; i < bytesToWrite; i++) {
            bb.put((byte) (pending >>> (Long.SIZE - Byte.SIZE - i * Byte.SIZE)));
        }

        return bb.array();
    }

    protected void enforcePrecision(int p) {
        if (p <= 0) {
            return;
        }

        for (int i = 0; i < p / Long.SIZE; i++) {
            write(0L, Long.SIZE);
        }

        write(0L, p % Long.SIZE);
    }

    private void enforcePrecision() {
        enforcePrecision(precision);
        precision = 0;
    }

    /**
     * Returns a base64 url encoded representation of the bit array.
     */
    public String toBase64() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(this.toByteArray());
    }
}
