package com.iabtcf.v2;

import java.util.List;
import java.util.Objects;

public class PublisherRestriction {

    private final int purposeId;
    private final RestrictionType restrictionType;
    private final List<Integer> vendorIds;

    public PublisherRestriction(
            int purposeId, RestrictionType restrictionType, List<Integer> vendorIds) {
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

    public List<Integer> getVendorIds() {
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
                && vendorIds.equals(that.vendorIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purposeId, restrictionType, vendorIds);
    }
}
