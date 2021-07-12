package com.iabtcf.test.utils;

import java.util.Arrays;

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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.iabtcf.utils.IntIterable;

public class IntIterableMatcher extends BaseMatcher<IntIterable> {

    private final Matcher<Iterable<? extends Integer>> baseM;

    public static IntIterableMatcher matchInts(Set<Integer> values) {
        return new IntIterableMatcher(values);
    }

    public static IntIterableMatcher matchInts(int... values) {
        return new IntIterableMatcher(values);
    }

    private IntIterableMatcher(Set<Integer> values) {
        baseM = org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder(values.toArray(new Integer[] {}));
    }

    private IntIterableMatcher(int... values) {
        baseM = org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder(Arrays.stream(values).boxed().toArray(Integer[]::new));
    }

    @Override
    public void describeTo(Description description) {
        baseM.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        super.describeMismatch(((IntIterable) item).toSet(), description);
    }

    @Override
    public boolean matches(Object item) {
        return baseM.matches(((IntIterable) item).toSet());
    }
}
