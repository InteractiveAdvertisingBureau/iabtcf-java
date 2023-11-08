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

/*
 * Vendors will be able to declare the categories of data they collect and process.
 * @since 3.0
 */
public class DataCategory implements com.iabtcf.extras.gvl.DataCategory {

    private int id;
    private String name;
    private String description;

    /**
     * A id
     *
     * @return id
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * Name
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Description
     *
     * @return description
     */
    @Override
    public String getDescription() {
        return description;
    }
}
