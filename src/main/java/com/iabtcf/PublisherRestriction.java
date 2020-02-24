package com.iabtcf;

import java.util.BitSet;
import java.util.Objects;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
public class PublisherRestriction {

    private final RestrictionType restrictionType;
    private final BitSet vendorIds;

    public PublisherRestriction(RestrictionType restrictionType, BitSet vendorIds) {
        Objects.requireNonNull(vendorIds);
        Objects.requireNonNull(restrictionType);

        this.restrictionType = restrictionType;
        this.vendorIds = vendorIds;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public boolean isVendorIncluded(final int vendor) {
        return vendorIds.get(vendor);
    }

    @Override
    public String toString() {
        return "PublisherRestriction{"
               + "restrictionType="
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
        return restrictionType == that.restrictionType
               && vendorIds.equals(that.vendorIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restrictionType, vendorIds);
    }
}
