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

import java.time.Instant;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iabtcf.extras.gvl.Gvl;
import com.iabtcf.extras.gvl.Vendor;
import com.iabtcf.extras.jackson.Loader;
import com.iabtcf.extras.jackson.TestUtil;

public class VendorTest {

    private static Vendor vendorEight;
    private static Vendor vendorTwo;
    private static final int VENDOR_ID_SELECTED_FOR_TEST = 8;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Loader loader = new Loader();
        Gvl gvl = loader.globalVendorList(TestUtil.getGlobalVendorList());
        vendorEight = gvl.getVendor(VENDOR_ID_SELECTED_FOR_TEST);
        vendorTwo = gvl.getVendor(2);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(8, vendorEight.getId());
    }

    @Test
    public void testGetName() {
        String expectedName = "Emerse Sverige AB";
        Assert.assertEquals(expectedName, vendorEight.getName());
    }

    @Test
    public void testGetPurposes() {
        Assert.assertNotNull(vendorEight.getPurposes());
        Assert.assertEquals(3, vendorEight.getPurposes().size());
        Assert.assertEquals(Arrays.asList(1, 3, 4), vendorEight.getPurposes());
    }

    @Test
    public void testGetLegIntPurposes() {
        Assert.assertNotNull(vendorEight.getLegIntPurposes());
        Assert.assertEquals(4, vendorEight.getLegIntPurposes().size());
        Assert.assertEquals(Arrays.asList(2, 7, 8, 9), vendorEight.getLegIntPurposes());
    }

    @Test
    public void testGetFlexiblePurposes() {
        Assert.assertNotNull(vendorEight.getFlexiblePurposes());
        Assert.assertEquals(2, vendorEight.getFlexiblePurposes().size());
        Assert.assertEquals(Arrays.asList(2, 9), vendorEight.getFlexiblePurposes());
    }

    @Test
    public void testGetSpecialPurposes() {
        Assert.assertNotNull(vendorEight.getSpecialPurposes());
        Assert.assertEquals(2, vendorEight.getSpecialPurposes().size());
        Assert.assertEquals(Arrays.asList(1, 2), vendorEight.getSpecialPurposes());
    }

    @Test
    public void testGetFeatures() {
        Assert.assertNotNull(vendorEight.getFeatures());
        Assert.assertEquals(2, vendorEight.getFeatures().size());
        Assert.assertEquals(Arrays.asList(1, 2), vendorEight.getFeatures());
    }

    @Test
    public void testGetSpecialFeatures() {
        Assert.assertNotNull(vendorEight.getSpecialFeatures());
        Assert.assertEquals(0, vendorEight.getSpecialFeatures().size());
    }

    @Test
    public void testGetPolicyUrl() {
        String expectedPolicyUrl = "https://www.emerse.com/privacy-policy/";
        Assert.assertEquals(expectedPolicyUrl, vendorEight.getPolicyUrl());
    }

    @Test
    public void testGetDeletedDate() {
        Assert.assertEquals(Instant.parse("2020-06-28T00:00:00Z"), vendorEight.getDeletedDate().get());
    }

    @Test
    public void testGetOverflow() {
        Assert.assertNotNull(vendorEight.getOverflow());
    }

    @Test
    public void testIsDeleted() {
        Assert.assertTrue(vendorEight.isDeleted());
    }

    @Test
    public void testCookieMaxAgeSeconds() {
        long expectedCookieMaxAgeSeconds = 31557600000L;
        Assert.assertTrue(vendorEight.getCookieMaxAgeSeconds().isPresent());
        Assert.assertEquals(expectedCookieMaxAgeSeconds, vendorEight.getCookieMaxAgeSeconds().get().longValue());
    }

    @Test
    public void testUsesCookies() {
        Assert.assertTrue(vendorEight.getUsesCookies());
    }

    @Test
    public void testCookieRefresh() {
        Assert.assertFalse(vendorEight.getHasCookieRefresh());
    }

    @Test
    public void testUsesNonCookieAccess() {
        Assert.assertTrue(vendorEight.getUsesNonCookieAccess());
    }

    @Test
    public void testNullDeviceStorageDisclosureUrl() {
        Assert.assertFalse(vendorEight.getDeviceStorageDisclosureUrl().isPresent());
    }

    @Test
    public void testNullCookieMaxAgeSeconds() {
        Assert.assertFalse(vendorTwo.getUsesCookies());
        Assert.assertFalse(vendorTwo.getCookieMaxAgeSeconds().isPresent());
    }

    @Test
    public void testDeviceStorageDisclosureUrl() {
        String expectedDeviceStorageDisclosureUrl = "https://privacy.blismedia.com/.well-known/deviceStorage.json";
        Assert.assertTrue(vendorTwo.getDeviceStorageDisclosureUrl().isPresent());
        Assert.assertEquals(expectedDeviceStorageDisclosureUrl, vendorTwo.getDeviceStorageDisclosureUrl().get());
    }
}
