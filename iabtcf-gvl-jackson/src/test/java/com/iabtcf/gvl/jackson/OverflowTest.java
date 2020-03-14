package com.iabtcf.gvl.jackson;

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

import com.iabtcf.gvl.Overflow;
import com.iabtcf.gvl.Vendor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class OverflowTest {

    private static Overflow vendorEightOverflow;
    private static final int VENDOR_ID_SELECTED_FOR_TEST = 8;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        GvlLoader gvlLoader = new GvlLoader();
        List<Vendor> vendors = gvlLoader.load(GvlUtil.getGlobalVendorList()).getVendors();
        Vendor vendor = vendors.stream().filter(o -> o.getId() == VENDOR_ID_SELECTED_FOR_TEST).findFirst().orElse(null);
        assert vendor != null;
        vendorEightOverflow = vendor.getOverflow();
    }

    @Test
    public void getHttpGetLimit() {
        Assert.assertEquals(32, vendorEightOverflow.getHttpGetLimit());
    }
}
