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
import java.util.List;

import com.iabtcf.extras.jackson.TestUtil;
import com.iabtcf.extras.jackson.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iabtcf.extras.gvl.Purpose;

public class PurposeTest {

    private static Purpose purposeTen;
    private static Purpose purposeElevenV3;
    private static final int PURPOSE_SELECTED_FOR_TEST = 10;
    private static final int PURPOSE_SELECTED_FOR_V3_TEST = 11;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        Loader loader = new Loader();
        List<Purpose> purposes = loader.globalVendorList(TestUtil.getGlobalVendorList()).getPurposes();
        purposeTen = purposes.stream().filter(o -> o.getId() == PURPOSE_SELECTED_FOR_TEST).findFirst().orElse(null);

        purposes = loader.globalVendorList(TestUtil.getGlobalVendorListV3()).getPurposes();
        purposeElevenV3 =
            purposes.stream().filter(o -> o.getId() == PURPOSE_SELECTED_FOR_V3_TEST).findFirst().orElse(null);
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
        Assert.assertEquals(expectedDescriptionLegal, purposeTen.getDescriptionLegal().get());
    }

    @Test
    public void testGetConsentable() {
        Assert.assertTrue(purposeTen.getConsentable());
    }

    @Test
    public void testGetRightToObject() {
        Assert.assertFalse(purposeTen.getRightToObject());
    }

    @Test
    public void testPurposeV3() {
        Assert.assertEquals(11, purposeElevenV3.getId());
        Assert.assertEquals("Use limited data to select content", purposeElevenV3.getName());
        Assert.assertEquals("Content presented to you on this service can be based on limited data, such as the website or app you are using, your non-precise location, your device type, or which content you are (or have been) interacting with (for example, to limit the number of times a video or an article is presented to you).\n", purposeElevenV3.getDescription());
        Assert.assertFalse(purposeElevenV3.getDescriptionLegal().isPresent());
        Assert.assertEquals(2, purposeElevenV3.getIllustrations().get().size());
        Assert.assertEquals("A sports news mobile app has started a new section of articles covering the most recent football games. Each article includes videos hosted by a separate streaming platform showcasing the highlights of each match. If you fast-forward a video, this information may be used to select a shorter video to play next.\n",
                            purposeElevenV3.getIllustrations().get().get(1));
        Assert.assertTrue(purposeElevenV3.getConsentable());
        Assert.assertTrue(purposeElevenV3.getRightToObject());

    }
}
