package com.iabtcf.encoder;

/*-
 * #%L
 * IAB TCF Java Encoder Library
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

import com.iabtcf.FieldDefs;
import com.iabtcf.encoder.exceptions.ValueOverflowException;
import com.iabtcf.utils.BitSetIntIterable;

class Bounds {
    public static List<PublisherRestrictionEntry> checkBounds(List<PublisherRestrictionEntry> value) {
        int max = (1 << FieldDefs.CORE_NUM_PUB_RESTRICTION.getLength()) - 1;
        if (value.size() > max) {
            throw new ValueOverflowException(value.size(), max, FieldDefs.CORE_NUM_PUB_RESTRICTION);
        }
        return value;
    }

    /**
     * This is used to make sure the maximum value is less than a digit of field#getLength number of
     * bits.
     */
    public static BitSetIntIterable.Builder checkBounds(BitSetIntIterable.Builder values, FieldDefs field) {
        long max = (1L << field.getLength()) - 1;
        if (values.max() > max) {
            throw new ValueOverflowException(values.max(), max, field);
        }

        return values;
    }

    /**
     * This is used make sure the maximum value is is less than field#getLength number of bits.
     */
    public static BitSetIntIterable.Builder checkBoundsBits(BitSetIntIterable.Builder values, FieldDefs field) {
        if (values.max() > field.getLength()) {
            throw new ValueOverflowException(values.max(), field.getLength(), field);
        }

        return values;
    }

    /**
     * This is used to make sure the value is less than a digit of field#getLength number of bits.
     */
    public static int checkBounds(int value, FieldDefs field) {
        final long max = (1L << field.getLength()) - 1;
        if (value > max) {
            throw new ValueOverflowException(value, max, field);
        }

        return value;
    }

    public static long checkBounds(long value, FieldDefs field) {
        final long max = (1L << field.getLength()) - 1;
        if (value > max) {
            throw new ValueOverflowException(value, max, field);
        }

        return value;
    }

}
