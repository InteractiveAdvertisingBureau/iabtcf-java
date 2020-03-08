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

import static org.junit.Assert.*;

import com.iabtcf.decoder.TCModelDecoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.junit.Test;

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
        assertEquals(675, tcModel.cmpId());
        assertEquals(2, tcModel.cmpVersion());
        assertEquals(1, tcModel.consentScreen());
        assertEquals(15, tcModel.vendorListVersion());
        assertEquals(2, tcModel.policyVersion());
        assertEquals("EN", tcModel.consentLanguage());
        assertEquals("AA", tcModel.publisherCountryCode());
        assertFalse(tcModel.isServiceSpecific());
        assertTrue(tcModel.isPurposeOneTreatment());
        assertFalse(tcModel.useNonStandardStacks());

        assertTrue(tcModel.isSpecialFeatureOptedIn(1));
        assertTrue(tcModel.isPurposeConsented(2));
        assertTrue(tcModel.isPurposeConsented(10));
        assertTrue(tcModel.isPurposeLegitimateInterest(2));
        assertTrue(tcModel.isPurposeLegitimateInterest(9));
    }

    @Test
    public void testConstructConsentsFromBitFields() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertTrue(tcModel.isPurposeConsented(3));
        assertTrue(tcModel.isPurposeConsented(4));
        assertTrue(tcModel.isPurposeConsented(5));
        assertTrue(tcModel.isPurposeConsented(6));
        assertTrue(tcModel.isPurposeConsented(7));
        assertTrue(tcModel.isPurposeConsented(8));
        assertTrue(tcModel.isPurposeConsented(9));
        assertTrue(tcModel.isVendorConsented(23));
        assertTrue(tcModel.isVendorConsented(37));
        assertTrue(tcModel.isVendorConsented(47));
        assertTrue(tcModel.isVendorConsented(48));
        assertTrue(tcModel.isVendorConsented(53));
        assertTrue(tcModel.isVendorConsented(65));
        assertTrue(tcModel.isVendorConsented(98));
        assertTrue(tcModel.isVendorLegitimateInterest(37));
        assertTrue(tcModel.isVendorLegitimateInterest(47));
        assertTrue(tcModel.isVendorLegitimateInterest(53));
        assertTrue(tcModel.isVendorLegitimateInterest(65));
        assertTrue(tcModel.isVendorLegitimateInterest(98));
        assertTrue(tcModel.isVendorLegitimateInterest(129));
    }

    @Test
    public void testCanParseDisclosedAndAllowedVendors() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertTrue(tcModel.isOOBAllowedVendor(12));
        assertTrue(tcModel.isOOBAllowedVendor(23));
        assertTrue(tcModel.isOOBAllowedVendor(37));
        assertTrue(tcModel.isOOBAllowedVendor(47));
        assertTrue(tcModel.isOOBAllowedVendor(48));
        assertTrue(tcModel.isOOBAllowedVendor(53));
        assertTrue(tcModel.isOOBDisclosedVendor(23));
        assertTrue(tcModel.isOOBDisclosedVendor(37));
        assertTrue(tcModel.isOOBDisclosedVendor(47));
        assertTrue(tcModel.isOOBDisclosedVendor(48));
        assertTrue(tcModel.isOOBDisclosedVendor(53));
        assertTrue(tcModel.isOOBDisclosedVendor(65));
        assertTrue(tcModel.isOOBDisclosedVendor(98));
        assertTrue(tcModel.isOOBDisclosedVendor(129));
    }

    @Test
    public void testCanParseAllParts() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA.cAAAAAAAITg=";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);
        assertTrue(tcModel.isPublisherPurposeConsented(1));
    }

    @Test
    public void testPublisherRestrictions() {
        String bitString =
                "0000100011101011100" +
                        "1000000000000001010" +
                        "0000001110101110010" +
                        "0000000000000101000" +
                        "0000110011111000000" +
                        "0000000000000000100" +
                        "0011010000000011110" +
                        "0001000000000000000" +
                        "0000000000000000000" +
                        "0000000000000000000" +
                        "0000000000000000000" +
                        "0000000000000000000" +
                        "0000000000000000000" +
                        "000000000011" + // NumPubRestrictions (3)
                        "000001" + // PurposeId
                        "01" + // restriction type Require Consent
                        "000000000001" + // num entries
                        "0" + // range encoding
                        "0000000000000001" + // range encoding
                        "000010" + // PurposeId
                        "00" + // restriction type Not Allowed
                        "000000000001" + // num entries
                        "0" + // range encoding
                        "0000000000000010" + // range encoding
                        "000011" + // PurposeId
                        "10" + // restriction REQUIRE_LEGITIMATE_INTEREST
                        "000000000001" + // num entries
                        "0" + // range encoding
                        "0000000000000011" + // range encoding
                        "0000000"; // padding

        String base64CoreString = base64FromBitString(bitString);
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(RestrictionType.REQUIRE_CONSENT, tcModel.getVendorRestrictionType(1, 1));
        assertEquals(RestrictionType.NOT_ALLOWED, tcModel.getVendorRestrictionType(2, 2));
        assertEquals(RestrictionType.REQUIRE_LEGITIMATE_INTEREST, tcModel.getVendorRestrictionType(3, 3));
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

        assertTrue(tcModel.isPublisherPurposeConsented(1));
        assertTrue(tcModel.isPublisherPurposeLegitimateInterest(24));
        assertTrue(tcModel.isCustomPublisherPurposeConsented(2));
        assertTrue(tcModel.isCustomPublisherPurposeLegitimateInterest(1));
        assertTrue(tcModel.isCustomPublisherPurposeLegitimateInterest(2));
    }
}
