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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.iabtcf.gvl.Purpose;
import com.iabtcf.gvl.SpecialFeature;
import com.iabtcf.gvl.SpecialPurpose;
import com.iabtcf.gvl.Stack;
import com.iabtcf.gvl.Vendor;

public class Gvl implements com.iabtcf.gvl.Gvl {

    private int gvlSpecificationVersion;
    private int vendorListVersion;
    private int tcfPolicyVersion;
    private Instant lastUpdated;
    private Map<Integer, com.iabtcf.gvl.Purpose> purposes;
    private Map<Integer, com.iabtcf.gvl.SpecialPurpose> specialPurposes;
    private Map<Integer, com.iabtcf.gvl.Feature> features;
    private Map<Integer, com.iabtcf.gvl.SpecialFeature> specialFeatures;
    private Map<Integer, com.iabtcf.gvl.Stack> stacks;
    private Map<Integer, com.iabtcf.gvl.Vendor> vendors;

    /**
     * A Global Vendor List Specification Version
     *
     * @return global vendor list specification version
     */
    @Override
    public int getGvlSpecificationVersion() {
        return gvlSpecificationVersion;
    }

    /**
     * A Global Vendor List version incremented with each published file change
     *
     * @return global vendor list version
     */
    @Override
    public int getVendorListVersion() {
        return vendorListVersion;
    }

    /**
     * A TCF Policy Version. The TCF MO will increment this value whenever a GVL change (such as
     * adding a new Purpose or Feature or a change in Purpose wording) legally invalidates existing
     * TC Strings and requires CMPs to re-establish transparency and consent from users. TCF Policy
     * changes should be relatively infrequent and only occur when necessary to support changes in
     * global mandate. If the policy version number in the latest GVL is different from the value in
     * your TC String, then you need to re-establish transparency and consent for that user. A
     * version 1 format TC String is considered to have a version value of 1.
     *
     * @return tcf policy version
     */
    @Override
    public int getTcfPolicyVersion() {
        return tcfPolicyVersion;
    }

    /**
     * Last Updated Date
     *
     * @return timestamp when the record was last updated
     */
    @Override
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    /**
     * A list of standard purposes
     *
     * @return A {@link List} of standard {@link Purpose} objects
     */
    @Override
    public List<Purpose> getPurposes() {
        return new ArrayList<>(purposes.values());
    }

    /**
     * A list of special purposes
     *
     * @return A {@link List} of {@link SpecialPurpose} objects
     */
    @Override
    public List<SpecialPurpose> getSpecialPurposes() {
        return new ArrayList<>(specialPurposes.values());
    }

    /**
     * A list of standard features
     *
     * @return A {@link List} of standard {@link com.iabtcf.gvl.Feature} objects
     */
    @Override
    public List<com.iabtcf.gvl.Feature> getFeatures() {
        return new ArrayList<>(features.values());
    }

    /**
     * A list of special features
     *
     * @return A {@link List} of special {@link SpecialFeature} objects
     */
    @Override
    public List<SpecialFeature> getSpecialFeatures() {
        return new ArrayList<>(specialFeatures.values());
    }

    /**
     * A list of stacks
     *
     * @return A {@link List} of {@link Stack} objects
     */
    @Override
    public List<Stack> getStacks() {
        return new ArrayList<>(stacks.values());
    }

    /**
     * A list of vendors
     *
     * @return A {@link List} of {@link Vendor} objects
     */
    @Override
    public List<Vendor> getVendors() {
        return new ArrayList<>(vendors.values());
    }

    /**
     * Return the vendor object for a given id
     *
     * @param vendorId vendor id
     * @return A {@link Vendor} object
     */
    @Override
    public Vendor getVendor(int vendorId) {
        return vendors.get(vendorId);
    }
}
