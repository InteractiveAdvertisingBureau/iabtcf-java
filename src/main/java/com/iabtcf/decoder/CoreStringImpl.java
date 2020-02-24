package com.iabtcf.decoder;

import com.iabtcf.CoreString;
import com.iabtcf.PublisherRestriction;
import com.iabtcf.RestrictionType;

import java.time.Instant;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author evanwht1
 */
public class CoreStringImpl implements CoreString {

	private final int version;
	private final Instant consentRecordCreated;
	private final Instant consentRecordLastUpdated;
	private final int consentManagerProviderId;
	private final int consentManagerProviderVersion;
	private final int consentScreen;
	private final String consentLanguage;
	private final int vendorListVersion;
	private final int policyVersion;
	private final boolean isServiceSpecific;
	private final boolean useNonStandardStacks;
	private final boolean isPurposeOneTreatment;
	private final String publisherCountryCode;
	private final BitSet specialFeaturesOptInts;
	private final BitSet purposesConsent;
	private final BitSet purposesLITransparency;
	private final BitSet vendorConsents;
	private final BitSet vendorLegitimateInterests;
	private final Map<Integer, PublisherRestriction> publisherRestrictions;

	private CoreStringImpl(final Builder builder) {
		version = builder.version;
		consentRecordCreated = builder.consentRecordCreated;
		consentRecordLastUpdated = builder.consentRecordLastUpdated;
		consentManagerProviderId = builder.consentManagerProviderId;
		consentManagerProviderVersion = builder.consentManagerProviderVersion;
		consentScreen = builder.consentScreen;
		consentLanguage = builder.consentLanguage;
		vendorListVersion = builder.vendorListVersion;
		policyVersion = builder.policyVersion;
		isServiceSpecific = builder.isServiceSpecific;
		useNonStandardStacks = builder.useNonStandardStacks;
		isPurposeOneTreatment = builder.isPurposeOneTreatment;
		publisherCountryCode = builder.publisherCountryCode;
		specialFeaturesOptInts = builder.specialFeaturesOptInts;
		purposesConsent = builder.purposesConsent;
		purposesLITransparency = builder.purposesLITransparency;
		vendorConsents = builder.vendorConsents;
		vendorLegitimateInterests = builder.vendorLegitimateInterests;
		publisherRestrictions = builder.publisherRestrictions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public Instant getConsentRecordCreated() {
		return consentRecordCreated;
	}

	@Override
	public Instant getConsentRecordLastUpdated() {
		return consentRecordLastUpdated;
	}

	@Override
	public int getConsentManagerProviderId() {
		return consentManagerProviderId;
	}

	@Override
	public int getConsentManagerProviderVersion() {
		return consentManagerProviderVersion;
	}

	@Override
	public int getConsentScreen() {
		return consentScreen;
	}

	@Override
	public String getConsentLanguage() {
		return consentLanguage;
	}

	@Override
	public int getVendorListVersion() {
		return vendorListVersion;
	}

	@Override
	public int getPolicyVersion() {
		return policyVersion;
	}

	@Override
	public boolean isServiceSpecific() {
		return isServiceSpecific;
	}

	@Override
	public boolean isUseNonStandardStacks() {
		return useNonStandardStacks;
	}

	@Override
	public boolean isPurposeOneTreatment() {
		return isPurposeOneTreatment;
	}

	@Override
	public String getPublisherCountryCode() {
		return publisherCountryCode;
	}

	@Override
	public boolean isSpecialFeatureOptedIn(final int specialFeature) {
		return specialFeaturesOptInts.get(specialFeature);
	}

	@Override
	public IntStream getAllOptedInSpecialFeatures() {
		return specialFeaturesOptInts.stream();
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
		return purposesLITransparency.get(purpose);
	}

	@Override
	public IntStream getAllLegitimateInterestPurposes() {
		return purposesLITransparency.stream();
	}

	@Override
	public boolean isVendorConsented(final int vendor) {
		return vendorConsents.get(vendor);
	}

	@Override
	public IntStream getAllConsentedVendors() {
		return vendorConsents.stream();
	}

	@Override
	public boolean isVendorLegitimateInterest(final int vendor) {
		return vendorLegitimateInterests.get(vendor);
	}

	@Override
	public IntStream getAllLegitimateInterestVendors() {
		return vendorLegitimateInterests.stream();
	}

	@Override
	public RestrictionType getVendorRestrictionType(final int purpose, final int vendor) {
		if (publisherRestrictions.containsKey(purpose) && publisherRestrictions.get(purpose).isVendorIncluded(vendor)) {
			return publisherRestrictions.get(purpose).getRestrictionType();
		}
		return RestrictionType.UNDEFINED;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final CoreStringImpl that = (CoreStringImpl) o;
		return version == that.version &&
		       consentManagerProviderId == that.consentManagerProviderId &&
		       consentManagerProviderVersion == that.consentManagerProviderVersion &&
		       consentScreen == that.consentScreen &&
		       vendorListVersion == that.vendorListVersion &&
		       policyVersion == that.policyVersion &&
		       isServiceSpecific == that.isServiceSpecific &&
		       useNonStandardStacks == that.useNonStandardStacks &&
		       isPurposeOneTreatment == that.isPurposeOneTreatment &&
		       Objects.equals(consentRecordCreated, that.consentRecordCreated) &&
		       Objects.equals(consentRecordLastUpdated, that.consentRecordLastUpdated) &&
		       Objects.equals(consentLanguage, that.consentLanguage) &&
		       Objects.equals(publisherCountryCode, that.publisherCountryCode) &&
		       Objects.equals(specialFeaturesOptInts, that.specialFeaturesOptInts) &&
		       Objects.equals(purposesConsent, that.purposesConsent) &&
		       Objects.equals(purposesLITransparency, that.purposesLITransparency) &&
		       Objects.equals(vendorConsents, that.vendorConsents) &&
		       Objects.equals(vendorLegitimateInterests, that.vendorLegitimateInterests) &&
		       Objects.equals(publisherRestrictions, that.publisherRestrictions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(version,
				consentRecordCreated,
				consentRecordLastUpdated,
				consentManagerProviderId,
				consentManagerProviderVersion,
				consentScreen,
				consentLanguage,
				vendorListVersion,
				policyVersion,
				isServiceSpecific,
				useNonStandardStacks,
				isPurposeOneTreatment,
				publisherCountryCode,
				specialFeaturesOptInts,
				purposesConsent,
				purposesLITransparency,
				vendorConsents,
				vendorLegitimateInterests,
				publisherRestrictions);
	}

	@Override
	public String toString() {
		return "CoreString{" +
		       "version: " + version +
		       ", consentRecordCreated: " + consentRecordCreated +
		       ", consentRecordLastUpdated: " + consentRecordLastUpdated +
		       ", consentManagerProviderId: " + consentManagerProviderId +
		       ", consentManagerProviderVersion: " + consentManagerProviderVersion +
		       ", consentScreen: " + consentScreen +
		       ", consentLanguage: " + consentLanguage +
		       ", vendorListVersion: " + vendorListVersion +
		       ", policyVersion: " + policyVersion +
		       ", isServiceSpecific: " + isServiceSpecific +
		       ", useNonStandardStacks: " + useNonStandardStacks +
		       ", isPurposeOneTreatment: " + isPurposeOneTreatment +
		       ", publisherCountryCode: " + publisherCountryCode +
		       ", specialFeaturesOptInts: " + specialFeaturesOptInts +
		       ", purposesConsent: " + purposesConsent +
		       ", purposesLITransparency: " + purposesLITransparency +
		       ", vendorConsents: " + vendorConsents +
		       ", vendorLegitimateInterests: " + vendorLegitimateInterests +
		       ", publisherRestrictions: " + publisherRestrictions +
		       '}';
	}

	public static final class Builder {

		private int version;
		private Instant consentRecordCreated;
		private Instant consentRecordLastUpdated;
		private int consentManagerProviderId;
		private int consentManagerProviderVersion;
		private int consentScreen;
		private String consentLanguage;
		private int vendorListVersion;
		private int policyVersion;
		private boolean isServiceSpecific;
		private boolean useNonStandardStacks;
		private boolean isPurposeOneTreatment;
		private String publisherCountryCode;
		private BitSet specialFeaturesOptInts;
		private BitSet purposesConsent;
		private BitSet purposesLITransparency;
		private BitSet vendorConsents;
		private BitSet vendorLegitimateInterests;
		private Map<Integer, PublisherRestriction> publisherRestrictions;

		private Builder() {}

		public Builder version(final int val) {
			version = val;
			return this;
		}

		public Builder consentRecordCreated(final Instant val) {
			consentRecordCreated = val;
			return this;
		}

		public Builder consentRecordLastUpdated(final Instant val) {
			consentRecordLastUpdated = val;
			return this;
		}

		public Builder consentManagerProviderId(final int val) {
			consentManagerProviderId = val;
			return this;
		}

		public Builder consentManagerProviderVersion(final int val) {
			consentManagerProviderVersion = val;
			return this;
		}

		public Builder consentScreen(final int val) {
			consentScreen = val;
			return this;
		}

		public Builder consentLanguage(final String val) {
			consentLanguage = val;
			return this;
		}

		public Builder vendorListVersion(final int val) {
			vendorListVersion = val;
			return this;
		}

		public Builder policyVersion(final int val) {
			policyVersion = val;
			return this;
		}

		public Builder isServiceSpecific(final boolean val) {
			isServiceSpecific = val;
			return this;
		}

		public Builder useNonStandardStacks(final boolean val) {
			useNonStandardStacks = val;
			return this;
		}

		public Builder isPurposeOneTreatment(final boolean val) {
			isPurposeOneTreatment = val;
			return this;
		}

		public Builder publisherCountryCode(final String val) {
			publisherCountryCode = val;
			return this;
		}

		public Builder specialFeaturesOptInts(final BitSet val) {
			specialFeaturesOptInts = val;
			return this;
		}

		public Builder purposesConsent(final BitSet val) {
			purposesConsent = val;
			return this;
		}

		public Builder purposesLITransparency(final BitSet val) {
			purposesLITransparency = val;
			return this;
		}

		public Builder vendorConsents(final BitSet val) {
			vendorConsents = val;
			return this;
		}

		public Builder vendorLegitimateInterests(final BitSet val) {
			vendorLegitimateInterests = val;
			return this;
		}

		public Builder publisherRestrictions(final Map<Integer, PublisherRestriction> val) {
			publisherRestrictions = val;
			return this;
		}

		public CoreStringImpl build() {
			return new CoreStringImpl(this);
		}
	}
}
