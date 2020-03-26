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

import java.util.Set;
import java.util.TreeSet;

import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterableUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IntIterableMatcher extends BaseMatcher<IntIterable> {

    private Matcher<Set<Integer>> baseM;

    public static IntIterableMatcher matchInts(Set<Integer> values) {
        return new IntIterableMatcher(values);
    }

    public static IntIterableMatcher matchInts(int... values) {
        return new IntIterableMatcher(values);
    }

    private IntIterableMatcher(Set<Integer> values) {
        baseM = org.hamcrest.core.Is.is(org.hamcrest.core.IsEqual.equalTo(values));
    }

    private IntIterableMatcher(int... values) {
        Set<Integer> asSet = new TreeSet<>();
        for (int i : values) {
            asSet.add(i);
        }

        baseM = org.hamcrest.core.Is.is(org.hamcrest.core.IsEqual.equalTo(asSet));
    }

    @Override
    public void describeTo(Description description) {
        baseM.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        super.describeMismatch(IntIterableUtils.toSet((IntIterable) item), description);
    }

    @Override
    public boolean matches(Object item) {
        IntIterable i = (IntIterable) item;
        return baseM.matches(IntIterableUtils.toSet(i));
    }
}
