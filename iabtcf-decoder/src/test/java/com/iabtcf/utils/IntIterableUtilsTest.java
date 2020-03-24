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

import java.util.Arrays;
import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;

public class IntIterableUtilsTest {
    @Test
    public void testToSet() {
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(1);
        bs.set(512);

        IntIterable ii = new BitSetIntIterable(bs);
        assertEquals(new TreeSet<>(Arrays.asList(0, 1, 512)), ii.toSet());
    }

    @Test
    public void testToStream() {
        BitSet bs = new BitSet();
        bs.set(0);
        bs.set(1);
        bs.set(512);

        IntIterable ii = new BitSetIntIterable(bs);
        Set<Integer> l = ii.toStream().boxed().collect(Collectors.toSet());

        assertEquals(new TreeSet<>(Arrays.asList(0, 1, 512)), l);
    }
}
