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
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GvlData {
    private int gvlSpecificationVersion;
    private int vendorListVersion;
    private int tcfPolicyVersion;
    private Date lastUpdated;
    @JsonIgnore
    private Map<Integer, GvlPurposeData> purposes;
    @JsonIgnore
    private Map<Integer, GvlPurposeData> specialPurposes;
    @JsonIgnore
    private Map<Integer, GvlPurposeData> features;
    @JsonIgnore
    private Map<Integer, GvlPurposeData> specialFeatures;
    private Map<Integer, GvlVendorData> vendors;
}

