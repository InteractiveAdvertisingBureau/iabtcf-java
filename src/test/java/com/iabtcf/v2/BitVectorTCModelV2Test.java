package com.iabtcf.v2;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

import com.iabtcf.decoder.TCModelDecoder;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.IntIterableUtils;

public class BitVectorTCModelV2Test {

    static String base64FromBitString(String str) {
        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < str.length(); ) {
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
        String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(2, tcModel.version());
        assertEquals(Instant.parse("2020-01-26T17:01:00Z"), tcModel.consentRecordCreated());
        assertEquals(Instant.parse("2021-02-02T17:01:00Z"), tcModel.consentRecordLastUpdated());
        assertEquals(675, tcModel.consentManagerProviderId());
        assertEquals(2, tcModel.consentManagerProviderVersion());
        assertEquals(1, tcModel.consentScreen());
        assertEquals(15, tcModel.vendorListVersion());
        assertEquals(2, tcModel.policyVersion());
        assertEquals("EN", tcModel.consentLanguage());
        assertEquals("AA", tcModel.publisherCountryCode());
        assertFalse(tcModel.isServiceSpecific());
        assertTrue(tcModel.isPurposeOneTreatment());
        assertFalse(tcModel.useNonStandardStacks());

        assertEquals(new TreeSet<>(Arrays.asList(1)), IntIterableUtils.toSet(tcModel.specialFeatureOptIns()));
        assertEquals(new TreeSet<>(Arrays.asList(2, 10)), IntIterableUtils.toSet(tcModel.purposesConsent()));
        assertEquals(new TreeSet<>(Arrays.asList(2, 9)), IntIterableUtils.toSet(tcModel.purposesLITransparency()));
    }

    @Test
    public void testConstructConsentsFromBitFields() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(new TreeSet<>(Arrays.asList(3, 4, 5, 6, 7, 8, 9)),
                IntIterableUtils.toSet(tcModel.purposesConsent()));
        assertEquals(
                new TreeSet<>(Arrays.asList(23, 37, 47, 48, 53, 65, 98)),
                IntIterableUtils.toSet(tcModel.vendorConsents()));
        assertEquals(
                new TreeSet<>(Arrays.asList(37, 47, 48, 53, 65, 98, 129)),
                IntIterableUtils.toSet(tcModel.vendorLegitimateInterests()));
    }

    @Test
    public void testCanParseDisclosedAndAllowedVendors() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(new TreeSet<>(Arrays.asList(12, 23, 37, 47, 48, 53)),
                IntIterableUtils.toSet(tcModel.allowedVendors()));
        assertEquals(
                new TreeSet<>(Arrays.asList(23, 37, 47, 48, 53, 65, 98, 129)),
                IntIterableUtils.toSet(tcModel.disclosedVendors()));
    }

    @Test
    public void testCanParseAllParts() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA.cAAAAAAAITg=";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);
        assertEquals(1, IntIterableUtils.toStream(tcModel.publisherPurposesConsent()).count());
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

        String base64CoreString = base64FromBitString(bitString);
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        List<PublisherRestriction> actual = tcModel.publisherRestrictions();
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

        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(new TreeSet<>(Arrays.asList(1)), IntIterableUtils.toSet(tcModel.publisherPurposesConsent()));
        assertEquals(new TreeSet<>(Arrays.asList(24)),
                IntIterableUtils.toSet(tcModel.publisherPurposesLITransparency()));
        assertEquals(new TreeSet<>(Arrays.asList(2)), IntIterableUtils.toSet(tcModel.customPurposesConsent()));
        assertEquals(new TreeSet<>(Arrays.asList(1, 2)),
                IntIterableUtils.toSet(tcModel.customPurposesLITransparency()));
    }

    @Test
    public void testDefaultSegmentType() {
        String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        String publisherPurposes = "00000000"; // segment type
        base64CoreString += "." + base64FromBitString(publisherPurposes);

        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);
        assertFalse(tcModel.customPurposesConsent().iterator().hasNext());
    }

    @Test
    public void testRange() {
        String base64CoreString2Range =
                "COwBOpCOwBOpCLqAAAENAPCAAAAAAAAAAAAAFfwAYFfAV-BVkAGBVYFWAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";
        String base64CoreString1Range =
                "COwBOpCOwBOpCLqAAAENAPCAAAAAAAAAAAAAFfwAQFfgUbABAUaAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";

        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString2Range);
        assertEquals(new TreeSet<>(Arrays.asList(702, 703)), IntIterableUtils.toSet(tcModel.vendorConsents()));

        tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString1Range);
        assertEquals(new TreeSet<>(Arrays.asList(703)), IntIterableUtils.toSet(tcModel.vendorConsents()));
    }
}
