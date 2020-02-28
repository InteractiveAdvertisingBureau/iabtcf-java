package com.iabtcf.v2;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
