package com.iabtcf.extras.gvl;

/*-
 * #%L
 * IAB TCF Java GVL and CMP List
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface Vendor {

    /**
     * A vendor id: a numeric ID which is incrementally assigned and never re-used – deleted Vendors
     * are just marked as deleted
     *
     * @return vendor id
     */
    int getId();

    /**
     * Name of the vendor
     *
     * @return vendor name
     */
    String getName();

    /**
     * List of Purposes for which the vendor is requesting consent
     *
     * @return A {@link List} of purpose ids that require consent
     */
    List<Integer> getPurposes();

    /**
     * List of Purposes for which the vendor requires to be transparently disclosed as their
     * legitimate interest
     *
     * @return A {@link List} of purpose ids disclosed as legitimate interests
     */
    List<Integer> getLegIntPurposes();

    /**
     * List of purposes where the vendor is flexible regarding the legal basis; they will perform
     * the processing based on consent or a legitimate interest. The 'default' is determined by
     * which of the other two mutually-exclusive purpose fields is used to declare the purpose for
     * the vendor
     *
     * @return A {@link List} of flexible purpose ids
     */
    List<Integer> getFlexiblePurposes();

    /**
     * List of Special Purposes that the vendor transparently discloses as their legitimate interest
     * that a user has no right to object
     *
     * @return A {@link List} of special purpose ids
     */
    List<Integer> getSpecialPurposes();

    /**
     * List of Features the vendor uses across Purposes
     *
     * @return A {@link List} of features
     */
    List<Integer> getFeatures();

    /**
     * List of Special Features the vendor uses across Purposes
     *
     * @return A {@link List} of special features
     */
    List<Integer> getSpecialFeatures();

    /**
     * GDPR/privacy policy page URL
     *
     * @return policy url string
     */
    String getPolicyUrl();

    /**
     * If available, a date/time after which the vendor is deleted from the GVL
     *
     * @return {@link Optional<Instant>} time after which the vendor is considered deleted
     */
    Optional<Instant> getDeletedDate();

    /**
     * object specifying the vendor's http GET request length limit. It is optional. If a vendor
     * entry does not include this attribute then the vendor has no overflow options and none can be
     * inferred.
     *
     * @return A {@link Overflow} object
     */
    Optional<Overflow> getOverflow();

    /**
     * Check if the vendor is deleted based on the current time (UTC)
     *
     * @return true, if the vendor is deleted
     */
    boolean isDeleted();

    /**
     * The number of seconds representing the longest potential duration for cookie storage on a device.
     * If a Vendor uses multiple cookies with differing durations, cookieMaxAgeSeconds represents the cookie with
     * the longest duration. Note: cookies are the only method of storage or device access that permit a predictable
     * duration to be set.
     * This is required only if usesCookies is set to true, else optional
     * @return The number, in seconds, of the longest potential duration for storage on a device, as set when using
     * the cookie method of storage. A negative number or a 0 indicate session storage similar to the Set-Cookie spec.
     * A "-100" value no longer indicates no cookie usage.
     * Note: this only includes what is declared when the storage is set and does not consider duration extensions
     * should storage be refreshed
     */
    Optional<Long> getCookieMaxAgeSeconds();

    /**
     * This boolean field indicates whether the vendor uses cookie storage (session or otherwise).
     * @return True indicates cookie storage is used
     */
    boolean getUsesCookies();

    /**
     * This true or false field indicates whether any cookies in scope for cookieMaxAgeSeconds are refreshed after
     * being initially set.
     * @return True indicates the vendor refreshes this cookie
     */
    boolean getHasCookieRefresh();

    /**
     * This boolean field indicates whether the vendor uses other, non-cookie methods of storage or accessing
     * information already stored on a user’s device. Examples of non-cookie storage and access may be localStorage,
     * indexDB, mobile ad IDs, etc.
     * @return True indicates non-cookie access is used
     */
    boolean getUsesNonCookieAccess();

    /**
     * Link to a recommended, vendor-hosted, secure URL for disclosing additional storage information
     * @return Location of vendor-hosted deviceStorage.json file
     */
    Optional<String> getDeviceStorageDisclosureUrl();
}
