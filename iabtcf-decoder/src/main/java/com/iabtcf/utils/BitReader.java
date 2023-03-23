package com.iabtcf.utils;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

import com.iabtcf.exceptions.ByteParseException;

/**
 * This is an internal only class and subject to change.
 */
public class BitReader {
    private byte[] buffer;
    private int isrpos;
    private final InputStream is;
    final LengthOffsetCache cache;

    public BitReader(InputStream is) {
        this.buffer = new byte[4096];
        this.is = is;
        this.isrpos = 0;
        cache = new LengthOffsetCache(this);
    }

    public BitReader(byte[] buffer) {
        this.buffer = buffer;
        this.isrpos = buffer.length;
        this.is = null;
        cache = new LengthOffsetCache(this);
    }

    private void ensureCapacity(int length) {
        if (buffer.length >= length) {
            return;
        }

        byte[] b = new byte[length * 2];
        System.arraycopy(buffer, 0, b, 0, buffer.length);
        buffer = b;
    }

    /**
     * @throws ByteParseException
     */
    private boolean ensureReadable(int offset, int length) {
        int tlength = offset + length;
        int n;
        int rem = tlength - isrpos;

        if (tlength <= isrpos) {
            return true;
        }

        if (is == null) {
            throw new ByteParseException(String.format("read %d bytes at index %d out of bounds for buffer length %d",
                    length, offset, buffer.length));
        }

        ensureCapacity(tlength);

        try {
            while (rem > 0) {
                n = is.read(buffer, isrpos, rem);
                if (n == -1) {
                    return false;
                }

                isrpos += n;
                rem -= n;
            }
        } catch (IOException e) {
            throw new ByteParseException(String.format("error decoding at offset %d length %d", offset, length), e);
        }

        return true;
    }

    public String readStr2(int offset) {
        return String
            .valueOf(new char[] {(char) ('A' + readBits6(offset)), (char) ('A' + readBits6(offset + 6))});
    }

    public String readStr2(FieldDefs field) {
        return readStr2(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public boolean readBits1(FieldDefs field) {
        assert field.getLength(this) == 1;
        return readBits1(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public boolean readBits1(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;

        ensureReadable(startByte, 1);

        return ((buffer[startByte] >>> (7 - bitPos)) & 1) == 1;
    }

    /**
     * @throws ByteParseException
     */
    public byte readBits2(FieldDefs field) {
        assert field.getLength(this) == 2;
        return readBits2(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public byte readBits2(int offset) {
        return readByteBits(offset, 2);
    }

    /**
     * @throws ByteParseException
     */
    public byte readBits3(FieldDefs field) {
        assert field.getLength(this) == 3;
        return readBits3(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public byte readBits3(int offset) {
        return readByteBits(offset, 3);
    }

    /**
     * @throws ByteParseException
     */
    public byte readBits6(FieldDefs field) {
        assert field.getLength(this) == 6;
        return readBits6(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public byte readBits6(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        if (n < 6) {
            ensureReadable(startByte, 2);
            return (byte) (unsafeReadLsb(buffer[startByte], 6 - n, n)
                    | unsafeReadMsb(buffer[startByte + 1], 0, 6 - n));
        } else {
            ensureReadable(startByte, 1);
            return unsafeReadMsb(buffer[startByte], bitPos, 6);
        }
    }

    /**
     * When nbits <= 8
     *
     * @throws ByteParseException
     */
    public byte readByteBits(int offset, int nbits) {
        if (nbits < 0 || nbits > 8) {
            throw new ByteParseException("Only 0 to 8 bytes can be read into a byte");
        }

        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        if (n < nbits) {
            ensureReadable(startByte, 2);
            return (byte) (unsafeReadLsb(buffer[startByte], nbits - n, n)
                    | unsafeReadMsb(buffer[startByte + 1], 0, nbits - n));
        } else {
            ensureReadable(startByte, 1);
            return unsafeReadMsb(buffer[startByte], bitPos, nbits);
        }
    }

    /**
     * @throws ByteParseException
     */
    public int readBits12(FieldDefs field) {
        assert field.getLength(this) == 12;
        return readBits12(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public int readBits12(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        if (n < 4) {
            ensureReadable(startByte, 3);
            return (unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 4
                    | (buffer[startByte + 1] & 0xFF) << (bitPos - 4)
                    | (unsafeReadMsb(buffer[startByte + 2], 0, bitPos - 4) & 0xFF);
        } else {
            ensureReadable(startByte, 2);
            return (unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 4
                    | (unsafeReadMsb(buffer[startByte + 1], 0, 4 + bitPos) & 0xFF);
        }
    }

    /**
     * @throws ByteParseException
     */
    public int readBits16(FieldDefs field) {
        assert field.getLength(this) == 16;
        return readBits16(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public int readBits16(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        if (n < 8) {
            ensureReadable(startByte, 3);
            return ((unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 8)
                    | (buffer[startByte + 1] & 0xFF) << bitPos
                    | (unsafeReadMsb(buffer[startByte + 2], 0, bitPos) & 0xFF);
        } else {
            ensureReadable(startByte, 2);
            return (buffer[startByte] & 0xFF) << 8
                    | (buffer[startByte + 1] & 0xFF);
        }
    }

    /**
     * @throws ByteParseException
     */
    public int readBits24(FieldDefs field) {
        assert field.getLength(this) == 24;
        return readBits24(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public int readBits24(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        if (n < 8) {
            ensureReadable(startByte, 4);
            return ((unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 16)
                    | (buffer[startByte + 1] & 0xFF) << (8 + bitPos)
                    | (buffer[startByte + 2] & 0xFF) << bitPos
                    | (unsafeReadMsb(buffer[startByte + 3], 0, bitPos) & 0xFF);
        } else {
            ensureReadable(startByte, 3);
            return (buffer[startByte] & 0xFF) << 16
                    | (buffer[startByte + 1] & 0xFF) << 8
                    | (buffer[startByte + 2] & 0xFF);
        }
    }

    /**
     * @throws ByteParseException
     */
    public long readBits36(FieldDefs field) {
        assert field.getLength(this) == 36;
        return readBits36(field.getOffset(this));
    }

    /**
     * @throws ByteParseException
     */
    public long readBits36(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos; // # bits to read

        if (n < 4) {
            ensureReadable(startByte, 6);
            return ((long) unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 28
                    | ((long) buffer[startByte + 1] & 0xFF) << (20 + bitPos)
                    | ((long) buffer[startByte + 2] & 0xFF) << (12 + bitPos)
                    | ((long) buffer[startByte + 3] & 0xFF) << (4 + bitPos)
                    | ((long) buffer[startByte + 4] & 0xFF) << (bitPos - 4)
                    | ((long) unsafeReadMsb(buffer[startByte + 5], 0, bitPos - 4) & 0xFF);
        } else {
            ensureReadable(startByte, 5);
            return ((long) unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 28
                    | ((long) buffer[startByte + 1] & 0xFF) << (20 + bitPos)
                    | ((long) buffer[startByte + 2] & 0xFF) << (12 + bitPos)
                    | ((long) buffer[startByte + 3] & 0xFF) << (4 + bitPos)
                    | ((long) unsafeReadMsb(buffer[startByte + 4], 0, 4 + bitPos) & 0xFF);
        }
    }

    /**
     * @throws ByteParseException
     */
    public long readBits64(int offset) {
        int startByte = offset >>> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos; // # bits to read

        if (n < 8) {
            ensureReadable(startByte, 9);
            return ((long) unsafeReadLsb(buffer[startByte], bitPos, n) & 0xFF) << 56
                    | ((long) buffer[startByte + 1] & 0xFF) << (48 + bitPos)
                    | ((long) buffer[startByte + 2] & 0xFF) << (40 + bitPos)
                    | ((long) buffer[startByte + 3] & 0xFF) << (32 + bitPos)
                    | ((long) buffer[startByte + 4] & 0xFF) << (24 + bitPos)
                    | ((long) buffer[startByte + 5] & 0xFF) << (16 + bitPos)
                    | ((long) buffer[startByte + 6] & 0xFF) << (8 + bitPos)
                    | ((long) buffer[startByte + 7] & 0xFF) << bitPos
                    | ((long) unsafeReadMsb(buffer[startByte + 8], 0, bitPos) & 0xFF);
        } else {
            ensureReadable(startByte, 8);
            return ((long) buffer[startByte] & 0xFF) << 56
                    | ((long) buffer[startByte + 1] & 0xFF) << 48
                    | ((long) buffer[startByte + 2] & 0xFF) << 40
                    | ((long) buffer[startByte + 3] & 0xFF) << 32
                    | ((long) buffer[startByte + 4] & 0xFF) << 24
                    | ((long) buffer[startByte + 5] & 0xFF) << 16
                    | ((long) buffer[startByte + 6] & 0xFF) << 8
                    | ((long) buffer[startByte + 7] & 0xFF);
        }
    }

    /**
     * @throws ByteParseException
     */
    public BitSet readBitSet(int offset, int length) {
        return readBitSet(offset, length, 0);
    }

    /**
     * @throws ByteParseException
     */
    public BitSet readBitSet(int offset, int length, int resultShift) {
        final BitSet bs = new BitSet(length);
        int i = 0;
        while (i < length) {
            final int remaining = length - i;
            final int readIndex = offset + i;
            final int writeIndex = resultShift + i;
            if (remaining >= 64) {
                fillBitSetWithContent(bs, readBits64(readIndex), 64, writeIndex);
                i += 64;
            } else if (remaining >= 36) {
                fillBitSetWithContent(bs, readBits36(readIndex), 36, writeIndex);
                i += 36;
            } else if (remaining >= 24) {
                fillBitSetWithContent(bs, readBits24(readIndex), 24, writeIndex);
                i += 24;
            } else if (remaining >= 16) {
                fillBitSetWithContent(bs, readBits16(readIndex), 16, writeIndex);
                i += 16;
            } else if (remaining >= 12) {
                fillBitSetWithContent(bs, readBits12(readIndex), 12, writeIndex);
                i += 12;
            } else if (remaining >= 8) {
                fillBitSetWithContent(bs, readByteBits(readIndex, 8), 8, writeIndex);
                i += 8;
            } else {
                fillBitSetWithContent(bs, readByteBits(readIndex, remaining), remaining, writeIndex);
                i += remaining;
            }
        }
        return bs;
    }

    private void fillBitSetWithContent(BitSet bs, long content, int size, int offset) {
        for (int j = 0; j < size; j++) {
            if (((content >>> (size - 1 - j)) & 1) == 1) {
                bs.set(offset + j);
            }
        }
    }

    private byte unsafeReadMsb(byte from, int offset, int length) {
        return length == 0 ? 0 : (byte) ((from >>> ((8 - length) - offset)) & ((1 << length) - 1));
    }

    private byte unsafeReadLsb(byte from, int offset, int length) {
        return length == 0 ? from : (byte) ((from & ((1 << length) - 1)) << offset);
    }
}
