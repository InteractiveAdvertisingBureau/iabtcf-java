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
import java.net.URL;

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
        resolver.addMapping(com.iabtcf.gvl.Gvl.class, Gvl.class);
        resolver.addMapping(com.iabtcf.gvl.Feature.class, Feature.class);
        resolver.addMapping(com.iabtcf.gvl.Overflow.class, Overflow.class);
        resolver.addMapping(com.iabtcf.gvl.Purpose.class, Purpose.class);
        resolver.addMapping(com.iabtcf.gvl.SpecialFeature.class, SpecialFeature.class);
        resolver.addMapping(com.iabtcf.gvl.SpecialPurpose.class, SpecialPurpose.class);
        resolver.addMapping(com.iabtcf.gvl.Stack.class, Stack.class);
        resolver.addMapping(com.iabtcf.gvl.Vendor.class, Vendor.class);

        module.setAbstractTypes(resolver);
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Gets the global vendor list json using the url and converts it into a POJO
     *
     * @param url url string
     * @return {@link com.iabtcf.gvl.Gvl} object
     */
    public com.iabtcf.gvl.Gvl load(String url) throws IOException {
        return objectMapper.readValue(new URL(url), com.iabtcf.gvl.Gvl.class);
    }

    /**
     * Converts global vendor list as a json byte array into a POJO
     *
     * @return Gvl object
     * @see com.iabtcf.gvl.Gvl
     */
    public com.iabtcf.gvl.Gvl load(byte[] json) throws IOException {
        return objectMapper.readValue(json, Gvl.class);
    }
}
