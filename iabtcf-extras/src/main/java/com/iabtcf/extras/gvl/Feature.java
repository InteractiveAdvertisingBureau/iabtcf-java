package com.iabtcf.extras.gvl;

/*-
 * #%L
 * IAB TCF Java GVL and CMP List
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
public interface Feature {

    /**
     * A feature id
     *
     * @return feature id
     */
    int getId();

    /**
     * Name of the feature
     *
     * @return feature name string
     */
    String getName();

    /**
     * Description of the feature
     *
     * @return feature description string
     */
    String getDescription();

    /**
     * Legal description of the feature
     *
     * @return legal description string
     */
    String getDescriptionLegal();
}
