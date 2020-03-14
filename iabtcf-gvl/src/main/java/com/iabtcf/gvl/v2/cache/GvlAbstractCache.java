package com.iabtcf.gvl.v2.cache;

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

import com.iabtcf.gvl.v2.GvlV2Util;
import com.iabtcf.gvl.v2.dao.GvlData;
import com.iabtcf.gvl.v2.dao.GvlVendorData;
import com.iabtcf.gvl.v2.dao.GvlVendorDataMap;

import java.util.HashMap;
import java.util.Map;

public abstract class GvlAbstractCache implements GvlCache {

    /**
     * Builds the cache of all vendors from the global vendor list for a specific vendor list version
     *
     * @param version vendor list version (usually parsed from tcf string)
     * @return {@link Map} with key as vendorId and value as {@link GvlVendorDataMap} object
     * @see GvlVendorDataMap
     */
    @Override
    public Map<Integer, GvlVendorDataMap> buildCache(final int version) {
        String url = GvlV2Util.DEFAULT_BASE_GVL_URL + version + GvlV2Util.DEFAULT_GVL_URL_SUFFIX;
        GvlData tcfPurposeVendorData = GvlV2Util.initializeVendorList(url);
        return buildVendorsCache(tcfPurposeVendorData);
    }

    /**
     * Helper method to build the cache of all vendors from the global vendor list
     * for a specific vendor list version
     *
     * @param gvlData {@link GvlData} object representing the global vendor list for a specific version
     *                                usually parsed from tcf string
     * @return {@link Map} with key as vendorId and value as {@link GvlVendorDataMap} object
     * @see GvlData
     * @see GvlVendorDataMap
     */
    protected Map<Integer, GvlVendorDataMap> buildVendorsCache(GvlData gvlData) {
        Map<Integer, GvlVendorDataMap> result = new HashMap<>();
        if (gvlData != null) {
            Map<Integer, GvlVendorData> vendors = gvlData.getVendors();
            if (vendors != null) {
                vendors.forEach((k, v) -> {
                    GvlVendorDataMap
                        tcfVendorDataMap =
                        new GvlVendorDataMap(v.getId(), v.getPurposes(), v.getLegIntPurposes(), v.getFlexiblePurposes());
                    result.put(k, tcfVendorDataMap);
                });
            }
        }
        return result;
    }
}
