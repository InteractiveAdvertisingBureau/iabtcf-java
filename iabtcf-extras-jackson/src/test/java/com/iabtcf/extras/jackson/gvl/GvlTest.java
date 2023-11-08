package com.iabtcf.extras.jackson.gvl;

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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.iabtcf.extras.jackson.TestUtil;
import com.iabtcf.extras.jackson.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iabtcf.extras.gvl.Gvl;

public class GvlTest {
    private static Gvl gvl;
    private static Gvl gvlV3;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        Loader loader = new Loader();
        gvl = loader.globalVendorList(TestUtil.getGlobalVendorList());
        gvlV3 = loader.globalVendorList(TestUtil.getGlobalVendorListV3());
    }

    @Test
    public void testGetGvlSpecificationVersion() {
        Assert.assertEquals(2, gvl.getGvlSpecificationVersion());
    }

    @Test
    public void testGetVendorListVersion() {
        Assert.assertEquals(26, gvl.getVendorListVersion());
    }

    @Test
    public void testGetTcfPolicyVersion() {
        Assert.assertEquals(2, gvl.getTcfPolicyVersion());
    }

    @Test
    public void testGetLastUpdated() throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        parser.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date parsed = parser.parse("2020-02-20T16:05:20");
        Assert.assertEquals(parsed.toInstant(), gvl.getLastUpdated());
    }

    @Test
    public void testGetPurposes() {
        Assert.assertEquals(10, gvl.getPurposes().size());
    }

    @Test
    public void testGetSpecialPurposes() {
        Assert.assertEquals(2, gvl.getSpecialPurposes().size());
    }

    @Test
    public void testGetFeatures() {
        Assert.assertEquals(3, gvl.getFeatures().size());
    }

    @Test
    public void testGetSpecialFeatures() {
        Assert.assertEquals(2, gvl.getSpecialFeatures().size());
    }

    @Test
    public void testGetStacks() {
        Assert.assertEquals(37, gvl.getStacks().size());
    }

    @Test
    public void testGetVendors() {
        Assert.assertEquals(3, gvl.getVendors().size());
    }

    @Test
    public void testGvlV3() {
        Assert.assertEquals(3, gvlV3.getGvlSpecificationVersion());
        Assert.assertEquals(15, gvlV3.getVendorListVersion());
        Assert.assertEquals(4, gvlV3.getTcfPolicyVersion());
        Assert.assertEquals(11, gvlV3.getPurposes().size());
        Assert.assertEquals(2, gvlV3.getSpecialPurposes().size());
        Assert.assertEquals(3, gvlV3.getFeatures().size());
        Assert.assertEquals(2, gvlV3.getSpecialFeatures().size());
        Assert.assertEquals(43, gvlV3.getStacks().size());
        Assert.assertEquals(2, gvlV3.getVendors().size());
    }
}
