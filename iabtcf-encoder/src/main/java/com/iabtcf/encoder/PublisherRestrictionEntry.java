package com.iabtcf.encoder;

/*-
 * #%L
 * IAB TCF Java Encoder Library
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

import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterator;
import com.iabtcf.v2.RestrictionType;

public class PublisherRestrictionEntry {
    private final int purposeId;
    private final RestrictionType restrictionType;
    private final IntIterable vendors;

    private PublisherRestrictionEntry(int purposeId, RestrictionType restrictionType, IntIterable vendors) {
        if (purposeId <= 0) {
            throw new IllegalArgumentException("purposeId must be positive: " + purposeId);
        }

        this.purposeId = Bounds.checkBounds(purposeId, FieldDefs.PURPOSE_ID);
        this.restrictionType = restrictionType;
        this.vendors = vendors;
    }

    public int getPurposeId() {
        return purposeId;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public IntIterable getVendors() {
        return vendors;
    }

    public static class Builder {
        private int purposeId;
        private RestrictionType restrictionType;
        private final BitSetIntIterable.Builder vendors = BitSetIntIterable.newBuilder();

        public Builder() {
        }

        public Builder(PublisherRestrictionEntry prototype) {
            purposeId = prototype.purposeId;
            restrictionType = prototype.restrictionType;
            vendors.add(prototype.vendors);
        }

        public Builder(Builder prototype) {
            purposeId = prototype.purposeId;
            restrictionType = prototype.restrictionType;
            vendors.add(prototype.vendors);
        }

        public Builder purposeId(int purposeId) {
            this.purposeId = purposeId;
            return this;
        }

        public Builder restrictionType(RestrictionType restrictionType) {
            this.restrictionType = restrictionType;
            return this;
        }

        public Builder addVendor(int vendor) {
            if (vendor < 1) {
                throw new IllegalArgumentException("vendor id must be > 0: " + vendor);
            }

            vendors.add(vendor);
            return this;
        }

        public Builder addVendor(int... vendors) {
            for (int i = 0; i < vendors.length; i++) {
                addVendor(vendors[i]);
            }
            return this;
        }

        public Builder addVendor(IntIterable vendors) {
            for (IntIterator ii = vendors.intIterator(); ii.hasNext();) {
                addVendor(ii.nextInt());
            }
            return this;
        }

        public Builder clearVendors() {
            vendors.clear();
            return this;
        }

        public PublisherRestrictionEntry build() {
            return new PublisherRestrictionEntry(purposeId,
                    restrictionType,
                    vendors.build());
        }

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(PublisherRestrictionEntry prototype) {
        return new Builder(prototype);
    }

    public static Builder newBuilder(Builder prototype) {
        return new Builder(prototype);
    }
}
