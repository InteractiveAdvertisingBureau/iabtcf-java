package com.iabtcf.gvl.jackson;

/*-
 * #%L
 * IAB TCF Java GVL Jackson
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

import com.iabtcf.gvl.Overflow;

public class Vendor implements com.iabtcf.gvl.Vendor {

    private int id;
    private String name;
    private List<Integer> purposes;
    private List<Integer> legIntPurposes;
    private List<Integer> flexiblePurposes;
    private List<Integer> specialPurposes;
    private List<Integer> features;
    private List<Integer> specialFeatures;
    private String policyUrl;
    private Instant deletedDate;
    private com.iabtcf.gvl.Overflow overflow;

    /**
     * A vendor id: a numeric ID which is incrementally assigned and never re-used – deleted Vendors
     * are just marked as deleted
     *
     * @return vendor id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Name of the vendor
     *
     * @return vendor name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * List of Purposes for which the vendor is requesting consent
     *
     * @return A {@link List} of purpose ids that require consent
     */
    @Override
    public List<Integer> getPurposes() {
        return purposes;
    }

    /**
     * List of Purposes for which the vendor requires to be transparently disclosed as their
     * legitimate interest
     *
     * @return A {@link List} of purpose ids disclosed as legitimate interests
     */
    @Override
    public List<Integer> getLegIntPurposes() {
        return legIntPurposes;
    }

    /**
     * List of purposes where the vendor is flexible regarding the legal basis; they will perform
     * the processing based on consent or a legitimate interest. The 'default' is determined by
     * which of the other two mutually-exclusive purpose fields is used to declare the purpose for
     * the vendor
     *
     * @return A {@link List} of flexible purpose ids
     */
    @Override
    public List<Integer> getFlexiblePurposes() {
        return flexiblePurposes;
    }

    /**
     * List of Special Purposes that the vendor transparently discloses as their legitimate interest
     * that a user has no right to object
     *
     * @return A {@link List} of special purpose ids
     */
    @Override
    public List<Integer> getSpecialPurposes() {
        return specialPurposes;
    }

    /**
     * List of Features the vendor uses across Purposes
     *
     * @return A {@link List} of features
     */
    @Override
    public List<Integer> getFeatures() {
        return features;
    }

    /**
     * List of Special Features the vendor uses across Purposes
     *
     * @return A {@link List} of special features
     */
    @Override
    public List<Integer> getSpecialFeatures() {
        return specialFeatures;
    }

    /**
     * GDPR/privacy policy page URL
     *
     * @return policy url string
     */
    @Override
    public String getPolicyUrl() {
        return policyUrl;
    }

    /**
     * A date string representing when the vendor is deleted from the GVL
     *
     * @return date string
     */
    @Override
    public Optional<Instant> getDeletedDate() {
        return Optional.ofNullable(deletedDate);
    }

    /**
     * object specifying the vendor's http GET request length limit. It is optional. If a vendor
     * entry does not include this attribute then the vendor has no overflow options and none can be
     * inferred.
     *
     * @return A {@link com.iabtcf.gvl.Overflow} object
     */
    @Override
    public Optional<Overflow> getOverflow() {
        return Optional.ofNullable(overflow);
    }

    /**
     * A helper method to check if the vendor is deleted based on the current time (UTC)
     *
     * @return true, if the vendor is deleted
     */
    @Override
    public boolean isDeleted() {
        return Optional.ofNullable(this.deletedDate)
            .map(deletedDate -> !deletedDate.isAfter(Instant.now()))
            .orElse(false);
    }
}
