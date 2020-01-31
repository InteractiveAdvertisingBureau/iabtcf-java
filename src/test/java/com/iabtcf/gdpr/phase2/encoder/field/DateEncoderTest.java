package com.iabtcf.gdpr.phase2.encoder.field;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateEncoderTest {
    private static final long EXPECTED_DATE_IN_MILLISECONDS = (new Date()).getTime();
    private static final long EXPECTED_DATE_IN_DECISECONDS = EXPECTED_DATE_IN_MILLISECONDS / 100;
    private static final Date EXPECTED_RETURN = new Date(EXPECTED_DATE_IN_DECISECONDS * 100);
    private static final String VALID_VALUE = Long.toBinaryString(EXPECTED_DATE_IN_DECISECONDS);

    private static final String INVALID_VALUE = "1234";
    private static final String EMPTY_VALUE = "";

    @Test
    public void whenValueIsValidShouldReturnExpectedDate() {
        Assert.assertEquals(EXPECTED_RETURN, DateEncoder.getInstance().decode(VALID_VALUE));
    }

    @Test
    public void whenValueIsInvalidShouldReturnNull() {
        Assert.assertNull(DateEncoder.getInstance().decode(INVALID_VALUE));
        Assert.assertNull(DateEncoder.getInstance().decode(EMPTY_VALUE));
    }
}
