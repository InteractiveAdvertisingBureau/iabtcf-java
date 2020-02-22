package com.iabtcf.decoder;

import com.iabtcf.GDPRTransparencyAndConsent;
import com.iabtcf.OutOfBandConsent;
import com.iabtcf.PublisherRestriction;
import com.iabtcf.PublisherTC;
import com.iabtcf.RestrictionType;
import org.junit.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TCDecoderTest {

    @Test
    public void testDecodeAllSegments() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA.cAAAAAAAITg=";
        GDPRTransparencyAndConsent tc = TCModelDecoder.decode(tcString);

        final OutOfBandConsent outOfBandSignals = tc.getOutOfBandSignals();
        assertNotNull(outOfBandSignals);
        assertFalse(outOfBandSignals.getDisclosedVendors().isEmpty());
        assertFalse(outOfBandSignals.getAllowedVendors().isEmpty());
        assertEquals(Util.setOf(12, 23, 37, 47, 48, 53), outOfBandSignals.getAllowedVendors());
        assertEquals(Util.setOf(23, 37, 47, 48, 53, 65, 98, 129), outOfBandSignals.getDisclosedVendors());
        final PublisherTC publisherTC = tc.getPublisherPurposesTC();
        assertNotNull(publisherTC);
        assertEquals(Util.setOf(1), publisherTC.getPurposesConsent());
        assertEquals(Util.setOf(24), publisherTC.getPurposesLITransparency());
        assertEquals(Util.setOf(2), publisherTC.getCustomPurposesConsent());
        assertEquals(Util.setOf(1, 2), publisherTC.getCustomPurposesLITransparency());
    }

    @Test
    public void testParseWithOOBSignals() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IBAgAAAgAIAwgAgAAAAEAAAACA.QAagAQAgAIAwgA";
        GDPRTransparencyAndConsent tc = TCModelDecoder.decode(tcString);

        final OutOfBandConsent outOfBandSignals = tc.getOutOfBandSignals();
        assertNotNull(outOfBandSignals);
        assertFalse(outOfBandSignals.getDisclosedVendors().isEmpty());
        assertFalse(outOfBandSignals.getAllowedVendors().isEmpty());
        assertEquals(Util.setOf(12, 23, 37, 47, 48, 53), outOfBandSignals.getAllowedVendors());
        assertEquals(Util.setOf(23, 37, 47, 48, 53, 65, 98, 129), outOfBandSignals.getDisclosedVendors());
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
                + "000000000000"
                + "000010" // PurposeId
                + "00" // restriction type Not Allowed
                + "000000000000"
                + "000011" // PurposeId
                + "10" // restriction REQUIRE_LEGITIMATE_INTEREST
                + "000000000000"
                + "0"; // padding

        String base64CoreString = Util.base64FromBitString(bitString);
        GDPRTransparencyAndConsent tc = TCModelDecoder.decode(base64CoreString);

        Set<PublisherRestriction> actual = tc.getCoreString().getPublisherRestrictions();
        Set<PublisherRestriction> expected =
                Util.setOf(
                        new PublisherRestriction(1, RestrictionType.REQUIRE_CONSENT, new HashSet<>()),
                        new PublisherRestriction(2, RestrictionType.NOT_ALLOWED, new HashSet<>()),
                        new PublisherRestriction(3, RestrictionType.REQUIRE_LEGITIMATE_INTEREST, new HashSet<>()));

        assertEquals(expected, actual);
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
