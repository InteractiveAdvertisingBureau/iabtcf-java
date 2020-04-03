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


/*
 * List of Features the Vendor may utilize when performing some declared Purposes processing
 */
public class Feature implements com.iabtcf.gvl.Feature {

    private int id;
    private String name;
    private String description;
    private String descriptionLegal;

    /**
     * A feature id
     *
     * @return feature id
     */
    public int getId() {
        return id;
    }

    /**
     * Name of the feature
     *
     * @return feature name string
     */
    public String getName() {
        return name;
    }

    /**
     * Description of the feature
     *
     * @return feature description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legal description of the feature
     *
     * @return legal description string
     */
    public String getDescriptionLegal() {
        return descriptionLegal;
    }
}
