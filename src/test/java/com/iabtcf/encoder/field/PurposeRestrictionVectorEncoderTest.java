package com.iabtcf.encoder.field;

import com.iabtcf.model.PurposeRestriction;
import com.iabtcf.model.PurposeRestrictionVector;
import com.iabtcf.model.RestrictionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PurposeRestrictionVectorEncoderTest {
    private static final String NUM_RESTRICTION = "000000000001";
    private static final String PURPOSE_ID = "000010";
    private static final String RESTRICTION_TYPE = "01";
    private static final String RANGE_NUM_ENTRIES = "000000000010";

    private static final String IS_A_RANGE = "1";
    private static final String RANGE_2 = "0000000000000010";
    private static final String RANGE_4 = "0000000000000100";
    private static final String RANGE_2_TO_4 = IS_A_RANGE + RANGE_2 + RANGE_4;

    private static final String IS_NOT_A_RANGE = "0";
    private static final String RANGE_8 = "0000000000001000";
    private static final String RANGE_8_TO_8 = IS_NOT_A_RANGE + RANGE_8;

    private static final String VALID_VALUE =
            NUM_RESTRICTION + PURPOSE_ID + RESTRICTION_TYPE + RANGE_NUM_ENTRIES + RANGE_2_TO_4 + RANGE_8_TO_8;
    private static final PurposeRestrictionVector EXPECTED_RETURN = new PurposeRestrictionVector();
    private static final int PURPOSE_ID_NUMBER = 2;
    private static final PurposeRestriction PURPOSE_RESTRICTION = new PurposeRestriction(PURPOSE_ID_NUMBER, RestrictionType.REQUIRE_CONSENT);

    @Before
    public void before() {
        for (int i = 2; i <= 4; i++) {
            EXPECTED_RETURN.add(i, PURPOSE_RESTRICTION);
        }

        EXPECTED_RETURN.add(8, PURPOSE_RESTRICTION);
        EXPECTED_RETURN.setBitLength(VALID_VALUE.length());
    }

    @Test
    public void whenValueIsValidThenShouldReturnExpectedSortedVector() {
        Assert.assertEquals(EXPECTED_RETURN, PurposeRestrictionVectorEncoder.getInstance().decode(VALID_VALUE));
    }
}
