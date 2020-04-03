package com.iabtcf.gvl;

/*-
 * #%L
 * IAB TCF Java GVL
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

/*
 * Stacks may be used to substitute Initial Layer information about two or more Purposes and/or
 * Special Features
 */
public interface Stack {

    /**
     * Stack id
     *
     * @return stack id
     */
    int getId();

    /**
     * A list of special features
     *
     * @return A {@link List} of special feature ids
     */
    List<Integer> getSpecialFeatures();

    /**
     * A list of purposes
     *
     * @return A {@link List} of purpose ids
     */
    List<Integer> getPurposes();

    /**
     * Name of the stack
     *
     * @return stack name string
     */
    String getName();

    /**
     * Description of the stack
     *
     * @return stack description string
     */
    String getDescription();
}
