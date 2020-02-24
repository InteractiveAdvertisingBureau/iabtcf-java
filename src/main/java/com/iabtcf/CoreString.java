package com.iabtcf;

import java.time.Instant;
import java.util.stream.IntStream;

/**
 * @author evanwht1
 */
public interface CoreString {

	int getVersion();

	Instant getConsentRecordCreated();

	Instant getConsentRecordLastUpdated();

	int getConsentManagerProviderId();

	int getConsentManagerProviderVersion();

	int getConsentScreen();

	String getConsentLanguage();

	int getVendorListVersion();

	int getPolicyVersion();

	boolean isServiceSpecific();

	boolean isUseNonStandardStacks();

	boolean isPurposeOneTreatment();

	String getPublisherCountryCode();

	boolean isSpecialFeatureOptedIn(final int specialFeature);

	IntStream getAllOptedInSpecialFeatures();

	boolean isPurposeConsented(final int purpose);

	IntStream getAllConsentedPurposes();

	boolean isPurposeLegitimateInterest(final int purpose);

	IntStream getAllLegitimatePurposes();

	boolean isVendorConsented(final int vendor);

	IntStream getAllConsentedVendors();

	boolean isVendorLegitimateInterestEstablished(final int vendor);

	IntStream getAllLegitimateInterestVendors();

	RestrictionType getVendorRestrictionType(final int purpose, final int vendor);
}
