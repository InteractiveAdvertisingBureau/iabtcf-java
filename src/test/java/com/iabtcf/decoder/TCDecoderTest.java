package com.iabtcf.decoder;

import com.iabtcf.CoreString;
import com.iabtcf.GDPRTransparencyAndConsent;
import com.iabtcf.OutOfBandConsent;
import com.iabtcf.PublisherTC;
import com.iabtcf.RestrictionType;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TCDecoderTest {

    @Test
    public void testDecodeAllSegments() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA.cAAAAAAAITg=";
        GDPRTransparencyAndConsent tc = TCModelDecoder.decode(tcString);

        final OutOfBandConsent outOfBandSignals = tc.getOutOfBandSignals();
        assertNotNull(outOfBandSignals);
        assertTrue(outOfBandSignals.isVendorAllowed(12));
        assertTrue(outOfBandSignals.isVendorAllowed(23));
        assertTrue(outOfBandSignals.isVendorAllowed(37));
        assertTrue(outOfBandSignals.isVendorAllowed(47));
        assertTrue(outOfBandSignals.isVendorAllowed(48));
        assertTrue(outOfBandSignals.isVendorAllowed(53));
        assertTrue(outOfBandSignals.isVendorDisclosed(23));
        assertTrue(outOfBandSignals.isVendorDisclosed(37));
        assertTrue(outOfBandSignals.isVendorDisclosed(47));
        assertTrue(outOfBandSignals.isVendorDisclosed(48));
        assertTrue(outOfBandSignals.isVendorDisclosed(53));
        assertTrue(outOfBandSignals.isVendorDisclosed(65));
        assertTrue(outOfBandSignals.isVendorDisclosed(98));
        assertTrue(outOfBandSignals.isVendorDisclosed(129));
        final PublisherTC publisherTC = tc.getPublisherPurposesTC();
        assertNotNull(publisherTC);
        assertTrue(publisherTC.isPurposeConsented(1));
        assertTrue(publisherTC.isPurposeLegitimateInterest(24));
        assertTrue(publisherTC.isCustomPurposeConsented(2));
        assertTrue(publisherTC.isCustomPurposeLegitimateInterest(1));
        assertTrue(publisherTC.isCustomPurposeLegitimateInterest(2));
    }

    @Test
    public void testParseWithOOBSignals() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA";
        GDPRTransparencyAndConsent tc = TCModelDecoder.decode(tcString);

        final OutOfBandConsent outOfBandSignals = tc.getOutOfBandSignals();
        assertNotNull(outOfBandSignals);
        assertTrue(outOfBandSignals.isVendorAllowed(12));
        assertTrue(outOfBandSignals.isVendorAllowed(23));
        assertTrue(outOfBandSignals.isVendorAllowed(37));
        assertTrue(outOfBandSignals.isVendorAllowed(47));
        assertTrue(outOfBandSignals.isVendorAllowed(48));
        assertTrue(outOfBandSignals.isVendorAllowed(53));
        assertTrue(outOfBandSignals.isVendorDisclosed(23));
        assertTrue(outOfBandSignals.isVendorDisclosed(37));
        assertTrue(outOfBandSignals.isVendorDisclosed(47));
        assertTrue(outOfBandSignals.isVendorDisclosed(48));
        assertTrue(outOfBandSignals.isVendorDisclosed(53));
        assertTrue(outOfBandSignals.isVendorDisclosed(65));
        assertTrue(outOfBandSignals.isVendorDisclosed(98));
        assertTrue(outOfBandSignals.isVendorDisclosed(129));
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
                + "000000000011" // NumPubRestrictions (1)
                + "000001" // PurposeId
                + "01" // restriction type Require Consent
                + "000000000001"
                + "0"
                + "0000000000000001"
                + "000010" // PurposeId
                + "00" // restriction type Not Allowed
                + "000000000001"
                + "0"
                + "0000000000000010"
                + "000011" // PurposeId
                + "10" // restriction REQUIRE_LEGITIMATE_INTEREST
                + "000000000001"
                + "0"
                + "0000000000000011";

        String base64CoreString = Util.base64FromBitString(bitString);
        GDPRTransparencyAndConsent tc = TCModelDecoder.decode(base64CoreString);
        final CoreString coreString = tc.getCoreString();

        assertEquals(RestrictionType.REQUIRE_CONSENT, coreString.getVendorRestrictionType(1, 1));
        assertEquals(RestrictionType.UNDEFINED, coreString.getVendorRestrictionType(1, 2));
        assertEquals(RestrictionType.NOT_ALLOWED, coreString.getVendorRestrictionType(2, 2));
        assertEquals(RestrictionType.REQUIRE_LEGITIMATE_INTEREST, coreString.getVendorRestrictionType(3, 3));
        assertEquals(RestrictionType.UNDEFINED, coreString.getVendorRestrictionType(4, 1));
    }

    @Test
    public void testCoreStringAndPublisherTC() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";
        assertNotNull(TCModelDecoder.decode(tcString));
    }

    @Test
    public void testCoreStringOnly() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        assertNotNull(TCModelDecoder.decode(tcString));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testVersionOne() {
        String tcString = "BObdrPUOevsguAfDqFENCNAAAAAmeAAA";
        TCModelDecoder.decode(tcString);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedVersion() {
        String tcString = Base64.getUrlEncoder().encodeToString(new byte[] {13 });
        TCModelDecoder.decode(tcString);
    }

    @Test
    public void testDefaultSegmentType() {
        final String publisherPurposes = "00000000"; // segment type
        final String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA." + Util.base64FromBitString(publisherPurposes);

        final GDPRTransparencyAndConsent tc = TCModelDecoder.decode(base64CoreString);
        assertNull(tc.getPublisherPurposesTC());
    }

}
