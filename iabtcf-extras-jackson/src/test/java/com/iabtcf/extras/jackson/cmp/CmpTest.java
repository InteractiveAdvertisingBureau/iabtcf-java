package com.iabtcf.extras.jackson.cmp;

/*-
 * #%L
 * IAB TCF Java CMP List Jackson
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

import com.iabtcf.extras.cmp.Cmp;
import com.iabtcf.extras.jackson.TestUtil;
import com.iabtcf.extras.jackson.Loader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class CmpTest {

    private static Cmp cmpThree;
    private static Cmp cmpTwentyThree;
    private static final int CMP_ID_SELECTED_FOR_TEST = 3;
    private static final int DELETED_CMP_ID_SELECTED_FOR_TEST = 23;

    @BeforeClass
    public static void setupBeforeClass() throws IOException {
        Loader loader = new Loader();
        List<Cmp> cmps = loader.cmpList(TestUtil.getCmpList()).getCmps();
        cmpThree = cmps.stream().filter(o -> o.getId() == CMP_ID_SELECTED_FOR_TEST).findFirst().orElse(null);
        cmpTwentyThree =
            cmps.stream().filter(o -> o.getId() == DELETED_CMP_ID_SELECTED_FOR_TEST).findFirst().orElse(null);
    }

    @Test
    public void getId() {
        Assert.assertEquals(3, cmpThree.getId());
    }

    @Test
    public void getName() {
        String name = "LiveRamp";
        Assert.assertEquals(name, cmpThree.getName());
    }

    @Test
    public void isCommercial() {
        Assert.assertTrue(cmpThree.isCommercial());
    }

    @Test
    public void getDeletedDate() {
        Assert.assertNull(cmpThree.getDeletedDate().orElse(null));
        Assert.assertNotNull(cmpTwentyThree.getDeletedDate().orElse(null));
    }

    @Test
    public void isDeleted() {
        Assert.assertFalse(cmpThree.isDeleted());
        Assert.assertTrue(cmpTwentyThree.isDeleted());
    }
}