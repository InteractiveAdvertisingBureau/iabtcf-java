package com.iabtcf;

import java.util.Set;

/**
 * @author evanwht1
 */
public interface OutOfBandConsent {

	Set<Integer> getDisclosedVendors();

	Set<Integer> getAllowedVendors();
}
