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
import java.util.List;

public interface CmpList {

    /**
     * Last Updated Date
     *
     * @return {@link Instant} time when the record was last updated
     */
    Instant getLastUpdated();

    /**
     * List of CMPs
     *
     * @return {@link List} of {@link Cmp} objects
     */
    List<Cmp> getCmps();

    /**
     * Get the CMP object for a give CMP id
     *
     * @param cmpId CMP id
     * @return {@link Cmp} object
     */
    Cmp getCmp(int cmpId);

}
