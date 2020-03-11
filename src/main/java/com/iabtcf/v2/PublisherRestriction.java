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

import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterator;

public class PublisherRestriction {

    private final int purposeId;
    private final RestrictionType restrictionType;
    private final IntIterable vendorIds;

    public PublisherRestriction(
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
}
