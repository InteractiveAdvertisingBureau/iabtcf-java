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

import org.junit.Test;

import com.iabtcf.utils.BitSetIntIterable;

public class PublisherRestrictionTest {
    @Test
    public void testToString() {
        PublisherRestriction publisherRestriction =
                PublisherRestriction.newBuilder().purposeId(1).restrictionType(RestrictionType.NOT_ALLOWED)
            .addVendor(BitSetIntIterable.from(1, 2)).build();
        assertEquals(
                "PublisherRestriction{purposeId=1, restrictionType=NOT_ALLOWED, vendorIds=[1, 2]}",
                publisherRestriction.toString());
    }

    @Test
    public void testEquals() {
        PublisherRestriction pub1 =
            PublisherRestriction.newBuilder().purposeId(1).restrictionType(RestrictionType.NOT_ALLOWED)
                .addVendor(BitSetIntIterable.from(1, 2)).build();
        PublisherRestriction pub2 =
            PublisherRestriction.newBuilder().purposeId(1).restrictionType(RestrictionType.NOT_ALLOWED)
                .addVendor(BitSetIntIterable.from(2, 1)).build();

        assertEquals(pub1, pub2);
        assertEquals(pub1, pub1);
        assertFalse(pub1.equals(null));
    }

    @Test
    public void testHash() {
        PublisherRestriction pub1 =
            PublisherRestriction.newBuilder().purposeId(1).restrictionType(RestrictionType.NOT_ALLOWED)
                .addVendor(BitSetIntIterable.from(1, 2)).build();
        PublisherRestriction pub2 =
            PublisherRestriction.newBuilder().purposeId(1).restrictionType(RestrictionType.NOT_ALLOWED)
                .addVendor(BitSetIntIterable.from(1, 2)).build();

        assertEquals(pub1.hashCode(), pub2.hashCode());
    }
}
