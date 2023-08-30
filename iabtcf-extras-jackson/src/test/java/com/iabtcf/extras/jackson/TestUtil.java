package com.iabtcf.extras.jackson;

/*-
 * #%L
 * IAB TCF Java GVL and CMP List Jackson
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

import com.iabtcf.extras.jackson.gvl.GvlTest;

import java.io.IOException;
import java.io.InputStream;

public class TestUtil {

    public static InputStream getCmpList() throws IOException {
        ClassLoader loader = GvlTest.class.getClassLoader();
        return loader.getResourceAsStream("cmpList.json");
    }

    public static InputStream getGlobalVendorList() throws IOException {
        ClassLoader loader = GvlTest.class.getClassLoader();
        return loader.getResourceAsStream("gvl.json");
    }

    public static InputStream getGlobalVendorListV3() throws IOException {
        ClassLoader loader = GvlTest.class.getClassLoader();
        return loader.getResourceAsStream("gvlv3.json");
    }
}
