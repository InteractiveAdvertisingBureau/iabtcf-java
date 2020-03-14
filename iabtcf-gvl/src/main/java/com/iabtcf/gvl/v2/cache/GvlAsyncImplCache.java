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

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iabtcf.gvl.v2.dao.GvlVendorDataMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A reference implementation of GvlCache that builds cache in Asynchronous mode using the
 * iab global vendor list url
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GvlAsyncImplCache extends GvlAbstractCache {

    public static final GvlAsyncImplCache INSTANCE = new GvlAsyncImplCache();

    AsyncCache<Integer, Map<Integer, GvlVendorDataMap>> gvlCache = Caffeine.newBuilder()
        .expireAfterWrite(396, TimeUnit.DAYS)
        .maximumSize(200L)
        .buildAsync();

    /**
     * Gets the global vendor list for a specific vendor list version and a specific vendor id
     * In this Async cache, chances are the first event that triggers the cache building
     * will get a null vendor list
     *
     * @param version vendor list version (usually parsed from tcf string)
     * @param vendorId id of the vendor in the global vendor list
     * @return null or {@link GvlVendorDataMap} object representing a vendor where all the fields are
     *         translated into various {@link Map} fields
     * @see GvlVendorDataMap
     */
    @Override
    public GvlVendorDataMap getVendorList(int version, int vendorId) {
        CompletableFuture<Map<Integer, GvlVendorDataMap>> vendorCacheFuture = gvlCache.get(version, k -> buildCache(version));
        Map<Integer, GvlVendorDataMap> vendorCache = vendorCacheFuture.getNow(null);
        if (vendorCache != null) {
            return vendorCache.get(vendorId);
        }
        return null;
        // FIXME: Should we throw an exception if we can't build cache?
    }

    /**
     * Gets the global vendor list for a specific vendor list version and a specific vendor id
     * In this Async cache, we provide a timeout for building of cache when its triggered by the
     * first event
     *
     * @param version vendor list version (usually parsed from tcf string)
     * @param vendorId id of the vendor in the global vendor list
     * @param timeoutInMillis number of milliseconds to wait for building the cache
     * @return null or {@link GvlVendorDataMap} object representing a vendor where all the fields are
     *         translated into various {@link Map} fields
     * @see GvlVendorDataMap
     */
    public GvlVendorDataMap getVendorList(int version, int vendorId, int timeoutInMillis)
        throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Map<Integer, GvlVendorDataMap>> vendorCacheFuture = gvlCache.get(version, k -> buildCache(version));
        Map<Integer, GvlVendorDataMap> vendorCache = vendorCacheFuture.get(timeoutInMillis, TimeUnit.MILLISECONDS);
        if (vendorCache != null) {
            return vendorCache.get(vendorId);
        }
        return null;
        // FIXME: Should we throw an exception if we can't build cache?
    }
}
