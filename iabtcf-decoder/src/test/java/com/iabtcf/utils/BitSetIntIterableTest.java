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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class BitSetIntIterableTest {
    @Test
    public void testEmpty() {
        BitSetIntIterable e = BitSetIntIterable.EMPTY;
        assertFalse(e.contains(0));
        assertFalse(e.intIterator().hasNext());
        assertEquals(0, e.toSet().size());
        assertEquals(0, e.toStream().count());
    }

    @Test
    public void testLengthZero() {
        BitSet bs = new BitSet();
        bs.set(0);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertTrue(e.contains(0));
        assertTrue(e.intIterator().hasNext());
        assertEquals(1, e.toSet().size());
        assertEquals(1, e.toStream().count());
    }

    @Test
    public void testLengthMiddle() {
        BitSet bs = new BitSet();
        bs.set(5);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertFalse(e.contains(0));
        assertTrue(e.contains(5));
        assertTrue(e.intIterator().hasNext());
        assertEquals(1, e.toSet().size());
        assertEquals(1, e.toStream().count());
    }

    @Test
    public void testLengthWithZeroMiddle() {
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(4);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertTrue(e.contains(0));
        assertTrue(e.contains(4));
        assertFalse(e.contains(5));
        assertTrue(e.intIterator().hasNext());
        assertEquals(2, e.toSet().size());
        assertEquals(2, e.toStream().count());
        assertEquals(new TreeSet<>(Arrays.asList(0, 4)), e.toSet());
    }

    @Test
    public void testWithExtra() {
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(4);
        bs.set(5);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertTrue(e.contains(0));
        assertTrue(e.contains(4));
        assertTrue(e.contains(5));
        assertFalse(e.contains(6));
        assertFalse(e.contains(10));
        assertTrue(e.intIterator().hasNext());
        assertEquals(3, e.toSet().size());
        assertEquals(3, e.toStream().count());
        assertEquals(new TreeSet<>(Arrays.asList(0, 4, 5)), e.toSet());
    }

    @Test
    public void testContainsAll() {
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(4);
        bs.set(5);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertTrue(e.containsAll());
        assertTrue(e.containsAll(0, 4));
        assertTrue(e.containsAll(4, 5));
        assertTrue(e.containsAll(0, 4, 5));
        assertTrue(e.containsAll(4, 0, 5));
        assertTrue(e.containsAll(4, 0, 5, 5, 0));

        assertFalse(e.containsAll(1));
        assertFalse(e.containsAll(10));
        assertFalse(e.containsAll(10, 1));
        assertFalse(e.containsAll(0, 4, 10));
        assertFalse(e.containsAll(6, 10));
        assertFalse(e.containsAll(5, 0, 1));
    }

    @Test
    public void testToEmptySet() {
        BitSet bs = new BitSet();
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertEquals(new TreeSet<>(Arrays.asList()), e.toSet());
    }

    @Test
    public void testToSet() {
        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(4);
        bs.set(5);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertEquals(new TreeSet<>(Arrays.asList(1, 4, 5)), e.toSet());
    }

    @Test
    public void testToEmptyStream() {
        BitSet bs = new BitSet();
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertStreamEquals(new TreeSet<>(Arrays.asList()).stream(), e.toStream());
    }

    @Test
    public void testToStream() {
        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(4);
        bs.set(5);
        BitSetIntIterable e = BitSetIntIterable.from(bs);
        assertStreamEquals(new TreeSet<>(Arrays.asList(1, 4, 5)).stream(), e.toStream());
        assertFalse(e.isEmpty());
    }

    @Test
    public void testOf() {
        BitSetIntIterable e = BitSetIntIterable.from(1, 2, 3);
        assertStreamEquals(new TreeSet<>(Arrays.asList(1, 2, 3)).stream(), e.toStream());
    }

    @Test
    public void testEmptyFalse() {
        BitSetIntIterable e = BitSetIntIterable.from(1, 2, 3);
        assertFalse(e.isEmpty());
    }

    @Test
    public void testEmptyTrue() {
        BitSetIntIterable e = BitSetIntIterable.from();
        assertTrue(e.isEmpty());
    }

    @Test
    public void testEmptyTrueBs() {
        BitSetIntIterable e = BitSetIntIterable.from(new BitSet());
        assertTrue(e.isEmpty());
    }

    @Test
    public void testOfIterable() {
        Set<Integer> expected = new TreeSet<>(Arrays.asList(1, 2, 3));
        BitSetIntIterable e = BitSetIntIterable.from(1, 2, 3);
        for (Iterator<Integer> i = e.iterator(); i.hasNext();) {
            expected.contains(i.next());
        }
        assertStreamEquals(expected.stream(), e.toStream());
    }


    @Test
    public void testClone() {
        BitSet bs = new BitSet();
        bs.set(10);
        BitSetIntIterable e = BitSetIntIterable.from(bs);

        BitSetIntIterable e1 = e.clone();

        bs.clear();
        assertFalse(e.isEmpty());
        assertFalse(e1.isEmpty());
    }

    @Test
    public void testCopy() {
        BitSet bs = new BitSet();
        bs.set(10);
        IntIterable e = BitSetIntIterable.from(bs);

        BitSetIntIterable e1 = BitSetIntIterable.from(e);

        bs.clear();
        assertFalse(e.isEmpty());
        assertFalse(e1.isEmpty());
    }

    @Test
    public void testMax() {
        BitSetIntIterable.Builder bs = BitSetIntIterable.newBuilder();
        assertEquals(bs.max(), 0);
        bs.add(1);
        assertEquals(bs.max(), 1);
        bs.add(2);
        bs.add(99);
        bs.add(100);
        assertEquals(bs.max(), 100);
    }

    static void assertStreamEquals(Stream<?> s1, IntStream s2) {
        Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            assertEquals(iter1.next(), iter2.next());
        }
        assert !iter1.hasNext() && !iter2.hasNext();
    }
}
