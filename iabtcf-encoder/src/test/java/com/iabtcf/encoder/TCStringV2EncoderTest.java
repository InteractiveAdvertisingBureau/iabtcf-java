package com.iabtcf.encoder;

import static com.iabtcf.encoder.utils.TestUtils.toDeci;
import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import java.time.temporal.ChronoUnit;
import java.util.BitSet;
import java.util.List;

import org.junit.Test;

import com.iabtcf.decoder.TCString;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.v2.PublisherRestriction;
import com.iabtcf.v2.RestrictionType;

public class TCStringV2EncoderTest {

    private final Instant created = Instant.now();
    private final Instant updated = created.plus(1, ChronoUnit.HOURS);

    private final TCStringEncoder.Builder encoderBuilder = TCStringEncoder.newBuilder()
        .version(2)
        .created(created)
        .lastUpdated(updated)
        .cmpId(1)
        .cmpVersion(12)
        .consentScreen(1)
        .consentLanguage("FR")
        .vendorListVersion(2)
        .tcfPolicyVersion(1)
        .isServiceSpecific(true)
        .useNonStandardStacks(false)
        .specialFeatureOptIns(BitSetIntIterable.of(1, 2))
        .purposesConsent(BitSetIntIterable.of(4, 8))
        .purposesLITransparency(BitSetIntIterable.of(11, 20))
        .purposeOneTreatment(true)
        .publisherCC("DE")
        .vendorsConsent(BitSetIntIterable.of(1, 4))
        .vendorLegitimateInterest(BitSetIntIterable.of(2, 6));

    @Test
    public void testEncodeDefault() {
        String tcf = TCStringEncoder.newBuilder().version(2).encode();
        assertEquals(2, TCString.decode(tcf).getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionMustBeSet() {
        TCStringEncoder.newBuilder().encode();
    }

    @Test
    public void testItConstructsCoreString() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder).encode();

        TCString decoded = TCString.decode(tcf);

        assertEquals(1, tcf.split("\\.").length);
        assertEquals(2, decoded.getVersion());
        assertEquals(toDeci(created), decoded.getCreated());
        assertEquals(toDeci(updated), decoded.getLastUpdated());
        assertEquals(1, decoded.getCmpId());
        assertEquals(12, decoded.getCmpVersion());
        assertEquals(1, decoded.getConsentScreen());
        assertEquals("FR", decoded.getConsentLanguage());
        assertEquals(2, decoded.getVendorListVersion());
        assertEquals(1, decoded.getTcfPolicyVersion());
        assertTrue(decoded.isServiceSpecific());
        assertFalse(decoded.getUseNonStandardStacks());
        assertThat(decoded.getSpecialFeatureOptIns(), matchInts(1, 2));
        assertThat(decoded.getPurposesConsent(), matchInts(4, 8));
        assertThat(decoded.getPurposesLITransparency(), matchInts(11, 20));
        assertTrue(decoded.getPurposeOneTreatment());
        assertEquals("DE", decoded.getPublisherCC());
        assertThat(decoded.getVendorConsent(), matchInts(1, 4));
        assertThat(decoded.getVendorLegitimateInterest(), matchInts(2, 6));
    }

    @Test
    public void testItDecodesAllOptionalSegments() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
            .disclosedVendors(BitSetIntIterable.of(1, 2))
            .allowedVendors(BitSetIntIterable.of(6, 11))
            .pubPurposesConsent(BitSetIntIterable.of(7, 9))
            .pubPurposesLITransparency(BitSetIntIterable.of(2, 3))
            .numberOfCustomPurposesConsent(4)
            .customPurposesConsent(BitSetIntIterable.of(1, 2, 4))
            .customPurposesLITransparency(BitSetIntIterable.of(1, 3, 4))
            .encode();

        TCString decoded = TCString.decode(tcf);

        assertEquals(4, tcf.split("\\.").length);
        assertThat(decoded.getDisclosedVendors(), matchInts(1, 2));
        assertThat(decoded.getAllowedVendors(), matchInts(6, 11));
        assertThat(decoded.getPubPurposesLITransparency(), matchInts(2, 3));
        assertThat(decoded.getCustomPurposesConsent(), matchInts(1, 2, 4));
        assertThat(decoded.getCustomPurposesLITransparency(), matchInts(1, 3, 4));
    }

    @Test
    public void testEncodedVendorDisclosedSection() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
            .disclosedVendors(BitSetIntIterable.of(1, 2))
            .encode();

        TCString decoded = TCString.decode(tcf);
        assertEquals(2, tcf.split("\\.").length);
        assertThat(decoded.getDisclosedVendors(), matchInts(1, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldFailIfLongStringIsProvided() {
        TCStringEncoder.newBuilder(encoderBuilder)
            .consentLanguage("GBR")
            .encode();

    }

    @Test
    public void testLanguageToCaps() {
        String tcStr = TCStringEncoder.newBuilder(encoderBuilder)
            .version(1)
            .consentLanguage("gb")
            .encode();

        assertEquals("GB", TCString.decode(tcStr).getConsentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVersion0() {
        TCStringEncoder.newBuilder(encoderBuilder).version(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVersion3() {
        TCStringEncoder.newBuilder(encoderBuilder).version(3);
    }

    @Test
    public void testPubisherRestrictions() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);

        TCString decoded = TCString.decode(b.encode());
        assertEquals(0, decoded.getPublisherRestrictions().size());

        PublisherRestrictionEntry.Builder pre = PublisherRestrictionEntry.newBuilder()
            .purposeId(3)
            .restrictionType(RestrictionType.NOT_ALLOWED)
            .addVendor(3)
            .addVendor(7);

        b.addPublisherRestrictionEntry(pre.build());

        pre.purposeId(1)
            .clearVendors()
            .addVendor(3);
        b.addPublisherRestrictionEntry(pre.build());

        decoded = TCString.decode(b.encode());
        List<PublisherRestriction> pubRest = decoded.getPublisherRestrictions();

        assertEquals(2, pubRest.size());
        assertEquals(3, pubRest.get(0).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(0).getRestrictionType());
        assertThat(pubRest.get(0).getVendorIds(), matchInts(3, 7));

        assertEquals(1, pubRest.get(1).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(1).getRestrictionType());
        assertThat(pubRest.get(1).getVendorIds(), matchInts(3));
    }

    @Test
    public void testToTCString() {
        TCString tcStr = TCString.decode("COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA");
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(tcStr);
        TCString tcStr1 = b.toTCString();
        assertEquals(tcStr, tcStr1);

        BitSet av = BitSetIntIterable.from(tcStr.getAllowedVendors()).toBitSet();
        av.set(400);
        b.allowedVendors(new BitSetIntIterable(av));
        assertNotEquals(tcStr, b.toTCString());
    }
}
