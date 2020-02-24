package com.iabtcf.decoder;

import com.iabtcf.PublisherTC;

import java.util.BitSet;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author evanwht1
 */
class PublisherTCImpl implements PublisherTC {

	private final BitSet purposesConsent;
	private final BitSet purposesLegitimateInterest;
	private final BitSet customPurposesConsent;
	private final BitSet customPurposesLegitimateInterest;

	PublisherTCImpl(final BitSet purposesConsent,
	                final BitSet purposesLegitimateInterest,
	                final BitSet customPurposesConsent,
	                final BitSet customPurposesLegitimateInterest) {
		this.purposesConsent = purposesConsent;
		this.purposesLegitimateInterest = purposesLegitimateInterest;
		this.customPurposesConsent = customPurposesConsent;
		this.customPurposesLegitimateInterest = customPurposesLegitimateInterest;
	}

	@Override
	public boolean isPurposeConsented(final int purpose) {
		return purposesConsent.get(purpose);
	}

	@Override
	public IntStream getAllConsentedPurposes() {
		return purposesConsent.stream();
	}

	@Override
	public boolean isPurposeLegitimateInterest(final int purpose) {
		return purposesLegitimateInterest.get(purpose);
	}

	@Override
	public IntStream getAllLegitimateInterestPurposes() {
		return purposesLegitimateInterest.stream();
	}

	@Override
	public boolean isCustomPurposeConsented(final int customPurpose) {
		return customPurposesConsent.get(customPurpose);
	}

	@Override
	public IntStream getAllConsentedCustomPurposes() {
		return customPurposesConsent.stream();
	}

	@Override
	public boolean isCustomPurposeLegitimateInterest(final int customPurpose) {
		return customPurposesLegitimateInterest.get(customPurpose);
	}

	@Override
	public IntStream getAllLegitimateInterestCustomPurposes() {
		return customPurposesLegitimateInterest.stream();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final PublisherTCImpl that = (PublisherTCImpl) o;
		return Objects.equals(purposesConsent, that.purposesConsent) &&
		       Objects.equals(purposesLegitimateInterest, that.purposesLegitimateInterest) &&
		       Objects.equals(customPurposesConsent, that.customPurposesConsent) &&
		       Objects.equals(customPurposesLegitimateInterest, that.customPurposesLegitimateInterest);
	}

	@Override
	public int hashCode() {
		return Objects.hash(purposesConsent, purposesLegitimateInterest, customPurposesConsent, customPurposesLegitimateInterest);
	}

	@Override
	public String toString() {
		return "PublisherTCImpl{" +
		       "publisherPurposesConsent: " + purposesConsent +
		       ", publisherPurposesLITransparency: " + purposesLegitimateInterest +
		       ", customPurposesConsent: " + customPurposesConsent +
		       ", customPurposesLITransparency: " + customPurposesLegitimateInterest +
		       '}';
	}
}
