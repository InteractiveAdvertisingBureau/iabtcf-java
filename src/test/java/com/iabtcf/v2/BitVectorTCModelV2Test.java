package com.iabtcf.v2;

import static org.junit.Assert.*;

import com.iabtcf.decoder.TCModelDecoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.TreeSet;

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

        assertEquals(new TreeSet<>(Arrays.asList(1)), tcModel.specialFeatureOptIns());
        assertEquals(new TreeSet<>(Arrays.asList(2, 10)), tcModel.purposesConsent());
        assertEquals(new TreeSet<>(Arrays.asList(2, 9)), tcModel.purposesLITransparency());
    }

    @Test
    public void testConstructConsentsFromBitFields() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(new TreeSet<>(Arrays.asList(3, 4, 5, 6, 7, 8, 9)), tcModel.purposesConsent());
        assertEquals(
                new TreeSet<>(Arrays.asList(23, 37, 47, 48, 53, 65, 98)), tcModel.vendorConsents());
        assertEquals(
                new TreeSet<>(Arrays.asList(37, 47, 48, 53, 65, 98, 129)),
                tcModel.vendorLegitimateInterests());
    }

    @Test
    public void testCanParseDisclosedAndAllowedVendors() {
        String base64CoreString =
                "COrEAV4OrXx94ACABBENAHCIAD-AAAAAAACAAxAAAAgAIAwgAgAAAAEAgQAAAAAEAYQAQAAAACAAAABAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA";
        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);

        assertEquals(new TreeSet<>(Arrays.asList(12, 23, 37, 47, 48, 53)), tcModel.allowedVendors());
        assertEquals(
                new TreeSet<>(Arrays.asList(23, 37, 47, 48, 53, 65, 98, 129)), tcModel.disclosedVendors());
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
                        new PublisherRestriction(1, RestrictionType.REQUIRE_CONSENT, Arrays.asList()),
                        new PublisherRestriction(2, RestrictionType.NOT_ALLOWED, Arrays.asList()),
                        new PublisherRestriction(
                                3, RestrictionType.REQUIRE_LEGITIMATE_INTEREST, Arrays.asList()));

        assertEquals(expected, actual);

        assertEquals(1, actual.get(0).getPurposeId());
        assertEquals(RestrictionType.REQUIRE_CONSENT, actual.get(0).getRestrictionType());
        assertEquals(Arrays.asList(), actual.get(0).getVendorIds());
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

        assertEquals(new TreeSet<>(Arrays.asList(1)), tcModel.publisherPurposesConsent());
        assertEquals(new TreeSet<>(Arrays.asList(24)), tcModel.publisherPurposesLITransparency());
        assertEquals(new TreeSet<>(Arrays.asList(2)), tcModel.customPurposesConsent());
        assertEquals(new TreeSet<>(Arrays.asList(1, 2)), tcModel.customPurposesLITransparency());
    }

    @Test
    public void testDefaultSegmentType() {
        String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        String publisherPurposes = "00000000"; // segment type
        base64CoreString += "." + base64FromBitString(publisherPurposes);

        TCModelV2 tcModel = (TCModelV2) TCModelDecoder.instance().decode(base64CoreString);
        assertTrue(tcModel.customPurposesConsent().isEmpty());
    }
}
