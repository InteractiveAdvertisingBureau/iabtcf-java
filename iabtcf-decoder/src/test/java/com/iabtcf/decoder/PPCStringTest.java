package com.iabtcf.decoder;

import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;

public class PPCStringTest {
    @Test
    public void decodePublisherConsetV1() {
        PPCString decode = PPCString.decode("BOxgOqAOxgOqAAAABBENC2-AAAAtHAA");

        assertEquals(decode.getVersion(), 1);
        assertEquals(Instant.parse("2020-04-07T20:36:16.000Z"), decode.getCreated());
        assertEquals(Instant.parse("2020-04-07T20:36:16.000Z"), decode.getLastUpdated());
        assertEquals(0, decode.getCmpId());
        assertEquals(1, decode.getCmpVersion());
        assertEquals(1, decode.getConsentScreen());
        assertEquals("EN", decode.getConsentLanguage());
        assertEquals(182, decode.getVendorListVersion());
        assertEquals(3968, decode.getPublisherPurposesVersion());
        assertThat(decode.getStandardPurposesAllowed(), matchInts(19, 21, 22, 24));
        assertTrue(decode.getCustomPurposesBitField().isEmpty());
    }
}
