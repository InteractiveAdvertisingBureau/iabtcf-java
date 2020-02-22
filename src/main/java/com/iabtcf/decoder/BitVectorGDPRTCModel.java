package com.iabtcf.decoder;

import com.iabtcf.CoreString;
import com.iabtcf.GDPRTransparencyAndConsent;
import com.iabtcf.OutOfBandConsent;
import com.iabtcf.PublisherTC;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
class BitVectorGDPRTCModel implements GDPRTransparencyAndConsent {

    private final CoreStringImpl coreString;
    private final OutOfBandVendors outOfBandVendors;
    private final PublisherTC publisherPurposes;

    private BitVectorGDPRTCModel(final Builder b) {
        coreString = b.coreString;
        outOfBandVendors = new OutOfBandVendors(b.disclosedVendors, b.allowedVendors);
        publisherPurposes = b.publisherPurposes;
    }

    static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public CoreString getCoreString() {
        return coreString;
    }

    @Override
    public OutOfBandConsent getOutOfBandSignals() {
        return outOfBandVendors;
    }

    @Override
    public PublisherTC getPublisherPurposesTC() {
        return publisherPurposes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BitVectorGDPRTCModel that = (BitVectorGDPRTCModel) o;
        return coreString.equals(that.coreString) &&
               outOfBandVendors.equals(that.outOfBandVendors) &&
               Objects.equals(publisherPurposes, that.publisherPurposes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coreString, outOfBandVendors, publisherPurposes);
    }

    @Override
    public String toString() {
        return "BitVectorGDPRTransparencyAndConsent{" +
               "coreString: " + coreString +
               ", outOfBandVendors: " + outOfBandVendors +
               ", publisherPurposes: " + publisherPurposes +
               '}';
    }

    static final class Builder {

        private CoreStringImpl coreString;
        private Set<Integer> disclosedVendors = Collections.emptySet();
        private Set<Integer> allowedVendors = Collections.emptySet();
        private PublisherTCImpl publisherPurposes;

        private Builder() {}

        Builder coreString(final CoreStringImpl coreString) {
            this.coreString = coreString;
            return this;
        }

        Builder disclosedVendors(final Set<Integer> disclosedVendors) {
            this.disclosedVendors = disclosedVendors;
            return this;
        }

        Builder allowedVendors(final Set<Integer> allowedVendors) {
            this.allowedVendors = allowedVendors;
            return this;
        }

        Builder publisherPurposes(final PublisherTCImpl publisherPurposes) {
            this.publisherPurposes = publisherPurposes;
            return this;
        }

        BitVectorGDPRTCModel build() {
            return new BitVectorGDPRTCModel(this);
        }
    }
}
