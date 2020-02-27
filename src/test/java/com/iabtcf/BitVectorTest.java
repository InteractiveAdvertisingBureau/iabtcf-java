package com.iabtcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.iabtcf.v2.BitVectorTCModelV2;

@Deprecated
public class BitVectorTest {

    @Test
    public void testCanReadSmallInt() {
        String bitString = "0000 1000 0000 0001";
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals(2, bitVector.readBits6(0));
    }

    @Test
    public void tesCanReadInstantFromDeciSecond() {
        String bitString = "1 001110101101110010100111000111000100 1";
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals(
                Instant.parse("2020-01-26T18:19:25.200Z"), Instant.ofEpochMilli(bitVector.readBits36(1) * 100));
    }

    @Test
    public void tesCanReadEpochInstantFromDeciSecond() {
        String bitString = Stream.generate(() -> "0").limit(36).collect(Collectors.joining());
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals(Instant.EPOCH, Instant.ofEpochMilli(bitVector.readBits36(1) * 100));
    }

    @Test
    public void testCanReadBit() {
        String bitString = "10101010";
        ByteBitVector bitVector = fromBitString(bitString);
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
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals("AB", BitVectorTCModelV2.readStr2(bitVector, 0));
    }

    private ByteBitVector fromBitString(String bits) {
        ByteBitVector bitVector = new ByteBitVector(fromString(bits));
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
