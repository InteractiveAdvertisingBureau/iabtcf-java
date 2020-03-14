package com.iabtcf.gvl.jackson;

/*-
 * #%L
 * IAB TCF Core Library
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

/**
 * A standard purpose
 */
public class Purpose implements com.iabtcf.gvl.Purpose {

    private int id;
    private String name;
    private String description;
    private String descriptionLegal;
    private Boolean consentable;
    private Boolean rightToObject;

    /**
     * A purpose id
     *
     * @return purpose id
     */
    public int getId() {
        return id;
    }

    /**
     * Name of the purpose
     *
     * @return purpose name string
     */
    public String getName() {
        return name;
    }

    /**
     * Description of the purpose
     *
     * @return purpose description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legal description of the purpose
     *
     * @return legal description string
     */
    public String getDescriptionLegal() {
        return descriptionLegal;
    }

    /**
     * An optional flag where false means CMPs should never afford users the means to provide
     * an opt-in consent choice
     *
     * @return consentable boolean
     */
    public Boolean getConsentable() {
        return consentable != null ?  consentable : true;
    }

    /**
     * An optional flag where false means CMPs should never afford users the means to exercise a right to object
     *
     * @return consentable boolean
     */
    public Boolean getRightToObject() {
        return rightToObject != null ?  rightToObject : true;
    }
}
