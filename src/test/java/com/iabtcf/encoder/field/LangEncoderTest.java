package com.iabtcf.encoder.field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LangEncoderTest {
    private static final String EXPECTED_VALUE_PT = "PT";
    private static final String EXPECTED_VALUE_EN = "EN";
    private static final String INVALID_VALUE = "invalid_value";
    private static final String EMPTY_VALUE = "";
    private static final String NULL_VALUE = null;
    private static String VALUE_PT = "";
    private static String VALUE_EN = "";

    @Before
    public void before() {
        VALUE_PT = getBinaryISO31661Alpha2Code('P') + getBinaryISO31661Alpha2Code('T');
        VALUE_EN = getBinaryISO31661Alpha2Code('E') + getBinaryISO31661Alpha2Code('N');
    }

    @Test
    public void whenValueIsValidThenShouldReturnExpectedValue() {
        Assert.assertEquals(EXPECTED_VALUE_PT, LangEncoder.getInstance().decode(VALUE_PT));
        Assert.assertEquals(EXPECTED_VALUE_EN, LangEncoder.getInstance().decode(VALUE_EN));
    }

    @Test
    public void whenValueIsInvalidShouldReturnFalse() {
        Assert.assertNull(IntEncoder.getInstance().decode(INVALID_VALUE));
    }

    @Test
    public void whenValueIsEmptyShouldReturnFalse() {
        Assert.assertNull(IntEncoder.getInstance().decode(EMPTY_VALUE));
    }

    @Test
    public void whenValueIsNullShouldReturnFalse() {
        Assert.assertNull(IntEncoder.getInstance().decode(NULL_VALUE));
    }

    private String getBinaryISO31661Alpha2Code(char c) {
        String binary = Integer.toBinaryString(Character.getNumericValue(c) - Character.getNumericValue('A'));
        String ret = "";
        for (int i = binary.length(); i < 6; i++) {
            ret += "0";
        }
        ret += binary;
        return ret;
    }
}
