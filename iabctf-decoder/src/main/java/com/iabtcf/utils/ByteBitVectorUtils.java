package com.iabtcf.utils;

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

import com.iabtcf.ByteBitVector;
import com.iabtcf.FieldDefs;

public class ByteBitVectorUtils {

    public static Instant deciSeconds(ByteBitVector bv, FieldDefs field) {
        return Instant.ofEpochMilli(bv.readBits36(field.getOffset(bv)) * 100);
    }

    public static String readStr2(ByteBitVector bv, int offset) {
        return String
                .valueOf(new char[] {(char) ('A' + bv.readBits6(offset)), (char) ('A' + bv.readBits6(offset + 6))});
    }

    public static String readStr2(ByteBitVector bv, FieldDefs field) {
        return readStr2(bv, field.getOffset(bv));
    }
}
