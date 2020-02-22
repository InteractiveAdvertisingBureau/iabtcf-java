package com.iabtcf;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
public class PublisherRestriction {

    private final int purposeId;
    private final RestrictionType restrictionType;
    private final Set<Integer> vendorIds;

    public PublisherRestriction(int purposeId, RestrictionType restrictionType, Set<Integer> vendorIds) {
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

    public Set<Integer> getVendorIds() {
        return vendorIds;
    }

    @Override
    public String toString() {
        return "PublisherRestriction{"
               + "purposeId="
               + purposeId
               + ", restrictionType="
               + restrictionType
               + ", vendorIds="
               + vendorIds
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
               && new HashSet<>(this.vendorIds).equals(new HashSet<>(that.vendorIds));
    }

    @Override
    public int hashCode() {
        return Objects.hash(purposeId, restrictionType, vendorIds);
    }
}
