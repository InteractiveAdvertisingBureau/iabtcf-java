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

import com.iabtcf.extras.gvl.SpecialPurpose;
import com.iabtcf.extras.jackson.TestUtil;
import com.iabtcf.extras.jackson.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class SpecialPurposeTest {

    private static SpecialPurpose specialPurposeOne;
    private static final int SPECIAL_PURPOSE_SELECTED_FOR_TEST = 1;

    @BeforeClass
    public static void setupBeforeClass() throws IOException {
        Loader loader = new Loader();
        List<SpecialPurpose> specialPurposes = loader.globalVendorList(TestUtil.getGlobalVendorList()).getSpecialPurposes();
        specialPurposeOne =
                specialPurposes.stream()
                    .filter(o -> o.getId() == SPECIAL_PURPOSE_SELECTED_FOR_TEST)
                    .findFirst()
                    .orElse(null);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(1, specialPurposeOne.getId());
    }

    @Test
    public void testGetName() {
        String expectedName = "Ensure security, prevent fraud, and debug";
        Assert.assertEquals(expectedName, specialPurposeOne.getName());
    }

    @Test
    public void testGetDescription() {
        String expectedDescription =
                "Your data can be used to monitor for and prevent fraudulent activity, and ensure systems and processes work properly and securely.";
        Assert.assertEquals(expectedDescription, specialPurposeOne.getDescription());
    }

    @Test
    public void testGetDescriptionLegal() {
        String expectedDescriptionLegal =
                "To ensure security, prevent fraud and debug vendors can:\n* Ensure data are securely transmitted\n* Detect and prevent malicious, fraudulent, invalid, or illegal activity.\n* Ensure correct and efficient operation of systems and processes, including to monitor and enhance the performance of systems and processes engaged in permitted purposes\nVendors cannot:\n* Conduct any other data processing operation allowed under a different purpose under this purpose.";
        Assert.assertEquals(expectedDescriptionLegal, specialPurposeOne.getDescriptionLegal());
    }

    @Test
    public void testGetConsentable() {
        Assert.assertTrue(specialPurposeOne.getConsentable());
    }

    @Test
    public void testGetRightToObject() {
        Assert.assertFalse(specialPurposeOne.getRightToObject());
    }
}
