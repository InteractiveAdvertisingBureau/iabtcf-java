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

import java.util.Objects;
import java.util.StringJoiner;

import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterator;

public class PublisherRestriction {

    private final int purposeId;
    private final RestrictionType restrictionType;
    private final IntIterable vendorIds;

    private PublisherRestriction(
            int purposeId, RestrictionType restrictionType, IntIterable vendorIds) {
        Objects.requireNonNull(vendorIds);
        Objects.requireNonNull(restrictionType);

        this.purposeId = purposeId;
        this.restrictionType = restrictionType;
        this.vendorIds = vendorIds;
    }

    public int getPurposeId() {
        return purposeId;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public IntIterable getVendorIds() {
        return vendorIds;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (IntIterator i = getVendorIds().intIterator(); i.hasNext();) {
            sj.add(i.next().toString());
        }
        return "PublisherRestriction{"
                + "purposeId="
                + purposeId
                + ", restrictionType="
                + restrictionType
                + ", vendorIds="
                + sj.toString()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublisherRestriction that = (PublisherRestriction) o;
        return purposeId == that.purposeId
                && restrictionType == that.restrictionType
                && this.vendorIds.equals(that.vendorIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purposeId, restrictionType, vendorIds);
    }

    public static class Builder {
        private int purposeId;
        private RestrictionType restrictionType;
        private final BitSetIntIterable.Builder vendorIds = BitSetIntIterable.newBuilder();

        public Builder() {
        }

        public Builder(PublisherRestriction prototype) {
            purposeId = prototype.purposeId;
            restrictionType = prototype.restrictionType;
            vendorIds.add(prototype.vendorIds);
        }

        public Builder(Builder prototype) {
            purposeId = prototype.purposeId;
            restrictionType = prototype.restrictionType;
            vendorIds.add(prototype.vendorIds);
        }

        public Builder purposeId(int purposeId) {
            this.purposeId = purposeId;
            return this;
        }

        public Builder restrictionType(RestrictionType restrictionType) {
            this.restrictionType = restrictionType;
            return this;
        }

        public Builder addVendor(int vendorId) {
            if (vendorId < 1) {
                throw new IllegalArgumentException("vendor id must be > 0: " + vendorId);
            }

            vendorIds.add(vendorId);
            return this;
        }

        public Builder addVendor(int... vendorIds) {
            for (int i = 0; i < vendorIds.length; i++) {
                addVendor(vendorIds[i]);
            }
            return this;
        }

        public Builder addVendor(IntIterable vendorIds) {
            for (IntIterator ii = vendorIds.intIterator(); ii.hasNext();) {
                addVendor(ii.nextInt());
            }
            return this;
        }

        public Builder clearVendors() {
            vendorIds.clear();
            return this;
        }

        public PublisherRestriction build() {
            if (purposeId <= 0) {
                throw new IllegalArgumentException("purposeId must be positive: " + purposeId);
            }
            
            return new PublisherRestriction(purposeId,
                                            restrictionType,
                                            vendorIds.build());
        }

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(PublisherRestriction prototype) {
        return new Builder(prototype);
    }

    public static Builder newBuilder(Builder prototype) {
        return new Builder(prototype);
    }
}
