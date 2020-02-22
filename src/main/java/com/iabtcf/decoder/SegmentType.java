package com.iabtcf.decoder;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
enum SegmentType {

	DEFAULT(0),
	DISCLOSED_VENDOR(1),
	ALLOWED_VENDOR(2),
	PUBLISHER_TC(3);

	private final int value;

	SegmentType(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SegmentType get(final int type) {
		switch (type) {
			case 1:
				return DISCLOSED_VENDOR;
			case 2:
				return ALLOWED_VENDOR;
			case 3:
				return PUBLISHER_TC;
			default:
				return DEFAULT;
		}
	}
}
