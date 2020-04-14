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

import com.iabtcf.extras.gvl.Stack;
import com.iabtcf.extras.jackson.TestUtil;
import com.iabtcf.extras.jackson.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class StackTest {

    private static Stack stackTwenty;
    private final static int STACK_ID_SELECTED_FOR_TEST = 20;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Loader loader = new Loader();
        List<Stack> stacks = loader.globalVendorList(TestUtil.getGlobalVendorList()).getStacks();
        stackTwenty = stacks.stream().filter(o -> o.getId() == STACK_ID_SELECTED_FOR_TEST).findFirst().orElse(null);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(20, stackTwenty.getId());
    }

    @Test
    public void testGetSpecialFeatures() {
        Assert.assertNotNull(stackTwenty.getSpecialFeatures());
        Assert.assertEquals(0, stackTwenty.getSpecialFeatures().size());
    }

    @Test
    public void testGetPurposes() {
        Assert.assertNotNull(stackTwenty.getPurposes());
        Assert.assertEquals(4, stackTwenty.getPurposes().size());
        Assert.assertEquals(Arrays.asList(7, 8, 9, 10), stackTwenty.getPurposes());
    }

    @Test
    public void testGetName() {
        String expectedName = "Ad and content measurement, audience insights, and product development";
        Assert.assertEquals(expectedName, stackTwenty.getName());
    }

    @Test
    public void testGetDescription() {
        String expectedDescription =
                "Ad and content performance can be measured.  Insights about the audiences who saw the ads and content can be derived. Data can be used to build or improve user experience, systems, and software. Insights about the audiences who saw the ads and content can be derived.";
        Assert.assertEquals(expectedDescription, stackTwenty.getDescription());
    }
}
