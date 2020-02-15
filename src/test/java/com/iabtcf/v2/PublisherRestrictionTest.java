package com.iabtcf.v2;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class PublisherRestrictionTest {

    @Test
    public void testToString() {
        PublisherRestriction publisherRestriction =
                new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, Arrays.asList(1, 2));
        assertEquals(
                "PublisherRestriction{purposeId=1, restrictionType=NOT_ALLOWED, vendorIds=[1, 2]}",
                publisherRestriction.toString());
    }

    @Test
    public void testEquals() {
        PublisherRestriction pub1 =
                new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, Arrays.asList(1, 2));
        PublisherRestriction pub2 =
                new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, Arrays.asList(1, 2));

        assertEquals(pub1, pub2);
        assertEquals(pub1, pub1);
        assertFalse(pub1.equals(null));
    }

    @Test
    public void testHash() {
        PublisherRestriction pub1 =
                new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, Arrays.asList(1, 2));
        PublisherRestriction pub2 =
                new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, Arrays.asList(1, 2));

        assertEquals(pub1.hashCode(), pub2.hashCode());
    }
}
