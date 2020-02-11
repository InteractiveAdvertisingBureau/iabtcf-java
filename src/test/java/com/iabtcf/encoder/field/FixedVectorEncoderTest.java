package com.iabtcf.encoder.field;

import com.iabtcf.model.SortedVector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FixedVectorEncoderTest {
    private static final String VALID_VALUE = "1010101000";
    private static final SortedVector EXPECTED_RETURN = new SortedVector();

    @Before
    public void before() {
        for (int i = 0; i < VALID_VALUE.length(); i++) {
            if (VALID_VALUE.charAt(i) == '1') {
                EXPECTED_RETURN.getSet().add(i + 1);
            }
        }
        EXPECTED_RETURN.setBitLength(VALID_VALUE.length());
    }

    @Test
    public void whenValueIsValidThenShouldReturnExpectedSortedVector() {
        Assert.assertEquals(EXPECTED_RETURN, FixedVectorEncoder.getInstance().decode(VALID_VALUE));
    }
}
