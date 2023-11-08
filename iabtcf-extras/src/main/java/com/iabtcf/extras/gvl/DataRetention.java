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

import java.util.Map;
import java.util.Optional;

/*
 * DataRetention allows vendors to declare how long they retain users’ data for each declared purpose. Vendors can
 * define specific retention periods on a per-purpose and special-purpose basis, and/or define a standard retention
 * period if it is the same for many or all of the declared purposes
 * @since 3.0
 */
public interface DataRetention {

    /**
     * stdRetention
     *
     * @return stdRetention
     */
    Optional<Integer> getStdRetention();

    /**
     * purposes
     *
     * @return purposes
     */
    Map<Integer, Integer> getPurposes();

    /**
     * specialPurposes
     *
     * @return specialPurposes
     */
    Map<Integer, Integer> getSpecialPurposes();
}
