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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Base64;

import org.junit.Test;

import com.iabtcf.ByteBitVector;
import com.iabtcf.FieldDefs;
import com.iabtcf.decoder.TCString;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.ByteBitVectorUtils;

public class BitWriterTest {
    @Test
    public void testEncode16() {
        BitWriter bw = new BitWriter();
        bw.write(32, 16);
        byte[] b = bw.toByteArray();
        ByteBitVector bbv = new ByteBitVector(b);
        int value = bbv.readBits16(0);
        assertEquals(32, value);
    }

    @Test
    public void testWrite64() {
        BitWriter bv = new BitWriter();
        bv.write(3, 1);
        bv.write(1L << 3294832095852432L, Long.SIZE);
        ByteBitVector bbv = new ByteBitVector(bv.toByteArray());
        long value = 0;
        value |= (long) bbv.readBits16(1) << 48;
        value |= (long) bbv.readBits16(1 + 16) << 32;
        value |= (long) bbv.readBits16(1 + 32) << 16;
        value |= bbv.readBits16(1 + 48);

        assertEquals(1L << 3294832095852432L, value);

        bv = new BitWriter();
        bv.write(1L << 3294832095852432L, Long.SIZE);
        bbv = new ByteBitVector(bv.toByteArray());
        value = 0;
        value |= (long) bbv.readBits16(0) << 48;
        value |= (long) bbv.readBits16(16) << 32;
        value |= (long) bbv.readBits16(32) << 16;
        value |= bbv.readBits16(48);

        assertEquals(1L << 3294832095852432L, value);
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

        ByteBitVector reader = new ByteBitVector(bitWriter.toByteArray());
        long l = reader.readBits36(0);
        long actual = now.toEpochMilli() / 100;
        assertEquals(l, actual);
    }

    @Test
    public void testStr() {
        BitWriter bitWriter = new BitWriter();
        String str = "DE";
        bitWriter.write(str);

        ByteBitVector reader = new ByteBitVector(bitWriter.toByteArray());
        String actual = ByteBitVectorUtils.readStr2(reader, 0);
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
        bw.write(BitSetIntIterable.of(1, 2, 3, 4, 5, 15, 24), FieldDefs.V1_PURPOSES_ALLOW);
        bw.write(32, FieldDefs.V1_VENDOR_MAX_VENDOR_ID);
        bw.write(false);
        bw.write(BitSetIntIterable.of(1, 25, 30), 32);

        byte[] rv = bw.toByteArray();
        String str = Base64.getUrlEncoder().encodeToString(rv);

        assertEquals("BOOzQoAOOzQoAAPAFSENCW-AIBACBAAABCA=", str);
    }

    @Test
    public void testSimpleEncode() {
        BitWriter bw = new BitWriter();
        bw.write(2, 6);
        bw.write(Instant.parse("2020-01-26T17:01:00Z").toEpochMilli() / 100, 36);
        bw.write(Instant.parse("2021-02-02T17:01:00Z").toEpochMilli() / 100, 36);
        bw.write(675, FieldDefs.CORE_CMP_ID);
        bw.write(2, FieldDefs.CORE_CMP_VERSION);
        bw.write(1, FieldDefs.CORE_CONSENT_SCREEN);
        bw.write("EN", FieldDefs.CORE_CONSENT_LANGUAGE);
        bw.write(15, FieldDefs.CORE_VENDOR_LIST_VERSION);
        bw.write(2, FieldDefs.CORE_TCF_POLICY_VERSION);
        bw.write(false, FieldDefs.CORE_IS_SERVICE_SPECIFIC);
        bw.write(false, FieldDefs.CORE_USE_NON_STANDARD_STOCKS);
        bw.write(BitSetIntIterable.of(1), FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS);
        bw.write(BitSetIntIterable.of(2, 10), FieldDefs.CORE_PURPOSES_CONSENT);
        bw.write(BitSetIntIterable.of(2, 9), FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY);
        bw.write(true, FieldDefs.CORE_PURPOSE_ONE_TREATMENT);
        bw.write("AA", FieldDefs.CORE_PUBLISHER_CC);

        // skip vendor consent
        bw.write(0, 16);
        bw.write(false);

        // skip vendor legitimate interests
        bw.write(0, 16);
        bw.write(false);

        // skip publisher restrictions section
        bw.write(0, 12);
        bw.write(false);

        byte[] rv = bw.toByteArray();
        String str = Base64.getUrlEncoder().encodeToString(rv);

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
        String str = Base64.getUrlEncoder().encodeToString(bw1.toByteArray());
        tcModel = TCString.decode(str);

        assertEquals(2, tcModel.getVersion());
        assertEquals(Instant.parse("2020-01-26T17:01:00Z"), tcModel.getCreated());
        assertEquals(Instant.parse("2021-02-02T17:01:00Z"), tcModel.getLastUpdated());
    }
}
