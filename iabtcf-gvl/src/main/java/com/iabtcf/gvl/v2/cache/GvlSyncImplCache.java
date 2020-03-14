package com.iabtcf.gvl.v2.cache;

/*-
 * #%L
 * iabtcf-gvl
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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iabtcf.gvl.v2.GvlV2Util;
import com.iabtcf.gvl.v2.dao.GvlData;
import com.iabtcf.gvl.v2.dao.GvlVendorDataMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A reference implementation of GvlCache that builds cache in synchronous mode using the
 * iab global vendor list url
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GvlSyncImplCache extends GvlAbstractCache {

    public static final GvlSyncImplCache INSTANCE = new GvlSyncImplCache();

    Cache<Integer, Map<Integer, GvlVendorDataMap>> gvlCache = Caffeine.newBuilder()
        .expireAfterWrite(396, TimeUnit.DAYS)
        .maximumSize(200L)
        .build();

    /**
     * Gets the global vendor list for a specific vendor list version and a specific vendor id
     *
     * @param version vendor list version (usually parsed from tcf string)
     * @param vendorId id of the vendor in the global vendor list
     * @return null or {@link GvlVendorDataMap} object representing a vendor where all the fields are
     *         translated into various {@link Map} fields
     * @see GvlVendorDataMap
     */
    @Override
    public GvlVendorDataMap getVendorList(int version, int vendorId) {
        Map<Integer, GvlVendorDataMap> vendorCache = gvlCache.get(version, k -> buildCache(version));
        if (vendorCache != null) {
            return vendorCache.get(vendorId);
        }
        return null;
        // FIXME: Should we throw an exception if we can't build cache?
    }

    /*
     * The following methods are mostly used for test cases to pass along a static gvl json file
     */
    protected GvlVendorDataMap getVendorList(byte[] vendorListJson, int version, int vendorId) {
        Map<Integer, GvlVendorDataMap> vendorCache = gvlCache.get(version, k -> {
            try {
                return buildCache(vendorListJson);
            } catch (IOException e) {
                // FIXME: Should we throw an exception if we can't build cache?
                return null;
            }
        });
        if (vendorCache != null) {
            return vendorCache.get(vendorId);
        }
        // FIXME: Should we throw an exception if can't build cache
        return null;
    }

    private Map<Integer, GvlVendorDataMap> buildCache(byte[] vendorListJson) throws IOException {
        GvlData gvlData = GvlV2Util.initializeVendorList(vendorListJson);
        return buildVendorsCache(gvlData);
    }
}

