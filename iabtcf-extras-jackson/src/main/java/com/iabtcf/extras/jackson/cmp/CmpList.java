package com.iabtcf.extras.jackson.cmp;

/*-
 * #%L
 * IAB TCF Java CMP List Jackson
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmpList implements com.iabtcf.extras.cmp.CmpList {

    private Instant lastUpdated;
    private Map<Integer, Cmp> cmps;

    /**
     * Last Updated Date
     *
     * @return {@link Instant} time when the record was last updated
     */
    @Override
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    /**
     * List of CMPs
     *
     * @return {@link List} of {@link com.iabtcf.extras.cmp.Cmp} objects
     */
    @Override
    public List<com.iabtcf.extras.cmp.Cmp> getCmps() {
        return new ArrayList<>(cmps.values());
    }

    /**
     * Get the CMP object for a give CMP id
     *
     * @param cmpId CMP id
     * @return {@link com.iabtcf.extras.cmp.Cmp} object
     */
    @Override
    public com.iabtcf.extras.cmp.Cmp getCmp(int cmpId) {
        return cmps.get(cmpId);
    }
}
