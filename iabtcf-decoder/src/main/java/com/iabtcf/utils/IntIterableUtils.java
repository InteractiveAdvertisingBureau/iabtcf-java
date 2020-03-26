package com.iabtcf.utils;

import java.util.BitSet;
import java.util.HashSet;

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

import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class IntIterableUtils {

    /**
     * Returns a set representation of the IntIterable.
     */
    public static Set<Integer> toSet(IntIterable it) {
        Set<Integer> ts = new HashSet<>();

        for (IntIterator bit = it.intIterator(); bit.hasNext();) {
            ts.add(bit.next());
        }

        return ts;
    }

    /**
     * Returns a stream representation of the IntIterable.
     */
    public static IntStream toStream(final IntIterable it) {
        return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(
                it.intIterator(),
                Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    public static IntIterable toIntIterable(int... values) {
        BitSet bitSet = new BitSet();
        for (int value : values) {
            bitSet.set(value);
        }
        BitSetIntIterable bitSetIntIterable = new BitSetIntIterable(bitSet);
        return bitSetIntIterable;
    }
}
