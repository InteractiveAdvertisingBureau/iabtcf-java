package com.iabtcf.decoder;

import static org.junit.Assert.*;

import java.util.Base64;

import org.junit.Test;

public class TCModelDecoderImplTest {

    @Test
    public void testCanCreateModelFromTwoPartsString() {
        String tcString =
                "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";
        assertNotNull(TCModelDecoder.instance().decode(tcString));
    }

    @Test
    public void testCanCreateModelOnePartString() {
        String tcString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
        assertNotNull(TCModelDecoder.instance().decode(tcString));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailIfVersionOneStringIsPassed() {
        String tcString = "BObdrPUOevsguAfDqFENCNAAAAAmeAAA";
        TCModelDecoder.instance().decode(tcString);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailIfANonSupportedVersionIsPassed() {
        String tcString = Base64.getUrlEncoder().encodeToString(new byte[] { 13 });
        TCModelDecoder.instance().decode(tcString);
    }
}
