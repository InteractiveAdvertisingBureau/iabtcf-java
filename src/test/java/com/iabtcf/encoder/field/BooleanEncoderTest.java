package com.iabtcf.encoder.field;

import org.junit.Assert;
import org.junit.Test;

public class BooleanEncoderTest {
    private static final String VALUE_0 = "0";
    private static final String VALUE_1 = "1";
    private static final String INVALID_VALUE = "231";
    private static final String EMPTY_VALUE = "";

    @Test
    public void whenValueIsZeroShouldReturnFalse() {
        Assert.assertFalse(BooleanEncoder.getInstance().decode(VALUE_0));
    }

    @Test
    public void whenValueIsOneShouldReturnTrue() {
        Assert.assertTrue(BooleanEncoder.getInstance().decode(VALUE_1));
    }

    @Test
    public void whenValueIsInvalidShouldReturnFalse() {
        Assert.assertFalse(BooleanEncoder.getInstance().decode(EMPTY_VALUE));
        Assert.assertFalse(BooleanEncoder.getInstance().decode(INVALID_VALUE));
    }
}
