package com.iabtcf.gvl;

/*-
 * #%L
 * IAB TCF Java GVL
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

public interface Gvl {

    /**
     * A Global Vendor List Specification Version
     *
     * @return global vendor list specification version
     */
    int getGvlSpecificationVersion();

    /**
     * A Global Vendor List version incremented with each published file change
     *
     * @return global vendor list version
     */
    int getVendorListVersion();

    /**
     * A TCF Policy Version.
     *
     * @return tcf policy version
     */
    int getTcfPolicyVersion();

    /**
     * Last Updated Date
     *
     * @return timestamp when the record was last updated
     */
    Instant getLastUpdated();

    /**
     * A list of standard purposes
     *
     * @return A {@link List} of standard {@link Purpose} objects
     */
    List<Purpose> getPurposes();

    /**
     * A list of special purposes
     *
     * @return A {@link List} of {@link SpecialPurpose} objects
     */
    List<SpecialPurpose> getSpecialPurposes();

    /**
     * A list of standard features
     *
     * @return A {@link List} of standard {@link Feature} objects
     */
    List<Feature> getFeatures();

    /**
     * A list of special features
     *
     * @return A {@link List} of special {@link SpecialFeature} objects
     */
    List<SpecialFeature> getSpecialFeatures();

    /**
     * A list of stacks
     *
     * @return A {@link List} of {@link Stack} objects
     */
    List<Stack> getStacks();

    /**
     * A list of vendors
     *
     * @return A {@link List} of {@link Vendor} objects
     */
    List<Vendor> getVendors();

    /**
     * Return the vendor object for a given id
     *
     * @param vendorId vendor id
     * @return A {@link Vendor} object
     */
    Vendor getVendor(int vendorId);
}
