package com.iabtcf.encoder.field;

import org.junit.Assert;
import org.junit.Test;

public class IntEncoderTest {
    private static final String VALUE_1 = Integer.toBinaryString(1);
    private static final String VALUE_10 = Integer.toBinaryString(10);
    private static final Integer EXPECTED_VALUE_1 = 1;
    private static final Integer EXPECTED_VALUE_10 = 10;

    @Test
    public void whenValueIsValidThenShouldReturnExpectedValue() {
        Assert.assertEquals(EXPECTED_VALUE_1, IntEncoder.getInstance().decode(VALUE_1));
        Assert.assertEquals(EXPECTED_VALUE_10, IntEncoder.getInstance().decode(VALUE_10));
    }
}
