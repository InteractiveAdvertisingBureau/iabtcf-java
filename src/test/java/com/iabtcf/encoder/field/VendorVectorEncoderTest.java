package com.iabtcf.encoder.field;

import com.iabtcf.model.SortedVector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VendorVectorEncoderTest {
    private static final String IS_FIELD = "0";
    private static final String FIELD_MAX_ID = "0000000000001010";
    private static final String FIELD_VENDOR_CONSENT = "1010101000";
    private static final String VALID_FIELD_VALUE = FIELD_MAX_ID + IS_FIELD + FIELD_VENDOR_CONSENT;
    private static final SortedVector FIELD_EXPECTED_RETURN = new SortedVector();

    private static final String IS_RANGE = "1";
    private static final String RANGE_MAX_ID = "0000000000001000";
    private static final String RANGE_NUM_ENTRIES = "000000000010";
    private static final String RANGE_2 = "0000000000000010";
    private static final String RANGE_4 = "0000000000000100";
    private static final String IS_A_RANGE = "1";
    private static final String RANGE_2_TO_4 = IS_A_RANGE + RANGE_2 + RANGE_4;
    private static final String RANGE_8 = "0000000000001000";
    private static final String IS_NOT_A_RANGE = "0";
    private static final String RANGE_8_TO_8 = IS_NOT_A_RANGE + RANGE_8;
    private static final String VALID_RANGE_VALUE =
            RANGE_MAX_ID + IS_RANGE + RANGE_NUM_ENTRIES + RANGE_2_TO_4 + RANGE_8_TO_8;
    private static final SortedVector RANGE_EXPECTED_RETURN = new SortedVector();

    private static final String EMPTY_VALUE = "";
    private static final String INVALID_VALUE = "invalid_value";
    private static final String NULL_VALUE = null;

    @Before
    public void before() {
        //BitField
        for (int i = 0; i < FIELD_VENDOR_CONSENT.length(); i++) {
            if (FIELD_VENDOR_CONSENT.charAt(i) == '1')
                FIELD_EXPECTED_RETURN.getSet().add(i + 1);
        }
        FIELD_EXPECTED_RETURN.setBitLength(VALID_FIELD_VALUE.length());

        //Range
        for (int i = 2; i <= 4; i++) {
            RANGE_EXPECTED_RETURN.getSet().add(i);
        }
        RANGE_EXPECTED_RETURN.getSet().add(8);
        RANGE_EXPECTED_RETURN.setBitLength(VALID_RANGE_VALUE.length());
    }

    @Test
    public void whenFieldValueIsValidThenShouldReturnExpectedSortedVector() {
        Assert.assertEquals(FIELD_EXPECTED_RETURN, VendorVectorEncoder.getInstance().decode(VALID_FIELD_VALUE));
    }

    @Test
    public void whenRangeValueIsValidThenShouldReturnExpectedSortedVector() {
        Assert.assertEquals(RANGE_EXPECTED_RETURN, VendorVectorEncoder.getInstance().decode(VALID_RANGE_VALUE));
    }

    @Test
    public void whenValueIsEmptyThenShouldReturnNull() {
        Assert.assertNull(VendorVectorEncoder.getInstance().decode(EMPTY_VALUE));
    }

    @Test
    public void whenValueIsInvalidThenShouldReturnNull() {
        Assert.assertNull(VendorVectorEncoder.getInstance().decode(INVALID_VALUE));
    }

    @Test
    public void whenValueIsNullThenShouldReturnNull() {
        Assert.assertNull(VendorVectorEncoder.getInstance().decode(NULL_VALUE));
    }

}
