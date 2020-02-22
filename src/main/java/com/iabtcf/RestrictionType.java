package com.iabtcf;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
public enum RestrictionType {

    NOT_ALLOWED(0),
    REQUIRE_CONSENT(1),
    REQUIRE_LEGITIMATE_INTEREST(2),
    UNDEFINED(3);

    private final int value;

    RestrictionType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RestrictionType fromId(int id) {
        switch (id) {
            case 0:
                return NOT_ALLOWED;
            case 1:
                return REQUIRE_CONSENT;
            case 2:
                return REQUIRE_LEGITIMATE_INTEREST;
            default:
                return UNDEFINED;
        }
    }
}
