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

import java.util.List;
import java.util.Optional;

/**
 * A standard purpose
 */
public interface Purpose {

    /**
     * A purpose id
     *
     * @return purpose id
     */
    int getId();

    /**
     * Name of the purpose
     *
     * @return purpose name string
     */
    String getName();

    /**
     * Description of the purpose
     *
     * @return purpose description string
     */
    String getDescription();

    /**
     * Legal description of the purpose
     * @deprecated since 3.0
     *
     * @return legal description string
     */
    Optional<String> getDescriptionLegal();

    /**
     * A list of illustrations
     * @since 3.0
     * @return A {@link List} of strings
     */
    Optional<List<String>> getIllustrations();

    /**
     * An optional flag where false means CMPs should never afford users the means to provide an
     * opt-in consent choice
     *
     * @return consentable boolean
     */
    boolean getConsentable();

    /**
     * An optional flag where false means CMPs should never afford users the means to exercise a
     * right to object
     *
     * @return consentable boolean
     */
    boolean getRightToObject();
}
