package com.iabtcf.v2;

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

public enum SegmentType {
    DEFAULT(0),
    DISCLOSED_VENDOR(1),
    ALLOWED_VENDOR(2),
    PUBLISHER_TC(3);

    private final int value;

    SegmentType(final int value) {
        this.value = value;
    }

    public static SegmentType valueOf(final int value) {
        switch (value) {
            case 1: return DISCLOSED_VENDOR;
            case 2: return ALLOWED_VENDOR;
            case 3: return PUBLISHER_TC;
            default: return DEFAULT;
        }
    }
}
