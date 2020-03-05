package com.iabtcf.v2;

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
	 * @param vendor id of the vendor
	 * @return is the publisher permits the vendor to use OOB legal bases
	 */
	boolean isVendorAllowed(final int vendor);
}
