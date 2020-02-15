package com.iabtcf;

import java.time.Instant;
import java.util.BitSet;

public class BitVector {

    private final BitSet bitSet;

    private BitVector(byte[] bytes) {
        this.bitSet = fromBigEndianOrderedByteArray(bytes);
    }

    public static BitVector from(byte[] bytes) {
        return new BitVector(bytes);
    }

    /**
     * creates a BitSet from byteArray in big-endian order
     *
     * @param bytes
     * @return
     */
    private BitSet fromBigEndianOrderedByteArray(byte[] bytes) {
        BitSet bits = new BitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            for (int index = (i + 1) * 8; b != 0; index--) {
                bits.set(index - 1, (b & 1) > 0);
                b >>= 1;
            }
        }
        return bits;
    }

    public int readUnsignedInt(int offset, int length) {
        return (int) readUnsignedLong(offset, length);
    }

    public long readUnsignedLong(int offset, int length) {
        long num = 0L;
        int bitIndex = length - 1;
        for (int i = 0; i < length; i++) {
            if (bitSet.get(offset + i)) {
                num |= 1L << bitIndex;
            }
            bitIndex--;
        }
        return num;
    }

    public Instant instanceFromDeciSecond(int offset, int length) {
        long epochDeci = readUnsignedLong(offset, length) * 100;
        return Instant.ofEpochMilli(epochDeci);
    }

    public String readStr(int offset, int length) {
        StringBuilder sb = new StringBuilder(length / 6);
        for (int i = 0; i < length; i += 6) {
            char c = (char) (readUnsignedInt(offset + i, 6) + 'A');
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean readBit(int offset) {
        return bitSet.get(offset);
    }
}
