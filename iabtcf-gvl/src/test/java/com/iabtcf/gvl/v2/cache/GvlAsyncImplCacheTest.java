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

import com.iabtcf.gvl.v2.dao.GvlVendorDataMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class GvlAsyncImplCacheTest {

    @Test
    public void testGetVendorList() throws ExecutionException, InterruptedException {
        int version = 26;
        int vendorId = 8;
        Map<Integer, GvlVendorDataMap> defaultObject = new HashMap<>();
        if (GvlAsyncImplCache.INSTANCE.gvlCache.getIfPresent(defaultObject) == null) {
            GvlVendorDataMap gvlVendorDataMap = GvlAsyncImplCache.INSTANCE.getVendorList(version, vendorId);
            Assert.assertNull(gvlVendorDataMap);
        }
    }

    @Test(expected = TimeoutException.class)
    public void testGetVendorListWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        int version = 26;
        int vendorId = 8;
        GvlVendorDataMap gvlVendorDataMap = GvlAsyncImplCache.INSTANCE.getVendorList(version, vendorId, 10);
        while (true) {
            try {
                Assert.assertNotNull(gvlVendorDataMap);
                Assert.assertEquals(gvlVendorDataMap.getPurposesMap().size(), 3);
                Assert.assertEquals(gvlVendorDataMap.getPurposesMap().get(1).intValue(), 1);
                Assert.assertEquals(gvlVendorDataMap.getPurposesMap().get(3).intValue(), 3);
                Assert.assertEquals(gvlVendorDataMap.getLegIntPurposesMap().size(), 4);
                Assert.assertEquals(gvlVendorDataMap.getLegIntPurposesMap().get(2).intValue(), 2);
                Assert.assertEquals(gvlVendorDataMap.getLegIntPurposesMap().get(9).intValue(), 9);
                Assert.assertEquals(gvlVendorDataMap.getFlexiblePurposesMap().size(), 2);
                Assert.assertEquals(gvlVendorDataMap.getFlexiblePurposesMap().get(2).intValue(), 2);
                Assert.assertEquals(gvlVendorDataMap.getFlexiblePurposesMap().get(9).intValue(), 9);
                break;
            } finally {
                // continue;
            }
        }
    }
}
