package com.iabtcf;

import java.time.Instant;
import java.util.Set;

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

	Set<Integer> getSpecialFeaturesOptInts();

	Set<Integer> getPurposesConsent();

	Set<Integer> getPurposesLITransparency();

	Set<Integer> getVendorConsents();

	Set<Integer> getVendorLegitimateInterests();

	Set<PublisherRestriction> getPublisherRestrictions();
}
