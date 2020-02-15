package com.iabtcf;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class BitVectorTest {

    @Test
    public void testCanReadSmallInt() {
        String bitString = "0000 1000 0000 0001";
        BitVector bitVector = fromBitString(bitString);
        assertEquals(2, bitVector.readUnsignedInt(0, 6));
        assertEquals(8, bitVector.readUnsignedInt(4, 4));
        assertEquals(128, bitVector.readUnsignedInt(4, 8));
    }

    @Test
    public void testCanReadBigIntegers() {
        String bitString = "0111 1111 1111 1111 1111 1111 1111 1111";
        BitVector bitVector = fromBitString(bitString);
        assertEquals(Integer.MAX_VALUE, bitVector.readUnsignedInt(0, 32));
    }

    @Test
    public void testCanReadBigLongValues() {
        String bitString =
                "0111 1111 1111 1111 1111 1111 1111 1111" + "1111 1111 1111 1111 1111 1111 1111 1111";
        BitVector bitVector = fromBitString(bitString);
        assertEquals(Long.MAX_VALUE, bitVector.readUnsignedLong(0, 64));
    }

    @Test
    public void tesCanReadInstantFromDeciSecond() {
        String bitString = "1 001110101101110010100111000111000100 1";
        BitVector bitVector = fromBitString(bitString);
        assertEquals(
                Instant.parse("2020-01-26T18:19:25.200Z"), bitVector.instanceFromDeciSecond(1, 36));
    }

    @Test
    public void tesCanReadEpochInstantFromDeciSecond() {
        String bitString = Stream.generate(() -> "0").limit(36).collect(Collectors.joining());
        BitVector bitVector = fromBitString(bitString);
        assertEquals(Instant.EPOCH, bitVector.instanceFromDeciSecond(1, 36));
    }

    @Test
    public void testCanReadBit() {
        String bitString = "10101010";
        BitVector bitVector = fromBitString(bitString);
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                assertTrue(bitVector.readBit(i));
            } else {
                assertFalse(bitVector.readBit(i));
            }
        }
    }

    @Test
    public void testReadSixBitString() {
        String bitString = "000000 000001";
        BitVector bitVector = fromBitString(bitString);
        assertEquals("AB", bitVector.readStr(0, 12));
    }

    private BitVector fromBitString(String bits) {
        BitVector bitVector = BitVector.from(fromString(bits));
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
}
