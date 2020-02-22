package com.iabtcf;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PublisherRestrictionTest {

    @Test
    public void testToString() {
        PublisherRestriction publisherRestriction = new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, new HashSet<>(Arrays.asList(1, 2)));
        assertEquals("PublisherRestriction{purposeId=1, restrictionType=NOT_ALLOWED, vendorIds=[1, 2]}", publisherRestriction.toString());
    }

    @Test
    public void testEquals() {
        PublisherRestriction pub1 = new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, new HashSet<>(Arrays.asList(1, 2)));
        PublisherRestriction pub2 = new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, new HashSet<>(Arrays.asList(2, 1)));

        assertNotNull(pub1);
        assertEquals(pub1, pub2);
        assertEquals(pub1, pub1);
    }

    @Test
    public void testHash() {
        PublisherRestriction pub1 = new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, new HashSet<>(Arrays.asList(1, 2)));
        PublisherRestriction pub2 = new PublisherRestriction(1, RestrictionType.NOT_ALLOWED, new HashSet<>(Arrays.asList(1, 2)));

        assertEquals(pub1.hashCode(), pub2.hashCode());
    }
}
