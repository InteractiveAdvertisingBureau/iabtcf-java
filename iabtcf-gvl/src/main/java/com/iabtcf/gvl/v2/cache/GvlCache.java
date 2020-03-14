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

import com.iabtcf.gvl.v2.dao.GvlVendorDataMap;

import java.util.Map;

public interface GvlCache {

    /**
     * Builds the global vendor list cache for a specific version
     *
     * @param version the global vendor list version usually parsed from tcf string
     * @return {@link Map} with key as vendorId and value as {@link GvlVendorDataMap} object
     * @see GvlVendorDataMap
     */
    Map<Integer, GvlVendorDataMap> buildCache(int version);

    /**
     * Gets the global vendor list entry for a specific  for a specific version
     *
     * @param version the global vendor list version usually parsed from tcf string
     * @param vendorId id of the vendor in the global vendor list
     * @return {@link GvlVendorDataMap} object representing a vendor where all the fields are
     *         translated into various {@link Map} fields
     * @see GvlVendorDataMap
     */
    GvlVendorDataMap getVendorList(int version, int vendorId);
}
