package com.iabtcf.gvl.jackson;

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
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iabtcf.gvl.Purpose;

public class PurposeTest {

    private static Purpose purposeTen;
    private static final int PURPOSE_SELECTED_FOR_TEST = 10;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        GvlLoader gvlLoader = new GvlLoader();
        List<Purpose> purposes = gvlLoader.load(GvlUtil.getGlobalVendorList()).getPurposes();
        purposeTen = purposes.stream().filter(o -> o.getId() == PURPOSE_SELECTED_FOR_TEST).findFirst().orElse(null);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(10, purposeTen.getId());
    }

    @Test
    public void testGetName() {
        String expectedName = "Develop and improve products";
        Assert.assertEquals(expectedName, purposeTen.getName());
    }

    @Test
    public void testGetDescription() {
        String expectedDescription =
                "Your data can be used to improve existing systems and software, and to develop new products";
        Assert.assertEquals(expectedDescription, purposeTen.getDescription());
    }

    @Test
    public void testGetDescriptionLegal() {
        String expectedDescriptionLegal =
                "To develop new products and improve products vendors can:\n* Use information to improve their existing products with new features and to develop new products\n* Create new models and algorithms through machine learning\nVendors cannot:\n* Conduct any other data processing operation allowed under a different purpose under this purpose";
        Assert.assertEquals(expectedDescriptionLegal, purposeTen.getDescriptionLegal());
    }

    @Test
    public void testGetConsentable() {
        Assert.assertTrue(purposeTen.getConsentable());
    }

    @Test
    public void testGetRightToObject() {
        Assert.assertFalse(purposeTen.getRightToObject());
    }
}
