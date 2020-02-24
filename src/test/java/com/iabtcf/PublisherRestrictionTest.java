package com.iabtcf;

import com.iabtcf.decoder.Util;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PublisherRestrictionTest {

    @Test
    public void testEquals() {
        PublisherRestriction pub1 = new PublisherRestriction(RestrictionType.NOT_ALLOWED, Util.bitSetOf(1, 2));
        PublisherRestriction pub2 = new PublisherRestriction(RestrictionType.NOT_ALLOWED, Util.bitSetOf(2, 1));

        assertNotNull(pub1);
        assertEquals(pub1, pub2);
        assertEquals(pub1, pub1);
    }

    @Test
    public void testHash() {
        PublisherRestriction pub1 = new PublisherRestriction(RestrictionType.NOT_ALLOWED, Util.bitSetOf(1, 2));
        PublisherRestriction pub2 = new PublisherRestriction(RestrictionType.NOT_ALLOWED, Util.bitSetOf(1, 2));

        assertEquals(pub1.hashCode(), pub2.hashCode());
    }
}
