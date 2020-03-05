package com.iabtcf.v1;

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

import java.util.Set;

import com.iabtcf.model.TCModel;

public interface TCModelV1 extends TCModel {

    /** @return the Consent Manager Provider ID that last updated the consent string */
    int cmpId();

    /** @return the Consent Manager Provider version */
    int cmpVersion();

    /** @return the screen number in the CMP where consent was given */
    int consentScreen();

    /** @return the two-letter ISO639-1 language code that CMP asked for consent in */
    String consentLanguage();

    /** @return version of vendor list used in most recent consent string update. */
    int vendorListVersion();

    /** @return the set of purpose id's which are permitted according to this consent string */
    Set<Integer> allowedPurposeIds();

    /** @return the set of allowed purposes which are permitted according to this consent string */
    Set<Purpose> allowedPurposes();

    /** @return the maximum VendorId for which consent values are given. */
    int maxVendorId();

    /**
     * Check whether purpose with specified ID is allowed
     *
     * @param purposeId purpose ID
     * @return true if purpose is allowed in this consent, false otherwise
     */
    boolean isPurposeAllowed(int purposeId);

    /**
     * Check whether specified purpose is allowed
     *
     * @param purpose purpose to check
     * @return true if purpose is allowed in this consent, false otherwise
     */
    boolean isPurposeAllowed(Purpose purpose);

    /**
     * Check whether vendor with specified ID is allowd
     *
     * @param vendorId vendor ID
     * @return a boolean describing if a user has consented to a particular vendor
     */
    boolean isVendorAllowed(int vendorId);

    Set<Integer> allowedVendorIds();
}
