package com.iabtcf.encoder.field;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateEncoderTest {
    private static final long EXPECTED_DATE_IN_MILLISECONDS = (new Date()).getTime();
    private static final long EXPECTED_DATE_IN_DECISECONDS = EXPECTED_DATE_IN_MILLISECONDS / 100;
    private static final Date EXPECTED_RETURN = new Date(EXPECTED_DATE_IN_DECISECONDS * 100);
    private static final String VALID_VALUE = Long.toBinaryString(EXPECTED_DATE_IN_DECISECONDS);

    private static final String INVALID_VALUE = "invalid_value";
    private static final String EMPTY_VALUE = "";
    private static final String NULL_VALUE = null;

    @Test
    public void whenValueIsValidShouldReturnExpectedDate() {
        Assert.assertEquals(EXPECTED_RETURN, DateEncoder.getInstance().decode(VALID_VALUE));
    }

    @Test
    public void whenValueIsInvalidShouldReturnNull() {
        Assert.assertNull(DateEncoder.getInstance().decode(INVALID_VALUE));
    }

    @Test
    public void whenValueIsEmptyShouldReturnNull() {
        Assert.assertNull(DateEncoder.getInstance().decode(EMPTY_VALUE));
    }

    @Test
    public void whenValueIsNullShouldReturnNull() {
        Assert.assertNull(DateEncoder.getInstance().decode(NULL_VALUE));
    }

}
