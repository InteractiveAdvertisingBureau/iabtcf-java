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

import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;

import com.iabtcf.decoder.DecoderOption;
import com.iabtcf.decoder.TCString;
import com.iabtcf.encoder.exceptions.ValueOverflowException;
import com.iabtcf.utils.BitReader;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;

public class BitWriterTest {
    @Test
    public void testWriteBoolean() {
        BitWriter bw = new BitWriter(0);
        bw.write(true);
        byte[] b = bw.toByteArray();
        assertEquals(1 << 7, b[0] & 0xFF);
        assertEquals(1, b.length);
    }

    @Test
    public void testWriteASCII() {
        String str = new String("A");
        BitWriter bw = new BitWriter();
        try {
            bw.write(str);
        } catch (AssertionError e) {
            return;
        }

        assertEquals(str.getBytes()[0] - 'A', bw.toByteArray()[0]);
    }

    @Test
    public void testLowerCase() {
        String str = new String("aA");
        BitWriter bw = new BitWriter();
        try {
            bw.write(str);
        } catch (AssertionError e) {
            return;
        }

        assertEquals(str.toUpperCase().getBytes()[0] - 'A', bw.toByteArray()[0]);
        assertEquals(str.toUpperCase().getBytes()[1] - 'A', bw.toByteArray()[1]);
    }

    @Test
    public void testWriteUTF16() {
        String str = new String(Character.toChars(0x0510));
        BitWriter bw = new BitWriter();
        try {
            bw.write(str);
        } catch (AssertionError e) {
            return;
        }

        assertNotEquals(str.getBytes()[0], bw.toByteArray()[0]);
    }

    @Test
    public void testEncodeIntIterableSmallerSize() {
        BitSetIntIterable bsii = BitSetIntIterable.from(1, 15, 512);
        BitWriter bw = new BitWriter();
        bw.write(bsii, 12);
        byte[] b = bw.toByteArray();
        assertEquals(1 << 7, b[0] & 0xFF);
        assertEquals(2, b.length);
    }

    @Test
    public void testEncodeIntIterableEqualSize() {
        BitSetIntIterable bsii = BitSetIntIterable.from(1, 15, 512);
        BitWriter bw = new BitWriter();
        bw.write(bsii, 15);
        byte[] b = bw.toByteArray();
        assertEquals(1 << 7, b[0] & 0xFF);
        assertEquals(1 << 1, b[1] & 0xFF);
        assertEquals(2, b.length);
    }

    @Test
    public void testEncodeIntIterableLargerSize() {
        BitSetIntIterable bsii = BitSetIntIterable.from(1, 15, 512);
        BitWriter bw = new BitWriter();
        bw.write(bsii, 1024);
        byte[] b = bw.toByteArray();
        assertEquals(1 << 7, b[0] & 0xFF);
        assertEquals(1 << 1, b[1] & 0xFF);
        assertEquals(1024 / 8, b.length);
        assertEquals(1, b[(512 / 8) - 1]);
    }

    @Test
    public void testBitSetRespectsPadding() {
        BitSetIntIterable bsii = BitSetIntIterable.from(1, 15, 512);
        BitWriter bw = new BitWriter();
        bw.write(bsii, 1024);
        byte[] b = bw.toByteArray();
        assertEquals(1 << 7, b[0] & 0xFF);
        assertEquals(1 << 1, b[1] & 0xFF);
        assertEquals(1024 / 8, b.length);
        assertEquals(1, b[(512 / 8) - 1]);

        // the next bit is at position 1025
        bw.write(true);
        b = bw.toByteArray();
        assertEquals(1 << 7, b[(1024 / 8)] & 0xFF);
    }

    @Test
    public void testCountsForPadding() {
        BitSetIntIterable bsii = BitSetIntIterable.from(1, 15, 512);
        BitWriter bw = new BitWriter(1024 + 512);
        bw.write(bsii, 784);
        bw.write(bsii, 128);
        byte[] b = bw.toByteArray();
        assertEquals((1024 + 512) / 8, b.length);
    }

    @Test
    public void testEncode16() {
        BitWriter bw = new BitWriter();
        bw.write(32, 16);
        byte[] b = bw.toByteArray();
        BitReader bbv = new BitReader(b);
        int value = bbv.readBits16(0);
        assertEquals(32, value);
    }

    @Test
    public void testWrite64() {
        BitWriter bv = new BitWriter();
        bv.write(3, 1);
        bv.write(1L << 3294832095852432L, Long.SIZE);
        BitReader bbv = new BitReader(bv.toByteArray());
        long value = 0;
        value |= (long) bbv.readBits16(1) << 48;
        value |= (long) bbv.readBits16(1 + 16) << 32;
        value |= (long) bbv.readBits16(1 + 32) << 16;
        value |= bbv.readBits16(1 + 48);

        assertEquals(1L << 3294832095852432L, value);

        bv = new BitWriter();
        bv.write(1L << 3294832095852432L, Long.SIZE);
        bbv = new BitReader(bv.toByteArray());
        value = 0;
        value |= (long) bbv.readBits16(0) << 48;
        value |= (long) bbv.readBits16(16) << 32;
        value |= (long) bbv.readBits16(32) << 16;
        value |= bbv.readBits16(48);

        assertEquals(1L << 3294832095852432L, value);
    }

    @Test
    public void testWrite64Twice() {
        BitWriter bv = new BitWriter();
        bv.write(0xCCCCCCCCCCCCCCCCL, Long.SIZE);
        bv.write(0xEEEEEEEEEEEEEEEEL, Long.SIZE);

        BitReader bbv = new BitReader(bv.toByteArray());

        long value = readBits64(bbv, 0);
        assertEquals(0xCCCCCCCCCCCCCCCCL, value);

        value = readBits64(bbv, 64);
        assertEquals(0xEEEEEEEEEEEEEEEEL, value);
    }

    @Test
    public void testWrite64Twice_1() {
        BitWriter bv = new BitWriter();
        for(int i = 0; i < 64; i += 4) {
            bv.write(0xC, 4);
        }
        for(int i = 0; i < 64; i += 4) {
            bv.write(0xE, 4);
        }

        BitReader bbv = new BitReader(bv.toByteArray());
        long value = readBits64(bbv, 0);

        assertEquals(0xCCCCCCCCCCCCCCCCL, value);

        value = readBits64(bbv, 64);
        assertEquals(0xEEEEEEEEEEEEEEEEL, value);
    }

    private long readBits64(BitReader bbv, int offset) {
        long value = 0L;
        value |= (long) bbv.readBits16(0 + offset) << 48L;
        value |= (long) bbv.readBits16(16 + offset) << 32L;
        value |= (long) bbv.readBits16(32 + offset) << 16L;
        value |= bbv.readBits16(48 + offset);
        return value;
    }

    @Test
    public void testWriteSingleByte() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(1, 6);
        bitWriter.write(3, 2);
        byte[] expected = new byte[] {0b00000111};
        assertArrayEquals(expected, bitWriter.toByteArray());
    }

    @Test
    public void testWriteToMultipleBytes() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(1, 16);
        byte[] expected = new byte[] {0, 1};
        byte[] array = bitWriter.toByteArray();
        assertArrayEquals(expected, array);
    }

    @Test
    public void testWriteInstant() {
        Instant now = Instant.now();
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(now);

        BitReader reader = new BitReader(bitWriter.toByteArray());
        long l = reader.readBits36(0);
        long actual = now.toEpochMilli() / 100;
        assertEquals(l, actual);
    }

    @Test
    public void testStr() {
        BitWriter bitWriter = new BitWriter();
        String str = "DE";
        bitWriter.write(str);

        BitReader reader = new BitReader(bitWriter.toByteArray());
        String actual = reader.readStr2(0);
        assertEquals(str, actual);
    }

    @Test
    public void shouldIncreaseBufferCapacity() {
        int requestedLength = 1 << 11;
        BitWriter bitWriter = new BitWriter(requestedLength);
        bitWriter.write(0, 0);
        int length = bitWriter.toByteArray().length;
        assertTrue(length >= requestedLength / 8);
    }

    @Test
    public void testWriteSingleBit() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(true);
        byte[] array = bitWriter.toByteArray();
        assertEquals(1, array.length);
        assertEquals(array[0] & 0xFF, 0b10000000);
    }

    /**
     * BOOzQoAOOzQoAAPAFSENCW-AIBACBAAABCA=
     *
     * @formatter:off
     *
     * {
     *  "cookieVersion": 1,
     *  "created": "2018-06-04T00:00:00.000Z",
     *  "lastUpdated": "2018-06-04T00:00:00.000Z",
     *  "cmpId": 15,
     *  "cmpVersion": 5,
     *  "consentScreen": 18,
     *  "consentLanguage": "EN",
     *  "vendorListVersion": 150,
     *  "purposeIdBitString": "111110000000001000000001",
     *  "maxVendorId": 32,
     *  "isRange": false,
     *  "vendorIdBitString": "10000000000000000000000010000100"
     * }
     * @formatter:on
     */
    @Test
    public void testVendorEncode() {
        BitWriter bw = new BitWriter();
        bw.write(1, 6);
        bw.write(Instant.parse("2018-06-04T00:00:00Z").toEpochMilli() / 100, 36);
        bw.write(Instant.parse("2018-06-04T00:00:00Z").toEpochMilli() / 100, 36);
        bw.write(15, FieldDefs.CORE_CMP_ID);
        bw.write(5, FieldDefs.CORE_CMP_VERSION);
        bw.write(18, FieldDefs.CORE_CONSENT_SCREEN);
        bw.write("EN", FieldDefs.CORE_CONSENT_LANGUAGE);
        bw.write(150, FieldDefs.CORE_VENDOR_LIST_VERSION);
        bw.write(BitSetIntIterable.from(1, 2, 3, 4, 5, 15, 24), FieldDefs.V1_PURPOSES_ALLOW);
        bw.write(32, FieldDefs.V1_VENDOR_MAX_VENDOR_ID);
        bw.write(false);
        bw.write(BitSetIntIterable.from(1, 25, 30), 32);

        String str = bw.toBase64();

        assertEquals("BOOzQoAOOzQoAAPAFSENCW-AIBACBAAABCA", str);
    }

    @Test
    public void testSimpleEncode() {
        BitWriter bw = new BitWriter();
        bw.write(2, 6);
        bw.write(Instant.parse("2020-01-26T17:01:00Z").toEpochMilli() / 100, 36);
        bw.write(Instant.parse("2021-02-02T17:01:00Z"), FieldDefs.CORE_LAST_UPDATED);
        bw.write(675, FieldDefs.CORE_CMP_ID);
        bw.write(2, FieldDefs.CORE_CMP_VERSION);
        bw.write(1, FieldDefs.CORE_CONSENT_SCREEN);

        BitWriter bwA = new BitWriter();
        bwA.write("EN", FieldDefs.CORE_CONSENT_LANGUAGE);
        bwA.write(15, FieldDefs.CORE_VENDOR_LIST_VERSION);
        bwA.write(2, FieldDefs.CORE_TCF_POLICY_VERSION);
        bwA.write(false, FieldDefs.CORE_IS_SERVICE_SPECIFIC);

        BitWriter bwB = new BitWriter();
        bwB.write(false, FieldDefs.CORE_USE_NON_STANDARD_STOCKS);
        bwB.write(BitSetIntIterable.from(1), FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS);
        bwB.write(BitSetIntIterable.from(2, 10), FieldDefs.CORE_PURPOSES_CONSENT);
        bwB.write(BitSetIntIterable.from(2, 9), FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY);

        BitWriter bwC = new BitWriter(
                FieldDefs.CORE_PURPOSE_ONE_TREATMENT.getLength() + FieldDefs.CORE_PUBLISHER_CC.getLength());
        bwC.write(true, FieldDefs.CORE_PURPOSE_ONE_TREATMENT);
        bwC.write("AA", FieldDefs.CORE_PUBLISHER_CC);

        bwB.write(bwC);

        bw.write(bwA);
        bw.write(bwB);

        // skip vendor consent
        bw.write(0, 16);
        bw.write(false);

        // skip vendor legitimate interests
        bw.write(0, 16);
        bw.write(false);

        // skip publisher restrictions section
        bw.write(0, 12);
        bw.write(false);

        String str = bw.toBase64();

        assertEquals("COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA", str);

        TCString tcModel = TCString.decode(str);
        assertEquals(2, tcModel.getVersion());
        assertEquals(Instant.parse("2020-01-26T17:01:00Z"), tcModel.getCreated());
        assertEquals(Instant.parse("2021-02-02T17:01:00Z"), tcModel.getLastUpdated());
        assertEquals(675, tcModel.getCmpId());
        assertEquals(2, tcModel.getCmpVersion());
        assertEquals(1, tcModel.getConsentScreen());
        assertEquals("EN", tcModel.getConsentLanguage());
        assertEquals(15, tcModel.getVendorListVersion());
        assertEquals(2, tcModel.getTcfPolicyVersion());
        assertFalse(tcModel.isServiceSpecific());
        assertFalse(tcModel.getUseNonStandardStacks());
        assertThat(tcModel.getSpecialFeatureOptIns(), matchInts(1));
        assertThat(tcModel.getPurposesConsent(), matchInts(2, 10));
        assertThat(tcModel.getPurposesLITransparency(), matchInts(2, 9));
        assertTrue(tcModel.getPurposeOneTreatment());
        assertEquals("AA", tcModel.getPublisherCC());
    }

    @Test
    public void testAppendBitWriters() {
        // setup expected
        String consentString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        TCString tcModel = TCString.decode(consentString);
        assertEquals(2, tcModel.getVersion());
        assertEquals(Instant.parse("2020-01-26T17:01:00Z"), tcModel.getCreated());
        assertEquals(Instant.parse("2021-02-02T17:01:00Z"), tcModel.getLastUpdated());

        // code under test
        BitWriter bw1 = new BitWriter();
        BitWriter bw2 = new BitWriter();
        BitWriter bw3 = new BitWriter();

        bw1.write(2, 6);
        bw2.write(Instant.parse("2020-01-26T17:01:00Z").toEpochMilli() / 100, 36);
        bw3.write(Instant.parse("2021-02-02T17:01:00Z").toEpochMilli() / 100, 36);

        // append bw2 and bw3
        bw1.write(bw2);
        bw1.write(bw3);

        // verify
        String str = bw1.toBase64();
        tcModel = TCString.decode(str, DecoderOption.LAZY);

        assertEquals(2, tcModel.getVersion());
        assertEquals(Instant.parse("2020-01-26T17:01:00Z"), tcModel.getCreated());
        assertEquals(Instant.parse("2021-02-02T17:01:00Z"), tcModel.getLastUpdated());
    }

    @Test
    public void testLength() {
        BitWriter bw = new BitWriter(0);
        assertEquals(0, bw.length());
        bw.write(true);
        assertEquals(1, bw.length());
        bw.write("hello");
        assertEquals(1 + 5 * 6, bw.length());
    }

    @Test
    public void testLengthPrecisionSmaller() {
        BitWriter bw = new BitWriter(28);
        assertEquals(28, bw.length());
        bw.write(true);
        assertEquals(28, bw.length());
        bw.write("hello");
        assertEquals(1 + 5 * 6, bw.length());
    }

    @Test
    public void testLengthPrecisionSmaller1() {
        BitWriter bw = new BitWriter(32);
        assertEquals(32, bw.length());
        bw.write(true);
        assertEquals(32, bw.length());
        bw.write("hello");
        assertEquals(32, bw.length());
    }

    @Test
    public void testLengthPrecision() {
        BitWriter bw = new BitWriter(128);
        assertEquals(128, bw.length());
        bw.write(true);
        assertEquals(128, bw.length());
        bw.write("hello");
        assertEquals(128, bw.length());
    }

    @Test(expected = ValueOverflowException.class)
    public void testWriteV() {
        BitWriter bw = new BitWriter(128);
        bw.writeV(64, FieldDefs.CHAR);
    }
}
