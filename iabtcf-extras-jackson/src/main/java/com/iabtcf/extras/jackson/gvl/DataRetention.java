package com.iabtcf.extras.jackson.gvl;

import java.util.Map;
import java.util.Optional;

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
 * DataRetention allows vendors to declare how long they retain users’ data for each declared purpose. Vendors can
 * define specific retention periods on a per-purpose and special-purpose basis, and/or define a standard retention
 * period if it is the same for many or all of the declared purposes
 * @since 3.0
 */
public class DataRetention implements com.iabtcf.extras.gvl.DataRetention {

    private Integer stdRetention;
    private Map<Integer, Integer> purposes;
    private Map<Integer, Integer> specialPurposes;

    /**
     * stdRetention
     *
     * @return stdRetention
     */
    @Override
    public Optional<Integer> getStdRetention() {
        return Optional.ofNullable(stdRetention);
    }

    /**
     * purposes
     *
     * @return purposes
     */
    @Override
    public Map<Integer, Integer> getPurposes() {
        return purposes;
    }

    /**
     * specialPurposes
     *
     * @return specialPurposes
     */
    @Override
    public Map<Integer, Integer> getSpecialPurposes() {
        return specialPurposes;
    }
}
