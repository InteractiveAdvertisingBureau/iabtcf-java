package com.iabtcf.gvl.v2.dao;

/*-
 * #%L
 * IAB TCF Core Library
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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
public class GvlVendorData {

    private int id;
    @JsonIgnore
    private String name;
    private List<Integer> purposes;
    private List<Integer> legIntPurposes;
    private List<Integer> flexiblePurposes;
    @JsonIgnore
    private List<Integer> specialPurposes;
    @JsonIgnore
    private List<Integer> features;
    @JsonIgnore
    private List<Integer> specialFeatures;
    @JsonIgnore
    private String policyUrl;
    private String deletedDate;

    public GvlVendorData(int id, List<Integer> purposes, List<Integer> legIntPurposes, List<Integer> flexiblePurposes) {
        this.id = id;
        this.purposes = purposes;
        this.legIntPurposes = legIntPurposes;
        this.flexiblePurposes = flexiblePurposes;
    }

    public boolean isDeleted() {
        return Optional.ofNullable(this.deletedDate)
            .map(ZonedDateTime::parse)
            .map(deleteAfterDate -> deleteAfterDate.isBefore(ZonedDateTime.now()))
            .orElse(false);
    }

    public boolean isActive() {
        return !isDeleted();
    }
}
