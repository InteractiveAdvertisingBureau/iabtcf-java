package com.iabtcf;

/**
 * @author evanwht1
 */
public enum SpecialPurpose {

	ENSURE_SECURITY_PREVENT_FRAUD_AND_DEBUG(1),
	TECHNICALLY_DELIVER_ADS_OR_CONTENT(2),
	;

	private final int id;

	SpecialPurpose(final int id) {
		this.id = id;
	}
}
