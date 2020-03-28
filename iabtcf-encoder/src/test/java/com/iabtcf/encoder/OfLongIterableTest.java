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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.PrimitiveIterator.OfLong;

import org.junit.Test;

public class OfLongIterableTest {

    @Test
    public void testEmpty() {
        OfLongIterable i = new OfLongIterable();
        assertFalse(i.longIterator().hasNext());
        assertEquals(0, i.size());
    }

    @Test
    public void testAdd9() {
        OfLongIterable i = new OfLongIterable();
        for (int j = 0; j < 9; j++) {
            i.add(j);
        }

        assertTrue(i.longIterator().hasNext());
        assertEquals(9, i.size());

        int j = 0;
        for (OfLong k = i.longIterator(); k.hasNext();) {
            assertEquals(j++, k.nextLong());
        }
    }

    @Test
    public void testResize() {
        OfLongIterable i = new OfLongIterable();
        for (int j = 0; j < 900; j++) {
            i.add(j);
        }

        assertTrue(i.longIterator().hasNext());
        assertEquals(900, i.size());

        int j = 0;
        for (OfLong k = i.longIterator(); k.hasNext();) {
            assertEquals(j++, k.nextLong());
        }
    }

    @Test
    public void testEquals() {
        OfLongIterable h = new OfLongIterable();
        OfLongIterable k = new OfLongIterable();
        OfLongIterable i = new OfLongIterable();
        for (int j = 0; j < 9; j++) {
            i.add(j);
            k.add(j);
            h.add(8 - j);
        }

        assertEquals(k, i);
        assertNotEquals(h, i);
    }
}
