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

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class GvlLoader {
    private ObjectMapper objectMapper = new ObjectMapper();

    public GvlLoader() {
        SimpleModule module = new SimpleModule();

        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(com.iabtcf.extras.Gvl.class, Gvl.class);
        resolver.addMapping(com.iabtcf.extras.Feature.class, Feature.class);
        resolver.addMapping(com.iabtcf.extras.Overflow.class, Overflow.class);
        resolver.addMapping(com.iabtcf.extras.Purpose.class, Purpose.class);
        resolver.addMapping(com.iabtcf.extras.SpecialFeature.class, SpecialFeature.class);
        resolver.addMapping(com.iabtcf.extras.SpecialPurpose.class, SpecialPurpose.class);
        resolver.addMapping(com.iabtcf.extras.Stack.class, Stack.class);
        resolver.addMapping(com.iabtcf.extras.Vendor.class, Vendor.class);

        module.setAbstractTypes(resolver);
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Gets the global vendor list json from the specified InputStream
     *
     * @param url url string
     * @return {@link com.iabtcf.extras.Gvl} object
     */
    public com.iabtcf.extras.Gvl load(InputStream content) throws IOException {
        return objectMapper.readValue(content, com.iabtcf.extras.Gvl.class);
    }

    /**
     * Gets the global vendor list json using a json string
     *
     * @param json the gvl json as a string
     * @return {@link com.iabtcf.extras.Gvl} object
     */
    public com.iabtcf.extras.Gvl load(String json) throws IOException {
        return objectMapper.readValue(json, com.iabtcf.extras.Gvl.class);
    }


    /**
     * Converts global vendor list as a json byte array
     *
     * @return Gvl object
     * @see com.iabtcf.extras.Gvl
     */
    public com.iabtcf.extras.Gvl load(byte[] json) throws IOException {
        return objectMapper.readValue(json, Gvl.class);
    }
}
