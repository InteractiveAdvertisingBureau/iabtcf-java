package com.iabtcf.decoder;

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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.iabtcf.decoder.LazySegmentFactory;

public class SegmentFactoryTest {
    @Test
    public void testFcatory() throws IOException {
        LazySegmentFactory f = new LazySegmentFactory("hello.world");
        Supplier<InputStream> f1 = f.next();
        Supplier<InputStream> f2 = f.next();
        Supplier<InputStream> f3 = f.next();

        String text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        text = IOUtils.toString(f2.get(), StandardCharsets.US_ASCII);
        assertEquals("world", text);

        text = IOUtils.toString(f3.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);
    }

    @Test
    public void testFcatory_1() throws IOException {
        LazySegmentFactory f = new LazySegmentFactory("hello.world");
        Supplier<InputStream> f1 = f.next();
        Supplier<InputStream> f2 = f.next();
        Supplier<InputStream> f3 = f.next();

        String text = IOUtils.toString(f3.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);

        text = IOUtils.toString(f3.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);

        text = IOUtils.toString(f2.get(), StandardCharsets.US_ASCII);
        assertEquals("world", text);

        text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("", text);
    }

    @Test
    public void testReset() throws IOException {
        LazySegmentFactory f = new LazySegmentFactory("hello.world");
        Supplier<InputStream> f1 = f.next();
        String text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);

        InputStream is = f1.get();
        assertEquals(0, is.available());
        is.reset();

        assertEquals(1, is.available());
        text = IOUtils.toString(f1.get(), StandardCharsets.US_ASCII);
        assertEquals("hello", text);
    }
}
