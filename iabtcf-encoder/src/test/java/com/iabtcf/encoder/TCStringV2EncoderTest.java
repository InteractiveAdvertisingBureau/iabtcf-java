package com.iabtcf.encoder;

import static com.iabtcf.encoder.utils.TestUtils.toDeci;
import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;

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

import org.junit.Assert;
import org.junit.Test;

import com.iabtcf.decoder.TCString;
import com.iabtcf.utils.BitSetIntIterable;

public class TCStringV2EncoderTest {

    private final Instant created = Instant.now();
    private final Instant updated = created.plus(1, ChronoUnit.HOURS);

    private final TCStringEncoder encoderBuilder = TCStringEncoder.newBuilder()
            .withVersion(2)
            .withCreated(created)
            .withLastUpdated(updated)
            .withCmpId(1)
            .withCmpVersion(12)
            .withConsentScreen(1)
            .withConsentLanguage("FR")
            .withVendorListVersion(2)
            .withTcfPolicyVersion(1)
            .withIsServiceSpecific(true)
            .withUseNonStandardStacks(false)
            .withSpecialFeatureOptIns(BitSetIntIterable.of(1, 2))
            .withPurposesConsent(BitSetIntIterable.of(4, 8))
            .withPurposesLITransparency(BitSetIntIterable.of(11, 20))
            .withPurposeOneTreatment(true)
            .withPublisherCC("DE")
            .withVendorsConsent(BitSetIntIterable.of(1, 4))
            .withVendorLegitimateInterest(BitSetIntIterable.of(2, 6));
    @Test
    public void testItConstructsCoreString() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
                .toTCFFormat();

        TCString decoded = TCString.decode(tcf);

        Assert.assertEquals(1, tcf.split("\\.").length);
        Assert.assertEquals(2, decoded.getVersion());
        Assert.assertEquals(toDeci(created), decoded.getCreated());
        Assert.assertEquals(toDeci(updated), decoded.getLastUpdated());
        Assert.assertEquals(1, decoded.getCmpId());
        Assert.assertEquals(12, decoded.getCmpVersion());
        Assert.assertEquals(1, decoded.getConsentScreen());
        Assert.assertEquals("FR", decoded.getConsentLanguage());
        Assert.assertEquals(2, decoded.getVendorListVersion());
        Assert.assertEquals(1, decoded.getTcfPolicyVersion());
        Assert.assertTrue(decoded.isServiceSpecific());
        Assert.assertFalse(decoded.getUseNonStandardStacks());
        Assert.assertThat(decoded.getSpecialFeatureOptIns(), matchInts(1, 2));
        Assert.assertThat(decoded.getPurposesConsent(), matchInts(4, 8));
        Assert.assertThat(decoded.getPurposesLITransparency(), matchInts(11, 20));
        Assert.assertTrue(decoded.getPurposeOneTreatment());
        Assert.assertEquals("DE", decoded.getPublisherCC());
        Assert.assertThat(decoded.getVendorConsent(), matchInts(1, 4));
        Assert.assertThat(decoded.getVendorLegitimateInterest(), matchInts(2, 6));
    }

    @Test
    public void testItDecodesAllOptionalSegments() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
                    .withDisclosedVendors(BitSetIntIterable.of(1, 2))
                    .withAllowedVendors(BitSetIntIterable.of(6, 11))
                    .withPubPurposesConsent(BitSetIntIterable.of(7, 9))
                    .withPubPurposesLITransparency(BitSetIntIterable.of(2, 3))
                    .withNumberOfCustomPurposesConsent(4)
                    .withCustomPurposesConsent(BitSetIntIterable.of(1, 2, 4))
                    .withCustomPurposesLITransparency(BitSetIntIterable.of(1, 3, 4))
                    .toTCFFormat();

        TCString decoded = TCString.decode(tcf);

        Assert.assertEquals(4, tcf.split("\\.").length);
        Assert.assertThat(decoded.getDisclosedVendors(), matchInts(1, 2));
        Assert.assertThat(decoded.getAllowedVendors(), matchInts(6, 11));
        Assert.assertThat(decoded.getPubPurposesLITransparency(), matchInts(2, 3));
        Assert.assertThat(decoded.getCustomPurposesConsent(), matchInts(1, 2, 4));
        Assert.assertThat(decoded.getCustomPurposesLITransparency(), matchInts(1, 3, 4));
    }

    @Test
    public void testEncodedVendorDisclosedSection() {
        String tcf = TCStringEncoder.newBuilder(encoderBuilder)
                .withDisclosedVendors(BitSetIntIterable.of(1, 2))
                .toTCFFormat();

        TCString decoded = TCString.decode(tcf);
        Assert.assertEquals(2, tcf.split("\\.").length);
        Assert.assertThat(decoded.getDisclosedVendors(), matchInts(1, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldFailIfLongStringIsProvided() {
        TCStringEncoder.newBuilder(encoderBuilder)
                .withConsentLanguage("GBR")
                .toTCFFormat();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldFailIfLowercaseStringIsProvided() {
        TCStringEncoder.newBuilder(encoderBuilder)
                .withConsentLanguage("gb")
                .toTCFFormat();
    }

}
