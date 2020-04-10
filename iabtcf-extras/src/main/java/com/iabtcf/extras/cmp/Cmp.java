package com.iabtcf.extras.cmp;

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

import java.time.Instant;
import java.util.Optional;

public interface Cmp {

    /**
     * A CMP id: a numeric ID which is incrementally assigned and never re-used – inactive CMPs are marked as deleted
     *
     * @return CMP id
     */
    int getId();

    /**
     * Name of the CMP
     *
     * @return CMP name
     */
    String getName();

    /**
     * Whether or not the CMP is a commercial service
     *
     * @return true, if the CMP is available as a commercial service
     */
    boolean isCommercial();

    /**
     * If available, the date/time after which CMP is considered inactive
     *
     * @return {@link Optional<Instant>} time after which CMP is inactive
     */
    Optional<Instant> getDeletedDate();

    /**
     * Check whether the CMP is deleted
     *
     * @return true, if the CMP is considered deleted
     */
    boolean isDeleted();
}
