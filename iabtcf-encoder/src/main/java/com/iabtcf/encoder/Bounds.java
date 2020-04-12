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

import com.iabtcf.encoder.exceptions.ValueOverflowException;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;

class Bounds {
    /**
     * Verifies that number of entries are within bounds.
     */
    public static List<PublisherRestrictionEntry> checkBounds(List<PublisherRestrictionEntry> value) {
        checkBounds(value.size(), (1L << FieldDefs.CORE_NUM_PUB_RESTRICTION.getLength()) - 1,
                FieldDefs.CORE_NUM_PUB_RESTRICTION);

        return value;
    }

    /**
     * This is used to make sure the maximum value in 'values' is less than a digit of
     * field#getLength number of bits.
     *
     * @throws ValueOverflowException
     */
    public static BitSetIntIterable.Builder checkBounds(BitSetIntIterable.Builder values, FieldDefs field) {
        checkBounds(values.max(), (1L << field.getLength()) - 1, field);

        return values;
    }

    /**
     * This is used make sure the maximum value in 'values' is less than field#getLength number of
     * bits.
     *
     * @throws ValueOverflowException
     */
    public static BitSetIntIterable.Builder checkBoundsBits(BitSetIntIterable.Builder values, FieldDefs field) {
        checkBounds(values.max(), field.getLength(), field);

        return values;
    }

    /**
     * This is used to make sure the 'value' is less than a digit of field#getLength number of bits.
     *
     * @throws ValueOverflowException if field#getLength > Integer.SIZE
     */
    public static int checkBounds(int value, FieldDefs field) {
        if (field.getLength() > Integer.SIZE) {
            throw new ValueOverflowException(Integer.SIZE, field.getLength());
        }

        checkBounds(value & ((1L << Integer.SIZE) - 1), field);

        return value;
    }

    /**
     * This is used to make sure the 'value' is less than a digit of field#getLength number of bits.
     *
     * @throws ValueOverflowException if field#getLength > Long.SIZE
     */
    public static long checkBounds(long value, FieldDefs field) {
        final int length = field.getLength();
        if (length > Long.SIZE) {
            throw new ValueOverflowException(Long.SIZE, length);
        } else if (length == Long.SIZE) {
            return value;
        }

        return checkBounds(value, (1L << length) - 1, field);
    }

    private static long checkBounds(long value, long max, FieldDefs field) {
        if (Long.compareUnsigned(value, max) > 0) {
            throw new ValueOverflowException(value, max, field);
        }

        return value;
    }
}
