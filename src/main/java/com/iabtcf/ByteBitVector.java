package com.iabtcf;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

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

    public boolean readBits1(int offset) {
        int startByte = offset >> 3;
        int bitPos = offset % 8;

        ensureReadable(startByte, 1);

        return ((buffer[startByte] >>> (7 - bitPos)) & 1) == 1;
    }

    public byte readBits2(int offset) {
        return readByteBits(offset, 2);
    }

    public byte readBits3(int offset) {
        return readByteBits(offset, 3);
    }

    public byte readBits6(int offset) {
        int startByte = offset >> 3;
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
     */
    private byte readByteBits(int offset, int nbits) {
        int startByte = offset >> 3;
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

    public BitSet readBitSet(int offset, int length) {
        // TODO(mk): can we read larger chunks at a time?
        BitSet bs = new BitSet(length);
        for (int i = 0; i < length; i++) {
            if (readBits1(offset + i)) {
                bs.set(i);
            }
        }
        return bs;
    }

    private byte unsafeReadMsb(byte from, int offset, int length) {
        return length == 0 ? 0 : (byte) ((from >>> ((8 - length) - offset)) & ((1 << length) - 1));
    }

    private byte unsafeReadLsb(byte from, int offset, int length) {
        return length == 0 ? from : (byte) ((from & ((1 << length) - 1)) << offset);
    }

    // exploring writing bits...
    public static void writeBits6(byte[] buffer, int offset, byte value) {
        int startByte = offset >> 3;
        int bitPos = offset % 8;
        int n = 8 - bitPos;

        value &= ((1 << 6) - 1);

        if (n < 6) {
            byte mask = (byte) ((1 << n) - 1);
            buffer[startByte] &= ~mask;
            buffer[startByte] |= (byte) (value >>> (6 - n));

            mask = (byte) (((1 << (6 - n)) - 1) << (8 - (6 - n) - 0));
            buffer[startByte + 1] &= ~mask;
            buffer[startByte + 1] |= (byte) ((value & ((1 << (6 - n)) - 1)) << (8 - (6 - n) - 0));
            return;
        } else {
            byte mask = (byte) (((1 << 6) - 1) << (8 - 6 - bitPos));
            buffer[startByte] &= ~mask;
            buffer[startByte] |= (byte) (value << (8 - 6 - bitPos));
        }
    }
}
