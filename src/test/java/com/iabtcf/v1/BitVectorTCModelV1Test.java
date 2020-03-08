package com.iabtcf.v1;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iabtcf.decoder.TCModelDecoder;
import com.iabtcf.model.TCModel;
import org.junit.Test;

import static com.iabtcf.v1.Purpose.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class BitVectorTCModelV1Test {

    @Test
    public void testDecodeCanDetectVersion1() {
        String base64 = "BObdrPUOevsguAfDqFENCNAAAAAmeAAA";
        TCModel model = TCModelDecoder.instance().decode(base64);
        assertTrue(model instanceof TCModelV1);
    }

    @Test
    public void testCanConstructV1Properties() {
        String base64 = "BObdrPUOevsguAfDqFENCNAAAAAmeAAA";
        TCModelV1 model = (TCModelV1) TCModelDecoder.instance().decode(base64);
        assertEquals(1, model.version());
        assertEquals(31, model.cmpId());
        assertEquals(234, model.cmpVersion());
        assertEquals(5, model.consentScreen());
        assertEquals("EN", model.consentLanguage());
        assertTrue(model.allowedPurposeIds().isEmpty());
        assertEquals(Instant.parse("2019-02-04T21:16:05.200Z"), model.consentRecordCreated());
        assertEquals(Instant.parse("2019-04-09T14:35:10.200Z"), model.consentRecordLastUpdated());
        assertEquals(141, model.vendorListVersion());
        assertFalse(model.isPurposeAllowed(1));
        assertFalse(model.isVendorAllowed(1));
        assertTrue(model.allowedPurposes().isEmpty());
    }

    @Test
    public void testAllowedPurposes() {
        String base64 = "BOOzQoAOOzQoAAPAFSENCW-AIBA=";

        TCModelV1 model = (TCModelV1) TCModelDecoder.instance().decode(base64);
        assertThat(model.allowedPurposeIds(), is(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 15, 24))));
        assertThat(
                model.allowedPurposes(),
                is(
                        new HashSet<>(
                                Arrays.asList(
                                        STORAGE_AND_ACCESS,
                                        PERSONALIZATION,
                                        AD_SELECTION,
                                        CONTENT_DELIVERY,
                                        MEASUREMENT,
                                        UNDEFINED))));

        assertTrue(model.isPurposeAllowed(1));
        assertTrue(model.isPurposeAllowed(STORAGE_AND_ACCESS));
        assertTrue(model.isPurposeAllowed(2));
        assertTrue(model.isPurposeAllowed(PERSONALIZATION));
        assertTrue(model.isPurposeAllowed(3));
        assertTrue(model.isPurposeAllowed(AD_SELECTION));
        assertTrue(model.isPurposeAllowed(4));
        assertTrue(model.isPurposeAllowed(CONTENT_DELIVERY));
        assertTrue(model.isPurposeAllowed(5));
        assertTrue(model.isPurposeAllowed(MEASUREMENT));
        assertTrue(model.isPurposeAllowed(15));
        assertTrue(model.isPurposeAllowed(24));
    }

    @Test
    public void testBitFieldEncoding() {
        String base64 = "BOOzQoAOOzQoAAPAFSENCW-AIBACBAAABCA=";
        TCModelV1 model = (TCModelV1) TCModelDecoder.instance().decode(base64);
        assertTrue(model.isVendorAllowed(1));
        assertTrue(model.isVendorAllowed(25));
        assertTrue(model.isVendorAllowed(30));
        assertThat(model.allowedVendorIds(), is(new HashSet<>(Arrays.asList(1, 25, 30))));

        assertFalse(model.isVendorAllowed(2));
        assertFalse(model.isVendorAllowed(3));
        assertFalse(model.isVendorAllowed(31));
        assertFalse(model.isVendorAllowed(32));

        assertFalse(model.isVendorAllowed(-99));
        assertFalse(model.isVendorAllowed(-1));
        assertFalse(model.isVendorAllowed(0));
        assertFalse(model.isVendorAllowed(33));
        assertFalse(model.isVendorAllowed(34));
        assertFalse(model.isVendorAllowed(99));
    }

    @Test
    public void testRangeEncodingDefaultFalse() {
        String base64 = "BOOzQoAOOzQoAAPAFSENCW-AIBACCACgACADIAHg";
        TCModelV1 model = (TCModelV1) TCModelDecoder.instance().decode(base64);

        // Then: correct vendor IDs are allowed
        assertTrue(model.isVendorAllowed(1));
        assertTrue(model.isVendorAllowed(10));
        assertTrue(model.isVendorAllowed(25));
        assertTrue(model.isVendorAllowed(30));

        Set<Integer> expectedVendorIds =
                IntStream.concat(IntStream.rangeClosed(1, 25), IntStream.of(30))
                    .boxed()
                    .collect(Collectors.toSet());
        assertThat(model.allowedVendorIds(), is(expectedVendorIds));

        assertFalse(model.isVendorAllowed(26));
        assertFalse(model.isVendorAllowed(28));
        assertFalse(model.isVendorAllowed(31));
        assertFalse(model.isVendorAllowed(32));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(model.isVendorAllowed(-99));
        assertFalse(model.isVendorAllowed(-1));
        assertFalse(model.isVendorAllowed(0));
        assertFalse(model.isVendorAllowed(33));
        assertFalse(model.isVendorAllowed(34));
        assertFalse(model.isVendorAllowed(99));
    }

    @Test
    public void testRangeEncodingDefaultTrue() {
        String base64 = "BOOzQoAOOzQoAAPAFSENCW-AIBACDACAADABkAHg";
        TCModelV1 model = (TCModelV1) TCModelDecoder.instance().decode(base64);

        // Then: correct vendor IDs are allowed
        assertFalse(model.isVendorAllowed(1));
        assertFalse(model.isVendorAllowed(25));
        assertFalse(model.isVendorAllowed(27));
        assertFalse(model.isVendorAllowed(30));

        assertTrue(model.isVendorAllowed(2));
        assertTrue(model.isVendorAllowed(15));
        assertTrue(model.isVendorAllowed(31));
        assertTrue(model.isVendorAllowed(32));

        Set<Integer> expectedVendorIds =
                IntStream.concat(IntStream.rangeClosed(2, 24), IntStream.rangeClosed(31, 32))
                    .boxed()
                    .collect(Collectors.toSet());
        assertThat(model.allowedVendorIds(), is(expectedVendorIds));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(model.isVendorAllowed(-99));
        assertFalse(model.isVendorAllowed(-1));
        assertFalse(model.isVendorAllowed(0));
        assertFalse(model.isVendorAllowed(33));
        assertFalse(model.isVendorAllowed(34));
        assertFalse(model.isVendorAllowed(99));
    }

    @Test
    public void testVendorIdRange() {
        String base64 = "BOwBMFeOwBMFeABABBAAABAAAAAAGADgAUACgAHgAPg";

        TCModelV1 model = (TCModelV1) TCModelDecoder.instance().decode(base64);
        assertTrue(model.allowedVendorIds().contains(15));
    }
}
