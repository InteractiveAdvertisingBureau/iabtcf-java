package com.iabtcf.decoder;

import org.junit.Test;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitVectorTest {

    @Test
    public void testReadSmall() {
        String bitString = "0000 1000 0000 0001";
        BitVector bitVector = vectorFromBitString(bitString);
        assertEquals(0, bitVector.readInt(() -> 4));
        assertEquals(128, bitVector.readInt(() -> 8));
        assertEquals(1, bitVector.readInt(() -> 4));
    }

    @Test
    public void testReadInteger() {
        String bitString = "0111 1111 1111 1111 1111 1111 1111 1111";
        BitVector bitVector = vectorFromBitString(bitString);
        assertEquals(Integer.MAX_VALUE, bitVector.readInt(() -> 32));
    }

    @Test
    public void testReadLong() {
        String bitString = "0111 1111 1111 1111 1111 1111 1111 1111" + "1111 1111 1111 1111 1111 1111 1111 1111";
        BitVector bitVector = vectorFromBitString(bitString);
        assertEquals(Long.MAX_VALUE, bitVector.readLong(64));
    }

    @Test
    public void tesReadInstant() {
        String bitString = "001110101101110010100111000111000100";
        BitVector bitVector = vectorFromBitString(bitString);
        assertEquals(Instant.parse("2020-01-26T18:19:25.200Z"), bitVector.readInstantFromDeciSecond(() -> 36));
    }

    @Test
    public void tesReadEpochInstant() {
        String bitString = Stream.generate(() -> "0").limit(36).collect(Collectors.joining());
        BitVector bitVector = vectorFromBitString(bitString);
        assertEquals(Instant.EPOCH, bitVector.readInstantFromDeciSecond(() -> 36));
    }

    @Test
    public void testReadBit() {
        String bitString = "10101010";
        BitVector bitVector = vectorFromBitString(bitString);
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                assertTrue(bitVector.readBit());
            } else {
                assertFalse(bitVector.readBit());
            }
        }
    }

    @Test
    public void testReadSixBitString() {
        String bitString = "000000 000001";
        BitVector bitVector = vectorFromBitString(bitString);
        assertEquals("AB", bitVector.readString(() -> 12));
    }

    private BitVector vectorFromBitString(String bits) {
        return BitVector.from(bytesFromBitString(bits));
    }

    private byte[] bytesFromBitString(String bitString) {
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
            sub = sub.length() == 8
                    ? sub
                    : sub + Stream.generate(() -> "0")
                                  .limit(8 - sub.length())
                                  .collect(Collectors.joining());
            bytes[j++] = (byte) (Integer.parseInt(sub, 2));
        }
        return bytes;
    }
}
