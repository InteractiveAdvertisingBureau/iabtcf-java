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
import com.iabtcf.gvl.v2.dao.GvlVendorDataMap;
import org.junit.Test;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GvlSyncImplCacheTest {

    @Test
    public void testGetVendorList() throws IOException {
        int version = 26;
        int vendorId = 8;
        GvlVendorDataMap
            gvlVendorDataMap =
            GvlSyncImplCache.INSTANCE.getVendorList(version, vendorId);
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
    }

    @Test
    public void whenParsingAGVLWithDeletedVendorsThenTheyShouldNotBePresentInTheMinimizedList() throws IOException {
        GvlData vendorData = GvlV2Util.initializeVendorList(getGlobalVendorList());
        Assert.assertNull(vendorData.getVendors().get(512));
        Assert.assertNotNull(vendorData.getVendors().get(8));
    }

    private byte[] getGlobalVendorList() throws IOException {
        ClassLoader loader = getClass().getClassLoader();
        InputStream gvlStream = loader.getResourceAsStream("gvl.json");
        return readFromInputStream(gvlStream).getBytes();
    }

    private String readFromInputStream(InputStream inputStream)
        throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                 = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
