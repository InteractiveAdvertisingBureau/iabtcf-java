package com.iabtcf.decoder;

import com.iabtcf.OutOfBandConsent;

import java.util.BitSet;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author evanwht1
 */
class OutOfBandVendors implements OutOfBandConsent {

	private final BitSet disclosedVendor;
	private final BitSet allowedVendors;

	OutOfBandVendors(final BitSet disclosedVendor, BitSet allowedVendors) {
		this.disclosedVendor = disclosedVendor;
		this.allowedVendors = allowedVendors;
	}

	@Override
	public boolean isVendorDisclosed(final int vendor) {
		return disclosedVendor.get(vendor);
	}

	@Override
	public IntStream getAllDisclosedVendors() {
		return disclosedVendor.stream();
	}

	@Override
	public boolean isVendorAllowed(final int vendor) {
		return allowedVendors.get(vendor);
	}

	@Override
	public IntStream getAllAllowedVendors() {
		return allowedVendors.stream();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final OutOfBandVendors that = (OutOfBandVendors) o;
		return disclosedVendor.equals(that.disclosedVendor) &&
		       allowedVendors.equals(that.allowedVendors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(disclosedVendor, allowedVendors);
	}

	@Override
	public String toString() {
		return "OutOfBandVendors{" +
		       "disclosedVendor: " + disclosedVendor +
		       ", allowedVendors: " + allowedVendors +
		       '}';
	}
}
