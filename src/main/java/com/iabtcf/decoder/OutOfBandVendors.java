package com.iabtcf.decoder;

import com.iabtcf.OutOfBandConsent;

import java.util.Objects;
import java.util.Set;

/**
 * @author evanwht1
 */
class OutOfBandVendors implements OutOfBandConsent {

	private final Set<Integer> disclosedVendor;
	private final Set<Integer> allowedVendors;

	OutOfBandVendors(final Set<Integer> disclosedVendor, final Set<Integer> allowedVendors) {
		this.disclosedVendor = disclosedVendor;
		this.allowedVendors = allowedVendors;
	}

	@Override
	public Set<Integer> getDisclosedVendors() {
		return disclosedVendor;
	}

	@Override
	public Set<Integer> getAllowedVendors() {
		return allowedVendors;
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
