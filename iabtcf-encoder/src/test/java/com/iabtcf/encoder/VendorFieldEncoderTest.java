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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Test;

import com.iabtcf.decoder.TCString;
import com.iabtcf.utils.BitReader;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;

public class VendorFieldEncoderTest {
    private static final Random RAND = new Random(312L);

    private void testEncodeMaxVendorId(int maxVendorId, int expectedMaxVendorId) {
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .setMaxVendorId(maxVendorId)
            .add(1)
            .add(25, 30, 32);

        byte[] b = bfe.build().toByteArray();
        BitReader bbv = new BitReader(b);
        int value = bbv.readBits16(0);
        assertEquals(expectedMaxVendorId, value);
    }

    @Test
    public void testEncodeMaxVendorId() {
        for (int i = 32; i < 1L << 16 - 1; i++) {
            testEncodeMaxVendorId(i, i);
        }
    }

    @Test
    public void testEncodeMaxVendorIdSmallest() {
        for (int i = 0; i < 32; i++) {
            testEncodeMaxVendorId(i, 32);
        }
    }

    @Test
    public void testVendorEncode() {
        BitWriter bw = createV1BitString();
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .setMaxVendorId(32)
            .add(1)
            .add(25, 30, 30);

        bw.write(bfe.buildV1());

        byte[] rv = bw.toByteArray();
        String str = Base64.getUrlEncoder().encodeToString(rv);

        TCString tcModel = TCString.decode(str);
        assertTrue(tcModel.getVendorConsent().contains(1));
        assertTrue(tcModel.getVendorConsent().contains(25));
        assertTrue(tcModel.getVendorConsent().contains(30));

        assertEquals("BOOzQoAOOzQoAAPAFSENCW-AIBACBAAABCA=", str);
    }

    @Test
    public void testEncodeManyVendors() {
        int[] vendorIds = new int[] {1, 3, 12, 17, 31, 32, 54, 55, 66, 81};
        testEncodeManyVendors(0, vendorIds, false, true);
    }

    @Test
    public void testEncodeManyVendorsRangeRandom() {
        for (int i = 0; i < 500; i++) {
            testEncodeManyVendors(i, randomArray(RAND.nextInt(50) + 10, 1024), false, true);
        }
    }

    @Test
    public void testEncodeManyVendorsRandom() {
        for (int i = 0; i < 500; i++) {
            testEncodeManyVendors(i, randomArray(15, 273), false, true);
        }
    }

    @Test
    public void testEncodeManyVendorsRandomForceRange() {
        for (int i = 0; i < 500; i++) {
            testEncodeManyVendors(i, randomArray(15, 273), true, true);
        }
    }

    // TODO: make this test for V2
    private void testEncodeManyVendors(int iteration, int[] vendorIds, boolean emitRange, boolean v1) {
        BitWriter bw = createV1BitString();
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .setMaxVendorId(81)
            .emitRangeEncoding(emitRange)
            .add(vendorIds);

        bw.write(bfe.buildV1());

        byte[] rv = bw.toByteArray();
        String str = Base64.getUrlEncoder().encodeToString(rv);

        TCString tcModel = TCString.decode(str);
        IntIterable vc = tcModel.getVendorConsent();
        for (int i = 0; i < vendorIds.length; i++) {
            assertTrue(String.format("%d (%d): failed to find: %d", iteration, i, vendorIds[i]),
                    vc.contains(vendorIds[i]));
        }
    }

    private int[] randomArray(int length, int max) {
        int[] rv = new int[length];
        for (int i = 0; i < rv.length; i++) {
            rv[i] = 1 + RAND.nextInt(max - 1);
        }

        return rv;
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
    private BitWriter createV1BitString() {
        BitWriter bw = new BitWriter();
        bw.write(1, 6);
        bw.write(Instant.parse("2018-06-04T00:00:00Z"), FieldDefs.TIMESTAMP);
        bw.write(Instant.parse("2018-06-04T00:00:00Z"), FieldDefs.TIMESTAMP);
        bw.write(15, FieldDefs.CORE_CMP_ID);
        bw.write(5, FieldDefs.CORE_CMP_VERSION);
        bw.write(18, FieldDefs.CORE_CONSENT_SCREEN);
        bw.write("EN", FieldDefs.CORE_CONSENT_LANGUAGE);
        bw.write(150, FieldDefs.CORE_VENDOR_LIST_VERSION);
        bw.write(BitSetIntIterable.from(1, 2, 3, 4, 5, 15, 24), FieldDefs.V1_PURPOSES_ALLOW);

        return bw;
    }

    @Test
    public void testEncodeDefaultConsentV1() {
        BitWriter bw = createV1BitString();

        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .defaultConsent(true)
            .add(1)
            .add(512);

        bw.write(bfe.buildV1());

        byte[] rv = bw.toByteArray();
        String str = Base64.getUrlEncoder().encodeToString(rv);

        TCString tcModel = TCString.decode(str);
        IntIterable vc = tcModel.getVendorConsent();
        assertFalse(vc.contains(1));
        assertTrue(vc.contains(2));
        assertFalse(vc.contains(512));
        assertFalse(vc.contains(513));
    }

    @Test
    public void testForceRange() {
        BitWriter bw = createV1BitString();

        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .defaultConsent(true)
            .emitRangeEncoding(true)
            .setMaxVendorId(12)
            .add(1);

        bw.write(bfe.buildV1());

        byte[] rv = bw.toByteArray();
        String str = Base64.getUrlEncoder().encodeToString(rv);

        TCString tcModel = TCString.decode(str);
        IntIterable vc = tcModel.getVendorConsent();
        assertFalse(vc.contains(1));
        assertTrue(vc.contains(2));
        assertTrue(vc.contains(12));
        assertFalse(vc.contains(13));
    }

    @Test
    public void TestBuildV2() {
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .emitRangeEncoding(true)
            .setMaxVendorId(3)
            .add(1)
            .add(3);

        BitWriter bfeBits = bfe.build();
        BitReader br = new BitReader(bfeBits.toByteArray());
        int j = br.readBits16(0);
        assertEquals(3, j); // max vendor id
        assertEquals(true, br.readBits1(16)); // is a range
        assertEquals(2, br.readBits12(17)); // num entries
    }

    @Test
    public void testVendorsLengthEmpty() {
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .emitRangeEncoding(true)
            .setMaxVendorId(0);

        BitWriter bfeBits = bfe.build();
        BitReader br = new BitReader(bfeBits.toByteArray());
        int j = br.readBits16(0);
        assertEquals(0, j); // max vendor id
        assertEquals(false, br.readBits1(16)); // is a range
    }

    @Test
    public void testIterable() {
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .emitRangeEncoding(true)
            .setMaxVendorId(3)
            .add(BitSetIntIterable.from(1, 3));

        BitWriter bfeBits = bfe.build();
        BitReader br = new BitReader(bfeBits.toByteArray());
        int j = br.readBits16(0);
        assertEquals(3, j); // max vendor id
        assertEquals(true, br.readBits1(16)); // is a range
        assertEquals(2, br.readBits12(17)); // num entries
    }

    @Test
    public void testIntIterable() {
        VendorFieldEncoder bfe = new VendorFieldEncoder()
            .emitRangeEncoding(true)
            .setMaxVendorId(3)
            .add(BitSetIntIterable.from(1, 3));

        BitWriter bfeBits = bfe.build();
        BitReader br = new BitReader(bfeBits.toByteArray());
        int j = br.readBits16(0);
        assertEquals(3, j); // max vendor id
        assertEquals(true, br.readBits1(16)); // is a range
        assertEquals(2, br.readBits12(17)); // num entries
        assertFalse(br.readBits1(17 + 12)); // is a range
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegtaiveVendorId() {
        new VendorFieldEncoder()
            .emitRangeEncoding(true)
            .setMaxVendorId(3)
            .add(0);
    }

    @Test
    public void testPrototype() {
        VendorFieldEncoder proto = new VendorFieldEncoder()
            .defaultConsent(true)
            .emitRangeEncoding(true)
            .setMaxVendorId(3)
            .add(BitSetIntIterable.from(1, 3).toStream().boxed().collect(Collectors.toList()));

        VendorFieldEncoder bfe = new VendorFieldEncoder(proto);
        bfe.add(2);

        BitWriter bfeBits = bfe.build();
        BitReader br = new BitReader(bfeBits.toByteArray());
        int j = br.readBits16(0);
        assertEquals(3, j); // max vendor id
        assertEquals(true, br.readBits1(16)); // is a range
        assertEquals(1, br.readBits12(17)); // num entries
        assertTrue(br.readBits1(29)); // is a range
        assertEquals(1, br.readBits16(30));
        assertEquals(3, br.readBits16(46));
    }

    @Test
    public void testPrototypeEmptyVendors() {
        VendorFieldEncoder proto = new VendorFieldEncoder()
            .defaultConsent(true)
            .emitRangeEncoding(true)
            .setMaxVendorId(0);

        VendorFieldEncoder bfe = new VendorFieldEncoder(proto);

        BitWriter bfeBits = bfe.build();
        BitReader br = new BitReader(bfeBits.toByteArray());
        int j = br.readBits16(0);
        assertEquals(0, j); // max vendor id
        assertEquals(false, br.readBits1(16)); // is a range
    }
}
