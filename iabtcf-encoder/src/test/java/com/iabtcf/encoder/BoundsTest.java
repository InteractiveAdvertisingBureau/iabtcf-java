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
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.iabtcf.FieldDefs;
import com.iabtcf.encoder.exceptions.ValueOverflowException;
import com.iabtcf.utils.BitSetIntIterable;

public class BoundsTest {
    private final Instant created = Instant.now();
    private final Instant updated = created.plus(1, ChronoUnit.HOURS);

    private TCStringEncoder.Builder encoderBuilder;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
    public void testCmpIdMax() {
        int value = 1 << FieldDefs.CORE_CMP_ID.getLength() - 1;
        encoderBuilder.cmpId(value);
        assertEquals(value, encoderBuilder.toTCString().getCmpId());
    }

    @Test(expected = ValueOverflowException.class)
    public void testCmpIdOverflow() {
        int value = 1 << FieldDefs.CORE_CMP_ID.getLength();
        encoderBuilder.cmpId(value);
        encoderBuilder.toTCString();
    }

    @Test
    public void testCmpVersionMax() {
        int value = 1 << FieldDefs.CORE_CMP_VERSION.getLength() - 1;
        encoderBuilder.cmpVersion(value);
        assertEquals(value, encoderBuilder.toTCString().getCmpVersion());
    }

    @Test(expected = ValueOverflowException.class)
    public void testCmpVersionOverflow() {
        int value = 1 << FieldDefs.CORE_CMP_VERSION.getLength();
        encoderBuilder.cmpVersion(value);
        encoderBuilder.toTCString();
    }

    @Test
    public void testConsentScreenMax() {
        int value = 1 << FieldDefs.CORE_CONSENT_SCREEN.getLength() - 1;
        encoderBuilder.consentScreen(value);
        assertEquals(value, encoderBuilder.toTCString().getConsentScreen());
    }

    @Test(expected = ValueOverflowException.class)
    public void testConsentScreenOverflow() {
        int value = 1 << FieldDefs.CORE_CONSENT_SCREEN.getLength();
        encoderBuilder.consentScreen(value);
        encoderBuilder.toTCString();
    }

    @Test
    public void testVendorListVersionMax() {
        int value = 1 << FieldDefs.CORE_VENDOR_LIST_VERSION.getLength() - 1;
        encoderBuilder.vendorListVersion(value);
        assertEquals(value, encoderBuilder.toTCString().getVendorListVersion());
    }

    @Test(expected = ValueOverflowException.class)
    public void testVendorListVersionOverflow() {
        int value = 1 << FieldDefs.CORE_VENDOR_LIST_VERSION.getLength();
        encoderBuilder.vendorListVersion(value);
        encoderBuilder.toTCString();
    }

    @Test
    public void testTcfPolicyVersionMax() {
        int value = 1 << FieldDefs.CORE_TCF_POLICY_VERSION.getLength() - 1;
        encoderBuilder.tcfPolicyVersion(value);
        assertEquals(value, encoderBuilder.toTCString().getTcfPolicyVersion());
    }

    @Test(expected = ValueOverflowException.class)
    public void testTcfPolicyVersionOverflow() {
        int value = 1 << FieldDefs.CORE_TCF_POLICY_VERSION.getLength();
        encoderBuilder.tcfPolicyVersion(value);
        encoderBuilder.toTCString();
    }

    @Test
    public void testPurposesConsentMax() {
        int maxValue = FieldDefs.CORE_PURPOSES_CONSENT.getLength();;
        encoderBuilder.clearPurposesConsent();
        encoderBuilder.addPurposesConsent(maxValue);
        assertTrue(encoderBuilder.toTCString().getPurposesConsent().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testPurposesConsentOverflow() {
        int maxValue = FieldDefs.CORE_PURPOSES_CONSENT.getLength() + 1;
        encoderBuilder.clearPurposesConsent();
        encoderBuilder.addPurposesConsent(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testVendorsConsentMax() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength()) - 1;
        encoderBuilder.clearVendorConsent();
        encoderBuilder.addVendorConsent(maxValue);
        assertTrue(encoderBuilder.toTCString().getVendorConsent().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testVendorsConsentOverflow() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength());
        encoderBuilder.clearVendorConsent();
        encoderBuilder.addVendorConsent(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testSpecialFeatureOptInsMax() {
        int maxValue = FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS.getLength();
        encoderBuilder.clearSpecialFeatureOptIns();
        encoderBuilder.addSpecialFeatureOptIns(maxValue);
        assertTrue(encoderBuilder.toTCString().getSpecialFeatureOptIns().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testSpecialFeatureOptInsOverflow() {
        int maxValue = FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS.getLength() + 1;
        encoderBuilder.clearSpecialFeatureOptIns();
        encoderBuilder.addSpecialFeatureOptIns(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testPurposesLITransparencyMax() {
        int maxValue = FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY.getLength();
        encoderBuilder.clearPurposesLITransparency();
        encoderBuilder.addPurposesLITransparency(maxValue);
        assertTrue(encoderBuilder.toTCString().getPurposesLITransparency().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testPurposesLITransparencyOverflow() {
        int maxValue = FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY.getLength() + 1;
        encoderBuilder.clearPurposesLITransparency();
        encoderBuilder.addPurposesLITransparency(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testVendorLegitimateInterestMax() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength()) - 1;
        encoderBuilder.clearVendorLegitimateInterest();
        encoderBuilder.addVendorLegitimateInterest(maxValue);
        assertTrue(encoderBuilder.toTCString().getVendorLegitimateInterest().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testVendorLegitimateInterestOverflow() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength());
        encoderBuilder.clearVendorLegitimateInterest();
        encoderBuilder.addVendorLegitimateInterest(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testDisclosedVendorsMax() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength()) - 1;
        encoderBuilder.clearDisclosedVendors();
        encoderBuilder.addDisclosedVendors(maxValue);
        assertTrue(encoderBuilder.toTCString().getDisclosedVendors().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testDisclosedVendorsOverflow() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength());
        encoderBuilder.clearDisclosedVendors();
        encoderBuilder.addDisclosedVendors(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testAllowedVendorsMax() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength()) - 1;
        encoderBuilder.clearAllowedVendors();
        encoderBuilder.addAllowedVendors(maxValue);
        assertTrue(encoderBuilder.toTCString().getAllowedVendors().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testAllowedVendorsOverflow() {
        int maxValue = (1 << FieldDefs.CORE_VENDOR_MAX_VENDOR_ID.getLength());
        encoderBuilder.clearAllowedVendors();
        encoderBuilder.addAllowedVendors(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testPubPurposesConsentMax() {
        int maxValue = FieldDefs.PPTC_PUB_PURPOSES_CONSENT.getLength();
        encoderBuilder.clearPubPurposesConsent();
        encoderBuilder.addPubPurposesConsent(maxValue);
        assertTrue(encoderBuilder.toTCString().getPubPurposesConsent().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testPubPurposesConsentOverflow() {
        int maxValue = FieldDefs.PPTC_PUB_PURPOSES_CONSENT.getLength() + 1;
        encoderBuilder.clearPubPurposesConsent();
        encoderBuilder.addPubPurposesConsent(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testCustomPurposesConsentMax() {
        int maxValue = (1 << FieldDefs.PPTC_NUM_CUSTOM_PURPOSES.getLength()) - 1;
        encoderBuilder.clearCustomPurposesConsent();
        encoderBuilder.addCustomPurposesConsent(maxValue);
        assertTrue(encoderBuilder.toTCString().getCustomPurposesConsent().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testCustomPurposesConsentOverflow() {
        int maxValue = (1 << FieldDefs.PPTC_NUM_CUSTOM_PURPOSES.getLength());
        encoderBuilder.clearCustomPurposesConsent();
        encoderBuilder.addCustomPurposesConsent(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testCustomPurposesLITransparencyMax() {
        int maxValue = (1 << FieldDefs.PPTC_NUM_CUSTOM_PURPOSES.getLength()) - 1;
        encoderBuilder.clearCustomPurposesLITransparency();
        encoderBuilder.addCustomPurposesLITransparency(maxValue);
        assertTrue(encoderBuilder.toTCString().getCustomPurposesLITransparency().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testCustomPurposesLITransparencyOverflow() {
        int maxValue = (1 << FieldDefs.PPTC_NUM_CUSTOM_PURPOSES.getLength());
        encoderBuilder.clearCustomPurposesLITransparency();
        encoderBuilder.addCustomPurposesLITransparency(maxValue);
        encoderBuilder.toTCString();
    }
    @Test
    public void testPubPurposesLITransparencyMax() {
        int maxValue = FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY.getLength();
        encoderBuilder.clearPubPurposesLITransparency();
        encoderBuilder.addPubPurposesLITransparency(maxValue);
        assertTrue(encoderBuilder.toTCString().getPubPurposesLITransparency().contains(maxValue));
    }

    @Test(expected = ValueOverflowException.class)
    public void testPubPurposesLITransparencyOverflow() {
        int maxValue = FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY.getLength() + 1;
        encoderBuilder.clearPubPurposesLITransparency();
        encoderBuilder.addPubPurposesLITransparency(maxValue);
        encoderBuilder.toTCString();
    }
}
