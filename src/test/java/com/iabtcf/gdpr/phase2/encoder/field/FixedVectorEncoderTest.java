package com.iabtcf.gdpr.phase2.encoder.field;

import com.iabtcf.gdpr.phase2.model.SortedVector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FixedVectorEncoderTest {
    private static final String VALID_VALUE = "1010101000";
    private static final SortedVector EXPECTED_RETURN = new SortedVector();
    private static final String INVALID_VALUE = null;

    @Before
    public void before() {
        for (int i = 0; i < VALID_VALUE.length(); i++) {
            if (VALID_VALUE.charAt(i) == '1')
                EXPECTED_RETURN.getSet().add(i + 1);
        }
        EXPECTED_RETURN.setBitLength(VALID_VALUE.length());
    }

    @Test
    public void whenValueIsValidThenShouldReturnExpectedSortedVector() {
        Assert.assertEquals(EXPECTED_RETURN, FixedVectorEncoder.getInstance().decode(VALID_VALUE));
    }

    @Test
    public void whenValueIsInvalidThenShouldReturnNull() {
        Assert.assertNull(FixedVectorEncoder.getInstance().decode(INVALID_VALUE));
    }

}
