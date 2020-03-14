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

import com.iabtcf.gvl.Gvl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GvlTest {
    private static Gvl gvl;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        GvlLoader gvlLoader = new GvlLoader();
        gvl = gvlLoader.load(GvlUtil.getGlobalVendorList());
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
        Assert.assertEquals(parsed, gvl.getLastUpdated());
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
}
