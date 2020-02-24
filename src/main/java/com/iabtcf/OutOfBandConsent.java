package com.iabtcf;

import java.util.stream.IntStream;

/**
 * Legal Basis for processing a user's personal data achieved outside of the TCF.
 *
 * @author evanwht1
 */
public interface OutOfBandConsent {

	/**
	 * @param vendor id of the vendor
	 * @return if the vendor was disclosed on the CMP UI
	 */
	boolean isVendorDisclosed(final int vendor);

	/**
	 * @return all vendors disclosed on the CMP UI
	 */
	IntStream getAllDisclosedVendors();

	/**
	 * @param vendor id of the vendor
	 * @return is the publisher permits the vendor to use OOB legal bases
	 */
	boolean isVendorAllowed(final int vendor);

	/**
	 * @return all vendors that the publisher permits to use OOB legal bases
	 */
	IntStream getAllAllowedVendors();
}
