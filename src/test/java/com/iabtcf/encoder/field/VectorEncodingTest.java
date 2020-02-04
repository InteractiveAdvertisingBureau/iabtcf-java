package com.iabtcf.encoder.field;

import org.junit.Assert;
import org.junit.Test;

public class VectorEncodingTest {
    private static final int VALUE_FIELD = 0;
    private static final int VALUE_RANGE = 1;

    @Test
    public void fieldValueShouldToBeZero() {
        Assert.assertEquals(VALUE_FIELD, VectorEncodingType.FIELD.getType());
    }

    @Test
    public void rangeValueShouldToBeOne() {
        Assert.assertEquals(VALUE_RANGE, VectorEncodingType.RANGE.getType());
    }
}
