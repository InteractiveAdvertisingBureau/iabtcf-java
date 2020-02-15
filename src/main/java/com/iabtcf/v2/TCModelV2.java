package com.iabtcf.v2;

import java.util.List;
import java.util.Set;

import com.iabtcf.model.TCModel;

public interface TCModelV2 extends TCModel {
    /**
     * @return Id of the consent manager provider
     */
    int consentManagerProviderId();

    /**
     * @return Consent Manager Provider version
     */
    int consentManagerProviderVersion();

    /**
     * @return Screen number in the CMP where consent was given
     */
    int consentScreen();

    /**
     * @return Two-letter ISO639-1 language code that CMP asked for consent in
     */
    String consentLanguage();

    /**
     * @return Version of vendor list used in most recent consent string update
     */
    int vendorListVersion();

    /**
     * @return version of policy used within GVL
     */
    int policyVersion();

    /**
     * @return whether the signals were from service-specific storage
     */
    boolean isServiceSpecific();

    /**
     * @return if that a publisher-run CMP – that is still IAB Europe registered is using customized
     * Stack descriptions and not the standard stack descriptions.
     */
    boolean useNonStandardStacks();

    /**
     * @return a set of special features opt ins
     */
    Set<Integer> specialFeatureOptIns();

    /**
     * @return a set of purposes consent
     */
    Set<Integer> purposesConsent();

    /**
     * @return a set purposes legitimate interest
     */
    Set<Integer> purposesLITransparency();

    /**
     * @return true if Purpose 1 was NOT disclosed at all
     */
    boolean isPurposeOneTreatment();

    /**
     * @return Two-letter ISO639-1 country code of the country that determines legislation of
     * reference. Commonly, this corresponds to the country in which the publisher’s business
     * entity is established
     */
    String publisherCountryCode();

    Set<Integer> vendorConsents();

    Set<Integer> vendorLegitimateInterests();

    List<PublisherRestriction> publisherRestrictions();

    Set<Integer> disclosedVendors();

    Set<Integer> allowedVendors();

    Set<Integer> publisherPurposesConsent();

    Set<Integer> publisherPurposesLITransparency();

    Set<Integer> customPurposesConsent();

    Set<Integer> customPurposesLITransparency();
}
