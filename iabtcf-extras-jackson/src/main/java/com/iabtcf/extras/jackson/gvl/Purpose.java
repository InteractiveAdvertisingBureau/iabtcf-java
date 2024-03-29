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

import java.util.List;
import java.util.Optional;

/**
 * A standard purpose
 */
public class Purpose implements com.iabtcf.extras.gvl.Purpose {

    private int id;
    private String name;
    private String description;
    private String descriptionLegal;
    private List<String> illustrations;
    private boolean consentable = true;
    private boolean rightToObject = true;

    /**
     * A purpose id
     *
     * @return purpose id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Name of the purpose
     *
     * @return purpose name string
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Description of the purpose
     *
     * @return purpose description string
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Legal description of the purpose
     * @deprecated since 3.0
     * @return legal description string
     */
    @Override
    public Optional<String> getDescriptionLegal() {
        return Optional.ofNullable(descriptionLegal);
    }

    /**
     * illustrations
     * @since 3.0
     * @return illustrations
     */
    @Override
    public Optional<List<String>> getIllustrations() {
        return Optional.ofNullable(illustrations);
    }

    /**
     * An optional flag where false means CMPs should never afford users the means to provide an
     * opt-in consent choice
     *
     * @return consentable boolean
     */
    @Override
    public boolean getConsentable() {
        return consentable;
    }

    /**
     * An optional flag where false means CMPs should never afford users the means to exercise a
     * right to object
     *
     * @return right to object boolean
     */
    @Override
    public boolean getRightToObject() {
        return rightToObject;
    }
}
