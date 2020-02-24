package com.iabtcf;

import java.util.stream.IntStream;

/**
 * @author evanwht1
 */
public interface OutOfBandConsent {

	boolean isVendorDisclosed(final int vendor);

	IntStream getAllDisclosedVendors();

	boolean isVendorAllowed(final int vendor);

	IntStream getAllAllowedVendors();
}
