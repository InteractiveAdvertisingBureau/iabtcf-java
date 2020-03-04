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

import com.iabtcf.v2.Field;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ByteBitVector {
    private byte[] buffer;
    private int isrpos;
    private final InputStream is;

    public ByteBitVector(InputStream is) {
        this.buffer = new byte[4096];
        this.is = is;
        this.isrpos = 0;
    }

    public ByteBitVector(byte[] buffer) {
        this.buffer = buffer;
        this.isrpos = buffer.length;
        this.is = null;
    }

    private void ensureCapacity(int length) {
        if (buffer.length >= length) {
            return;
        }

        byte[] b = new byte[length * 2];
        System.arraycopy(buffer, 0, b, 0, buffer.length);
        buffer = b;
    }

    private boolean ensureReadable(int offset, int length) {
        int rem = length;
        int tlength = offset + length;
        int n;

        if (tlength <= isrpos) {
            return true;
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
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean readBit(int offset) {
        int startByte = offset >> 3;
        int bitPos = offset % 8;

        ensureReadable(startByte, 1);

        return ((buffer[startByte] >>> (7 - bitPos)) & 1) == 1;
    }

    public boolean readBit(Field field) {
        return readBit(field.getOffset());
    }

    public byte readByte(Field field) {
        return readByte(field.getOffset(), field.getLength());
    }

    /**
     * When length <= 8
     */
    public byte readByte(int offset, int length) {
        int startByte = offset >> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        if (n < length) {
            ensureReadable(startByte, 2);
            return (byte) (unsafeReadLsb(buffer[startByte], length - n, n)
                    | unsafeReadMsb(buffer[startByte + 1], 0, length - n));
        } else {
            ensureReadable(startByte, 1);
            return unsafeReadMsb(buffer[startByte], bitPos, length);
        }
    }

    public int readBits12(int offset) {
        int startByte = offset >> 3;
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

    public int readBits16(int offset) {
        int startByte = offset >> 3;
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

    public int readBits24(int offset) {
        int startByte = offset >> 3;
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

    public long readBits36(int offset) {
        int startByte = offset >> 3;
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

    public Set<Integer> readSet(final Field field) {
        return readSet(field.getOffset(), field.getLength());
    }

    public Set<Integer> readSet(final int offset, final int length) {
        final Set<Integer> set = new HashSet<>();
        for (int i = 0; i < length; i++) {
            if (readBit(offset + i)) {
                set.add(i + 1);
            }
        }
        return set;
    }

    private byte unsafeReadMsb(byte from, int offset, int length) {
        return length == 0 ? 0 : (byte) ((from >>> ((8 - length) - offset)) & ((1 << length) - 1));
    }

    private byte unsafeReadLsb(byte from, int offset, int length) {
        return length == 0 ? from : (byte) ((from & ((1 << length) - 1)) << offset);
    }
}
