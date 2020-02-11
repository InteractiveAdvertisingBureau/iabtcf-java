package com.iabtcf.encoder.field;

import com.iabtcf.encoder.BaseEncoder;
import com.iabtcf.model.Fields;
import org.junit.Assert;
import org.junit.Test;

public class FieldEncoderMapTest {
    private static final FieldEncoderMap FIELD_ENCODER_MAP = FieldEncoderMap.getInstance();

    @Test
    public void whenMapKeyIsValidThenTheValueShouldIsCorrect() {
        Assert.assertEquals(FIELD_ENCODER_MAP.version, getValueOfFieldMap(Fields.version));
        Assert.assertEquals(FIELD_ENCODER_MAP.created, getValueOfFieldMap(Fields.created));
        Assert.assertEquals(FIELD_ENCODER_MAP.lastUpdated, getValueOfFieldMap(Fields.lastUpdated));
        Assert.assertEquals(FIELD_ENCODER_MAP.cmpId, getValueOfFieldMap(Fields.cmpId));
        Assert.assertEquals(FIELD_ENCODER_MAP.cmpVersion, getValueOfFieldMap(Fields.cmpVersion));
        Assert.assertEquals(FIELD_ENCODER_MAP.consentScreen, getValueOfFieldMap(Fields.consentScreen));
        Assert.assertEquals(FIELD_ENCODER_MAP.consentLanguage, getValueOfFieldMap(Fields.consentLanguage));
        Assert.assertEquals(FIELD_ENCODER_MAP.vendorListVersion, getValueOfFieldMap(Fields.vendorListVersion));
        Assert.assertEquals(FIELD_ENCODER_MAP.policyVersion, getValueOfFieldMap(Fields.policyVersion));
        Assert.assertEquals(FIELD_ENCODER_MAP.isServiceSpecific, getValueOfFieldMap(Fields.isServiceSpecific));
        Assert.assertEquals(FIELD_ENCODER_MAP.useNonStandardStacks, getValueOfFieldMap(Fields.useNonStandardStacks));
        Assert.assertEquals(FIELD_ENCODER_MAP.specialFeatureOptIns, getValueOfFieldMap(Fields.specialFeatureOptIns));
        Assert.assertEquals(FIELD_ENCODER_MAP.purposeConsents, getValueOfFieldMap(Fields.purposeConsents));
        Assert.assertEquals(FIELD_ENCODER_MAP.purposeLegitimateInterest,
                getValueOfFieldMap(Fields.purposeLegitimateInterest));
        Assert.assertEquals(FIELD_ENCODER_MAP.purposeOneTreatment, getValueOfFieldMap(Fields.purposeOneTreatment));
        Assert.assertEquals(FIELD_ENCODER_MAP.publisherCountryCode, getValueOfFieldMap(Fields.publisherCountryCode));
        Assert.assertEquals(FIELD_ENCODER_MAP.vendorConsents, getValueOfFieldMap(Fields.vendorConsents));
        Assert.assertEquals(FIELD_ENCODER_MAP.vendorLegitimateInterest,
                getValueOfFieldMap(Fields.vendorLegitimateInterest));
        Assert.assertEquals(FIELD_ENCODER_MAP.publisherRestrictions, getValueOfFieldMap(Fields.publisherRestrictions));
        Assert.assertEquals(FIELD_ENCODER_MAP.segmentType, getValueOfFieldMap(Fields.segmentType));
        Assert.assertEquals(FIELD_ENCODER_MAP.vendorsDisclosed, getValueOfFieldMap(Fields.vendorsDisclosed));
        Assert.assertEquals(FIELD_ENCODER_MAP.vendorsAllowed, getValueOfFieldMap(Fields.vendorsAllowed));
        Assert.assertEquals(FIELD_ENCODER_MAP.publisherConsents, getValueOfFieldMap(Fields.publisherConsents));
        Assert.assertEquals(FIELD_ENCODER_MAP.publisherLegitimateInterest,
                getValueOfFieldMap(Fields.publisherLegitimateInterest));
        Assert.assertEquals(FIELD_ENCODER_MAP.numCustomPurposes, getValueOfFieldMap(Fields.numCustomPurposes));
        Assert.assertEquals(FIELD_ENCODER_MAP.publisherCustomConsents,
                getValueOfFieldMap(Fields.publisherCustomConsents));
        Assert.assertEquals(FIELD_ENCODER_MAP.publisherCustomLegitimateInterest,
                getValueOfFieldMap(Fields.publisherCustomLegitimateInterest));
    }

    private BaseEncoder getValueOfFieldMap(String key) {
        return FIELD_ENCODER_MAP.fieldMap.get(key);
    }
}
