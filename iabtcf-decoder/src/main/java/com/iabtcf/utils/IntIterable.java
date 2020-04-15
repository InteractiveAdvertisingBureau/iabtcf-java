package com.iabtcf.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

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

import java.util.Arrays;

/**
 * An int primitive memory optimized iterable.
 */
public abstract class IntIterable implements Iterable<Integer> {
    /**
     * Returns a set representation of the IntIterable.
     */
    public Set<Integer> toSet() {
        Set<Integer> ts = new HashSet<>();

        for (IntIterator bit = intIterator(); bit.hasNext();) {
            ts.add(bit.next());
        }

        return ts;
    }

    /**
     * Returns a stream representation of the IntIterable.
     */
    public IntStream toStream() {
        return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(
                intIterator(),
                Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    public boolean isEmpty() {
        return !intIterator().hasNext();
    }

    public boolean containsAll(int... source) {
        return Arrays
                .stream(source)
                .allMatch(this::contains);
    }

    public boolean containsAny(int... source) {
        return Arrays
                .stream(source)
                .anyMatch(this::contains);
    }

    public abstract boolean contains(int value);

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            final IntIterator internal = intIterator();

            @Override
            public boolean hasNext() {
                return internal.hasNext();
            }

            @Override
            public Integer next() {
                return internal.next();
            }
        };
    }

    public abstract IntIterator intIterator();
}
