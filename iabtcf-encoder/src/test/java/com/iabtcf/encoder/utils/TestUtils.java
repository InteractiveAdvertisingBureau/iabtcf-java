package com.iabtcf.encoder.utils;

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

import java.time.Instant;

public class TestUtils {

    private static final long DAY_AS_DECISECONDS = 24 * 60 * 60 * 10;

    public static Instant toDeci(Instant instant) {
       return Instant.ofEpochMilli((instant.toEpochMilli() / 100) * 100);
    }

    public static Instant toDeciDays(Instant instant) {
        long deciseconds = instant.toEpochMilli() / 100;
        long precisionToRemove = deciseconds % DAY_AS_DECISECONDS;
        return Instant.ofEpochMilli((deciseconds - precisionToRemove) * 100);
    }
}
