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

import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static com.iabtcf.v1.Purpose.AD_SELECTION;
import static com.iabtcf.v1.Purpose.CONTENT_DELIVERY;
import static com.iabtcf.v1.Purpose.MEASUREMENT;
import static com.iabtcf.v1.Purpose.PERSONALIZATION;
import static com.iabtcf.v1.Purpose.STORAGE_AND_ACCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class TCStringV1Test {

    private static TCString parse(String consentString) {
        TCString model = TCString.decode(consentString);
        assertTrue(model instanceof TCStringV1);
        assertEquals(1, model.getVersion());

        return model;
    }

    @Test
    public void testDecodeCanDetectVersion1() {
        TCString model = TCString.decode("BObdrPUOevsguAfDqFENCNAAAAAmeAAA");
        assertTrue(model instanceof TCStringV1);
        assertEquals(1, model.getVersion());
    }

    @Test
    public void testCanConstructV1Properties() {
        TCString model = parse("BObdrPUOevsguAfDqFENCNAAAAAmeAAA");
        assertEquals(1, model.getVersion());
        assertEquals(31, model.getCmpId());
        assertEquals(234, model.getCmpVersion());
        assertEquals(5, model.getConsentScreen());
        assertEquals("EN", model.getConsentLanguage());
        assertEquals(Instant.parse("2019-02-04T21:16:05.200Z"), model.getCreated());
        assertEquals(Instant.parse("2019-04-09T14:35:10.200Z"), model.getLastUpdated());
        assertEquals(141, model.getVendorListVersion());
        assertTrue(model.getPurposesConsent().isEmpty());
        assertFalse(model.getPurposesConsent().contains(1));
        assertFalse(model.getVendorConsent().contains(1));
    }

    @Test
    public void testAllowedPurposes() {
        TCString model = parse("BOOzQoAOOzQoAAPAFSENCW-AIBA=");
        assertThat(model.getPurposesConsent(), matchInts(1, 2, 3, 4, 5, 15, 24));
        assertTrue(model.getPurposesConsent().contains(1));
        assertTrue(model.getPurposesConsent().contains(STORAGE_AND_ACCESS.getId()));
        assertTrue(model.getPurposesConsent().contains(2));
        assertTrue(model.getPurposesConsent().contains(PERSONALIZATION.getId()));
        assertTrue(model.getPurposesConsent().contains(3));
        assertTrue(model.getPurposesConsent().contains(AD_SELECTION.getId()));
        assertTrue(model.getPurposesConsent().contains(4));
        assertTrue(model.getPurposesConsent().contains(CONTENT_DELIVERY.getId()));
        assertTrue(model.getPurposesConsent().contains(5));
        assertTrue(model.getPurposesConsent().contains(MEASUREMENT.getId()));
        assertTrue(model.getPurposesConsent().contains(15));
        assertTrue(model.getPurposesConsent().contains(24));
    }

    @Test
    public void testBitFieldEncoding() {
        TCString model = parse("BOOzQoAOOzQoAAPAFSENCW-AIBACBAAABCA=");
        assertTrue(model.getVendorConsent().contains(1));
        assertTrue(model.getVendorConsent().contains(25));
        assertTrue(model.getVendorConsent().contains(30));
        assertThat(model.getVendorConsent(), matchInts(1, 25, 30));

        assertFalse(model.getVendorConsent().contains(2));
        assertFalse(model.getVendorConsent().contains(3));
        assertFalse(model.getVendorConsent().contains(31));
        assertFalse(model.getVendorConsent().contains(32));

        assertFalse(model.getVendorConsent().contains(-99));
        assertFalse(model.getVendorConsent().contains(-1));
        assertFalse(model.getVendorConsent().contains(0));
        assertFalse(model.getVendorConsent().contains(33));
        assertFalse(model.getVendorConsent().contains(34));
        assertFalse(model.getVendorConsent().contains(99));
    }

    @Test
    public void testRangeEncodingDefaultFalse() {
        TCString model = parse("BOOzQoAOOzQoAAPAFSENCW-AIBACCACgACADIAHg");

        // Then: correct vendor IDs are allowed
        assertTrue(model.getVendorConsent().contains(1));
        assertTrue(model.getVendorConsent().contains(10));
        assertTrue(model.getVendorConsent().contains(25));
        assertTrue(model.getVendorConsent().contains(30));

        Set<Integer> expectedVendorIds =
                IntStream.concat(IntStream.rangeClosed(1, 25), IntStream.of(30))
                    .boxed()
                    .collect(Collectors.toSet());
        assertThat(model.getVendorConsent(), matchInts(expectedVendorIds));

        assertFalse(model.getVendorConsent().contains(26));
        assertFalse(model.getVendorConsent().contains(28));
        assertFalse(model.getVendorConsent().contains(31));
        assertFalse(model.getVendorConsent().contains(32));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(model.getVendorConsent().contains(-99));
        assertFalse(model.getVendorConsent().contains(-1));
        assertFalse(model.getVendorConsent().contains(0));
        assertFalse(model.getVendorConsent().contains(33));
        assertFalse(model.getVendorConsent().contains(34));
        assertFalse(model.getVendorConsent().contains(99));
    }

    @Test
    public void testRangeEncodingDefaultTrue() {
        TCString model = parse("BOOzQoAOOzQoAAPAFSENCW-AIBACDACAADABkAHg");

        // Then: correct vendor IDs are allowed
        assertFalse(model.getVendorConsent().contains(1));
        assertFalse(model.getVendorConsent().contains(25));
        assertFalse(model.getVendorConsent().contains(27));
        assertFalse(model.getVendorConsent().contains(30));

        assertTrue(model.getVendorConsent().contains(2));
        assertTrue(model.getVendorConsent().contains(15));
        assertTrue(model.getVendorConsent().contains(31));
        assertTrue(model.getVendorConsent().contains(32));

        Set<Integer> expectedVendorIds =
                IntStream.concat(IntStream.rangeClosed(2, 24), IntStream.rangeClosed(31, 32))
                    .boxed()
                    .collect(Collectors.toSet());
        assertThat(model.getVendorConsent(), matchInts(expectedVendorIds));

        // Vendors outside range [1, MaxVendorId] are not allowed
        assertFalse(model.getVendorConsent().contains(-99));
        assertFalse(model.getVendorConsent().contains(-1));
        assertFalse(model.getVendorConsent().contains(0));
        assertFalse(model.getVendorConsent().contains(33));
        assertFalse(model.getVendorConsent().contains(34));
        assertFalse(model.getVendorConsent().contains(99));
    }

    @Test
    public void testVendorIdRange() {
        TCString model = parse("BOwBMFeOwBMFeABABBAAABAAAAAAGADgAUACgAHgAPg");
        assertTrue(model.getVendorConsent().contains(15));
    }
}
