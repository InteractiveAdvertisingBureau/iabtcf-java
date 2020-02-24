package com.iabtcf;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
public enum RestrictionType {

    /**
     * Purpose flatly not allowed by publisher
     */
    NOT_ALLOWED(0),
    /**
     * if Vendor has declared the Purpose IDs legal basis as Legitimate Interest and flexible
     */
    REQUIRE_CONSENT(1),
    /**
     * if Vendor has declared the Purpose IDs legal basis as Consent and flexible
     */
    REQUIRE_LEGITIMATE_INTEREST(2),
    /**
     * Not Used or Not Known
     */
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
