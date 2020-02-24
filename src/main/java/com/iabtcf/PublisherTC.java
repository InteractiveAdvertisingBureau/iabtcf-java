package com.iabtcf;

import java.util.stream.IntStream;

/**
 * @author evanwht1
 */
public interface PublisherTC {

	boolean isPurposeConsented(final int purpose);

	IntStream getAllConsentedPurposes();

	boolean isPurposeLegitimateInterest(final int purpose);

	IntStream getAllLegitimateInterestPurposes();

	boolean isCustomPurposeConsented(final int customPurpose);

	IntStream getAllConsentedCustomPurposes();

	 boolean isCustomPurposeLegitimateInterest(final int customPurpose);

	 IntStream getAllLegitimateInterestCustomPurposes();
}
