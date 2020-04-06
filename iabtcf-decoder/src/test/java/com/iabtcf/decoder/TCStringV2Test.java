package com.iabtcf.decoder;

/*-
 * #%L
 * IAB TCF Java Decoder Library
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.junit.Test;

import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.v2.PublisherRestriction;
import com.iabtcf.v2.RestrictionType;

public class TCStringV2Test {
    private static TCString parse(String consentString) {
        TCString model = TCString.decode(consentString);
        assertTrue(model instanceof TCStringV2);
        assertEquals(2, model.getVersion());

        return model;
    }

    static String base64FromBitString(String str) {
        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < str.length();) {
            String s = "";
            for (int j = 0; j < 8 && i < str.length(); j++) {
                s += str.charAt(i);
                i++;
            }
            byteList.add((byte) Integer.parseInt(s, 2));
        }
        byte[] bytes = new byte[byteList.size()];
        int i = 0;
        for (Byte aByte : byteList) {
            bytes[i++] = aByte;
        }
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    /**
     * the string was created here https://www.iabtcf.com/#/encode
     */
    @Test
    public void testCanConstructTCModelFromBase64String() {
        TCString tcModel = parse("COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA");

        assertEquals(2, tcModel.getVersion());
        assertEquals(Instant.parse("2020-01-26T17:01:00Z"), tcModel.getCreated());
        assertEquals(Instant.parse("2021-02-02T17:01:00Z"), tcModel.getLastUpdated());
        assertEquals(675, tcModel.getCmpId());
        assertEquals(2, tcModel.getCmpVersion());
        assertEquals(1, tcModel.getConsentScreen());
        assertEquals(15, tcModel.getVendorListVersion());
        assertEquals(2, tcModel.getTcfPolicyVersion());
        assertEquals("EN", tcModel.getConsentLanguage());
        assertEquals("AA", tcModel.getPublisherCC());
        assertFalse(tcModel.isServiceSpecific());
        assertTrue(tcModel.getPurposeOneTreatment());
        assertFalse(tcModel.getUseNonStandardStacks());

        assertThat(tcModel.getSpecialFeatureOptIns(), matchInts(1));
        assertThat(tcModel.getPurposesConsent(), matchInts(2, 10));
        assertThat(tcModel.getPurposesLITransparency(), matchInts(2, 9));
    }

    @Test
    public void testConstructConsentsFromBitFields() {
        TCString tcModel =
                parse("COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA");

        assertThat(tcModel.getPurposesConsent(), matchInts(3, 4, 5, 6, 7, 8, 9));
        assertThat(tcModel.getVendorConsent(), matchInts(23, 37, 47, 48, 53, 65, 98));
        assertThat(tcModel.getVendorLegitimateInterest(), matchInts(37, 47, 48, 53, 65, 98, 129));
    }

    @Test
    public void testCanParseDisclosedAndAllowedVendors() {
        TCString tcModel =
                parse("COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA");

        assertThat(tcModel.getAllowedVendors(), matchInts(12, 23, 37, 47, 48, 53));
        assertThat(tcModel.getDisclosedVendors(), matchInts(23, 37, 47, 48, 53, 65, 98, 129));
    }

    @Test
    public void testCanParseAllParts() {
        TCString tcModel =
                parse("COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA.cAAAAAAAITg=");
        assertEquals(1, tcModel.getPubPurposesConsent().toStream().count());
    }

    @Test
    public void testCanParseRangeEncodedVendorLegitimateInterests() {
        String base64CoreString =
                "COv__-wOv__-wC2AAAENAPCgAAAAAAAAAAAAA_wAQA_gEBABAEAAAA";
        TCString tcModel = parse(base64CoreString);
        assertThat(tcModel.getVendorLegitimateInterest(), matchInts(128));
    }

    @Test
    public void testPublisherRestrictions() {
        String bitString =
                "0000100011101011100"
                        + "1000000000000001010"
                        + "0000001110101110010"
                        + "0000000000000101000"
                        + "0000110011111000000"
                        + "0000000000000000100"
                        + "0011010000000011110"
                        + "0001000000000000000"
                        + "0000000000000000000"
                        + "0000000000000000000"
                        + "0000000000000000000"
                        + "0000000000000000000"
                        + "0000000000000000000"
                        + "000000000011"
                        + // NumPubRestrictions (1)
                        "000001"
                        + // PurposeId
                        "01"
                        + // restriction type Require Consent
                        "000000000000"
                        + "000010"
                        + // PurposeId
                        "00"
                        + // restriction type Not Allowed
                        "000000000000"
                        + "000011"
                        + // PurposeId
                        "10"
                        + // restriction REQUIRE_LEGITIMATE_INTEREST
                        "000000000000"
                        + "0"; // padding

        TCString tcModel = parse(base64FromBitString(bitString));

        List<PublisherRestriction> actual = tcModel.getPublisherRestrictions();
        List<PublisherRestriction> expected =
                Arrays.asList(
                        new PublisherRestriction(1, RestrictionType.REQUIRE_CONSENT, BitSetIntIterable.EMPTY),
                        new PublisherRestriction(2, RestrictionType.NOT_ALLOWED, BitSetIntIterable.EMPTY),
                        new PublisherRestriction(
                                3,
                                RestrictionType.REQUIRE_LEGITIMATE_INTEREST,
                                BitSetIntIterable.EMPTY));

        assertEquals(expected, actual);

        assertEquals(1, actual.get(0).getPurposeId());
        assertEquals(RestrictionType.REQUIRE_CONSENT, actual.get(0).getRestrictionType());
        assertFalse(actual.get(0).getVendorIds().iterator().hasNext());
    }

    @Test
    public void testPublisherPurposes() {
        String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        String publisherPurposes =
                "011"
                        + // segment type
                        "100000000000000000000000"
                        + // PubPurposesConsent
                        "000000000000000000000001"
                        + // PubPurposesLITransparency
                        "000010"
                        + // number of custom purposes
                        "01"
                        + // CustomPurposesConsent
                        "11"
                        + // CustomPurposesLITransparency
                        "000"; // just padding
        base64CoreString += "." + base64FromBitString(publisherPurposes);

        TCString tcModel = parse(base64CoreString);

        assertThat(tcModel.getPubPurposesConsent(), matchInts(1));
        assertThat(tcModel.getPubPurposesLITransparency(), matchInts(24));
        assertThat(tcModel.getCustomPurposesConsent(), matchInts(2));
        assertThat(tcModel.getCustomPurposesLITransparency(), matchInts(1, 2));
    }

    @Test
    public void testDefaultSegmentType() {
        String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        String publisherPurposes = "00000000"; // segment type
        base64CoreString += "." + base64FromBitString(publisherPurposes);

        TCString tcModel = parse(base64CoreString);
        assertFalse(tcModel.getCustomPurposesConsent().iterator().hasNext());
        assertTrue(tcModel.getCustomPurposesConsent().isEmpty());
    }

    @Test
    public void testRange() {
        String base64CoreString2Range =
                "COwBOpCOwBOpCLqAAAENAPCAAAAAAAAAAAAAFfwAYFfAV-BVkAGBVYFWAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";
        String base64CoreString1Range =
                "COwBOpCOwBOpCLqAAAENAPCAAAAAAAAAAAAAFfwAQFfgUbABAUaAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";

        TCString tcModel = parse(base64CoreString2Range);
        assertThat(tcModel.getVendorConsent(), matchInts(702, 703));

        tcModel = parse(base64CoreString1Range);
        assertThat(tcModel.getVendorConsent(), matchInts(703));
    }

    @Test
    public void testPurposesConsent() {
        String consent = "COwxsONOwxsONKpAAAENAdCAAMAAAAAAAAAAAAAAAAAA";
        TCString tcModel = parse(consent);

        assertTrue(tcModel.getPurposesConsent().containsAll(1, 2));
    }
}
