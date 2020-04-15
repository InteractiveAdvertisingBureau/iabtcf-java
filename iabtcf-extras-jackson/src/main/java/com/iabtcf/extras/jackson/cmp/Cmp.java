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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Optional;

public class Cmp implements com.iabtcf.extras.cmp.Cmp {

    private int id;
    private String name;

    @JsonProperty("isCommercial")
    private boolean isCommercial;

    private Instant deletedDate;


    /**
     * A CMP id: a numeric ID which is incrementally assigned and never re-used – inactive CMPs are marked as deleted
     *
     * @return CMP id
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Name of the CMP
     *
     * @return CMP name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Whether or not the CMP is a commercial service
     *
     * @return true, if the CMP is available as a commercial service
     */
    @Override
    public boolean isCommercial() {
        return this.isCommercial;
    }

    /**
     * If available, the date/time after which CMP is considered inactive
     *
     * @return {@link Optional<Instant>} time after which CMP is inactive
     */
    @Override
    public Optional<Instant> getDeletedDate() {
        return Optional.ofNullable(this.deletedDate);
    }

    /**
     * Check whether the CMP is deleted
     *
     * @return true, if the CMP is considered deleted
     */
    @Override
    public boolean isDeleted() {
        return Optional.ofNullable(this.deletedDate)
            .map(deleteDate -> !deleteDate.isAfter(Instant.now()))
            .orElse(false);
    }
}
