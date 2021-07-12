package com.iabtcf.encoder;

import static com.iabtcf.encoder.utils.TestUtils.toDeci;
import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import com.iabtcf.decoder.TCString;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.PublisherRestriction;
import com.iabtcf.v2.RestrictionType;

public class TCStringV2EncoderTest {

    private final Instant created = Instant.now();
    private final Instant updated = created.plus(1, ChronoUnit.HOURS);
    private TCStringEncoder.Builder encoderBuilder;

    @Before
    public void setUp() {
        encoderBuilder = TCStringEncoder.newBuilder()
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
            .addSpecialFeatureOptIns(BitSetIntIterable.from(1, 2))
            .addPurposesConsent(BitSetIntIterable.from(4, 8))
            .addPurposesLITransparency(BitSetIntIterable.from(11, 20))
            .purposeOneTreatment(true)
            .publisherCC("DE")
            .addVendorConsent(BitSetIntIterable.from(1, 4))
            .addVendorLegitimateInterest(BitSetIntIterable.from(2, 6));
    }

    @Test
    public void testIssue125_1() throws Exception {
        final IntIterable vendorConsent = BitSetIntIterable.from(130, 202, 184, 385, 215, 285, 343, 746, 228, 550, 69,
                120, 275, 599, 368, 501, 50, 100, 301, 345, 98, 126, 574, 416, 639, 236, 512, 577, 277, 388, 185,
                678, 436, 272, 769, 2, 598, 697, 199, 423, 617, 381, 153, 687, 606, 662, 311, 131, 129, 49, 469,
                11, 192, 315, 681, 115, 702, 226, 707, 452, 173, 119, 193, 66, 507, 15, 77, 68, 10, 251, 16, 431,
                31, 40, 164, 91, 111, 97, 793, 565, 468, 434, 12, 36, 28, 360, 791, 21, 889, 45, 162, 316, 448, 79,
                82, 109, 127, 253, 39, 76, 170, 128, 144, 812, 780, 772, 506, 418, 114, 244, 361, 384, 435, 124, 71,
                264, 254, 813, 27, 52, 60, 95, 278, 475, 663, 744, 671, 163, 25, 32, 42, 92, 132, 102, 142, 150, 152,
                165, 284, 53, 93, 231, 234, 239, 916, 373, 830, 666, 922, 259, 573, 78);

        final IntIterable vendorLegitimateInterest =
                BitSetIntIterable.from(130, 202, 184, 385, 215, 285, 343, 746, 228, 550, 69, 120, 275, 599, 368,
                        501, 50, 100, 301, 345, 98, 126, 574, 416, 639, 236, 512, 577, 277, 388, 185, 678, 436, 272,
                        769, 2, 598, 697, 199, 423, 617, 381, 153, 687, 606, 662, 311, 131, 129, 49, 469, 11, 192, 315, 681,
                        115, 702, 226, 707, 452, 173, 119, 193, 66, 507, 15, 77, 68, 10, 251, 16, 431, 31, 40, 164, 91,
                        111, 97, 793, 565, 468, 434, 12, 36, 28, 360, 791, 21, 889, 45, 162, 316, 448, 79, 82, 109, 127,
                        253, 39, 76, 170, 128, 144, 812, 780, 772, 506, 418, 114, 244, 361, 384, 435, 124, 71, 264, 254,
                        813, 27, 52, 60, 95, 278, 475, 663, 744, 671, 163, 25, 32, 42, 92, 132, 102, 142, 150, 152, 165,
                        284, 53, 93, 231, 234, 239, 916, 373, 830, 666, 922, 259, 573, 78);


        encoderBuilder.clearVendorConsent();
        encoderBuilder.addVendorConsent(vendorConsent);

        encoderBuilder.clearVendorLegitimateInterest();
        encoderBuilder.addVendorLegitimateInterest(vendorLegitimateInterest);

        String tcf = TCStringEncoder.newBuilder(encoderBuilder).encode();

        TCString decoded = TCString.decode(tcf);

        MatcherAssert.assertThat(decoded.getVendorConsent(), matchInts(vendorConsent.toSet()));
        MatcherAssert.assertThat(decoded.getVendorLegitimateInterest(), matchInts(vendorLegitimateInterest.toSet()));
    }

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
        MatcherAssert.assertThat(decoded.getSpecialFeatureOptIns(), matchInts(1, 2));
        MatcherAssert.assertThat(decoded.getPurposesConsent(), matchInts(4, 8));
        MatcherAssert.assertThat(decoded.getPurposesLITransparency(), matchInts(11, 20));
        assertTrue(decoded.getPurposeOneTreatment());
        assertEquals("DE", decoded.getPublisherCC());
        MatcherAssert.assertThat(decoded.getVendorConsent(), matchInts(1, 4));
        MatcherAssert.assertThat(decoded.getVendorLegitimateInterest(), matchInts(2, 6));
    }

    @Test
    public void testItDecodesAllOptionalSegments() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
            .addDisclosedVendors(BitSetIntIterable.from(1, 2))
            .addAllowedVendors(BitSetIntIterable.from(6, 11))
            .addPubPurposesConsent(BitSetIntIterable.from(7, 9))
            .addPubPurposesLITransparency(BitSetIntIterable.from(2, 3))
            .addCustomPurposesConsent(BitSetIntIterable.from(1, 2))
            .addCustomPurposesLITransparency(BitSetIntIterable.from(1, 3, 4))
            .encode();

        TCString decoded = TCString.decode(tcf);

        assertEquals(4, tcf.split("\\.").length);
        MatcherAssert.assertThat(decoded.getDisclosedVendors(), matchInts(1, 2));
        MatcherAssert.assertThat(decoded.getAllowedVendors(), matchInts(6, 11));
        MatcherAssert.assertThat(decoded.getPubPurposesLITransparency(), matchInts(2, 3));
        MatcherAssert.assertThat(decoded.getCustomPurposesConsent(), matchInts(1, 2));
        MatcherAssert.assertThat(decoded.getCustomPurposesLITransparency(), matchInts(1, 3, 4));
    }

    @Test
    public void testEncodedVendorDisclosedSection() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
            .addDisclosedVendors(BitSetIntIterable.from(1, 2))
            .encode();

        TCString decoded = TCString.decode(tcf);
        assertEquals(2, tcf.split("\\.").length);
        MatcherAssert.assertThat(decoded.getDisclosedVendors(), matchInts(1, 2));
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
        MatcherAssert.assertThat(pubRest.get(0).getVendorIds(), matchInts(3, 7));

        assertEquals(1, pubRest.get(1).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(1).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(1).getVendorIds(), matchInts(3));
    }

    @Test
    public void testPubisherRestrictionsAddVendors() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);

        TCString decoded = TCString.decode(b.encode());
        assertEquals(0, decoded.getPublisherRestrictions().size());

        PublisherRestrictionEntry.Builder pre = PublisherRestrictionEntry.newBuilder()
            .purposeId(3)
            .restrictionType(RestrictionType.NOT_ALLOWED)
            .addVendor(3, 7)
            .addVendor(BitSetIntIterable.from(1, 10));

        b.addPublisherRestrictionEntry(pre.build());
        b.clearPublisherRestrictionEntry();
        b.addPublisherRestrictionEntry(pre.build());

        PublisherRestrictionEntry.Builder pre1 = PublisherRestrictionEntry.newBuilder(pre)
            .purposeId(4)
            .restrictionType(RestrictionType.NOT_ALLOWED)
            .addVendor(BitSetIntIterable.from(1, 2));

        b.addPublisherRestrictionEntry(pre1.build());

        PublisherRestrictionEntry.Builder pre2 = PublisherRestrictionEntry.newBuilder(pre.build())
            .purposeId(5)
            .restrictionType(RestrictionType.NOT_ALLOWED)
            .addVendor(BitSetIntIterable.from(1, 2));

        b.addPublisherRestrictionEntry(pre2.build(), pre2.build());
        List<PublisherRestrictionEntry> l = new ArrayList<>();
        l.add(pre2.build());
        l.add(pre2.build());
        b.addPublisherRestrictionEntry(l);

        decoded = TCString.decode(b.encode());
        List<PublisherRestriction> pubRest = decoded.getPublisherRestrictions();

        assertEquals(6, pubRest.size());
        assertEquals(3, pubRest.get(0).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(0).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(0).getVendorIds(), matchInts(1, 3, 7, 10));

        assertEquals(4, pubRest.get(1).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(1).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(1).getVendorIds(), matchInts(1, 2, 3, 7, 10));

        assertEquals(5, pubRest.get(2).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(2).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(2).getVendorIds(), matchInts(1, 2, 3, 7, 10));

        assertEquals(5, pubRest.get(3).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(3).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(3).getVendorIds(), matchInts(1, 2, 3, 7, 10));

        assertEquals(5, pubRest.get(4).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(4).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(4).getVendorIds(), matchInts(1, 2, 3, 7, 10));

        assertEquals(5, pubRest.get(5).getPurposeId());
        assertEquals(RestrictionType.NOT_ALLOWED, pubRest.get(5).getRestrictionType());
        MatcherAssert.assertThat(pubRest.get(5).getVendorIds(), matchInts(1, 2, 3, 7, 10));
    }

    @Test
    public void testAddClearPurposesConsent() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addPurposesConsent(10);

        MatcherAssert.assertThat(b.toTCString().getPurposesConsent(), matchInts(4, 8, 10));

        b.clearPurposesConsent();
        b.addPurposesConsent(11);

        MatcherAssert.assertThat(b.toTCString().getPurposesConsent(), matchInts(11));
    }

    @Test
    public void testAddClearVendorConsent() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addVendorConsent(10);

        MatcherAssert.assertThat(b.toTCString().getVendorConsent(), matchInts(1, 4, 10));

        b.clearVendorConsent();
        b.addVendorConsent(11);

        MatcherAssert.assertThat(b.toTCString().getVendorConsent(), matchInts(11));
    }

    @Test
    public void testAddClearpecialFeatureOptIns() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addSpecialFeatureOptIns(10);

        MatcherAssert.assertThat(b.toTCString().getSpecialFeatureOptIns(), matchInts(1, 2, 10));

        b.clearSpecialFeatureOptIns();
        b.addSpecialFeatureOptIns(11);

        MatcherAssert.assertThat(b.toTCString().getSpecialFeatureOptIns(), matchInts(11));
    }

    @Test
    public void testAddClearPurposesLITransparency() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addPurposesLITransparency(10);

        MatcherAssert.assertThat(b.toTCString().getPurposesLITransparency(), matchInts(10, 11, 20));

        b.clearPurposesLITransparency();
        b.addPurposesLITransparency(11);

        MatcherAssert.assertThat(b.toTCString().getPurposesLITransparency(), matchInts(11));
    }

    @Test
    public void testAddClearVendorLegitimateInterest() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addVendorLegitimateInterest(10);

        MatcherAssert.assertThat(b.toTCString().getVendorLegitimateInterest(), matchInts(2, 6, 10));

        b.clearVendorLegitimateInterest();
        b.addVendorLegitimateInterest(11);

        MatcherAssert.assertThat(b.toTCString().getVendorLegitimateInterest(), matchInts(11));
    }

    @Test
    public void testAddClearDisclosedVendors() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addDisclosedVendors(BitSetIntIterable.from(9, 7));
        b.addDisclosedVendors(10);

        MatcherAssert.assertThat(b.toTCString().getDisclosedVendors(), matchInts(7, 9, 10));

        b.clearDisclosedVendors();
        b.addDisclosedVendors(11);

        MatcherAssert.assertThat(b.toTCString().getDisclosedVendors(), matchInts(11));
    }

    @Test
    public void testAddClearPubPurposesConsent() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addPubPurposesConsent(BitSetIntIterable.from(24, 1));
        b.addPubPurposesConsent(23);

        MatcherAssert.assertThat(b.toTCString().getPubPurposesConsent(), matchInts(24, 1, 23));

        b.clearPubPurposesConsent();
        b.addPubPurposesConsent(11);

        MatcherAssert.assertThat(b.toTCString().getPubPurposesConsent(), matchInts(11));
    }

    @Test
    public void testAddClearCustomPurposesConsent() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addCustomPurposesConsent(BitSetIntIterable.from(50, 20));
        b.addCustomPurposesConsent(10);

        MatcherAssert.assertThat(b.toTCString().getCustomPurposesConsent(), matchInts(50, 20, 10));

        b.clearCustomPurposesConsent();
        b.addCustomPurposesConsent(11);

        MatcherAssert.assertThat(b.toTCString().getCustomPurposesConsent(), matchInts(11));
    }

    @Test
    public void testAddClearCustomPurposesLITransparency() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addCustomPurposesLITransparency(BitSetIntIterable.from(50, 20));
        b.addCustomPurposesLITransparency(10);

        MatcherAssert.assertThat(b.toTCString().getCustomPurposesLITransparency(), matchInts(50, 20, 10));

        b.clearCustomPurposesLITransparency();
        b.addCustomPurposesLITransparency(11);

        MatcherAssert.assertThat(b.toTCString().getCustomPurposesLITransparency(), matchInts(11));
    }

    @Test
    public void testAddClearPubPurposesLITransparency() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addPubPurposesLITransparency(BitSetIntIterable.from(24, 1));
        b.addPubPurposesLITransparency(23);

        MatcherAssert.assertThat(b.toTCString().getPubPurposesLITransparency(), matchInts(24, 1, 23));

        b.clearPubPurposesLITransparency();
        b.addPubPurposesLITransparency(11);

        MatcherAssert.assertThat(b.toTCString().getPubPurposesLITransparency(), matchInts(11));
    }

    @Test
    public void testAddClearAllowedVendors() {
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(encoderBuilder);
        b.addAllowedVendors(BitSetIntIterable.from(24, 1));
        b.addAllowedVendors(23);

        MatcherAssert.assertThat(b.toTCString().getAllowedVendors(), matchInts(24, 1, 23));

        b.clearAllowedVendors();
        b.addAllowedVendors(11);

        MatcherAssert.assertThat(b.toTCString().getAllowedVendors(), matchInts(11));
    }

    @Test
    public void testToTCString() {
        TCString tcStr = TCString.decode("COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA");
        TCStringEncoder.Builder b = TCStringEncoder.newBuilder(tcStr);
        TCString tcStr1 = b.toTCString();
        assertEquals(tcStr, tcStr1);

        b.addAllowedVendors(BitSetIntIterable.from(tcStr.getAllowedVendors()))
            .addAllowedVendors(400);
        assertNotEquals(tcStr, b.toTCString());
    }
}
