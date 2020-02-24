package com.iabtcf;

import java.util.stream.IntStream;

/**
 * The Publisher TC segment in the TC string represents publisher purposes transparency & consent signals which is
 * different than the other TC String segments; they are used to collect consumer purposes transparency & consent for
 * vendors. This segment supports the standard list of purposes defined by the TCF as well as Custom Purposes defined
 * by the publisher if they so choose.
 *
 * @author evanwht1
 */
public interface PublisherTC {

	/**
	 * The user's consent value for each Purpose established on the legal basis of consent, for the publisher. The
	 * Purposes are numerically identified and published in the Global Vendor List.
	 *
	 * @param purpose id of the purpose
	 * @return if the user consented to this purpose
	 */
	boolean isPurposeConsented(final int purpose);

	/**
	 * The user's consent value for each Purpose established on the legal basis of consent, for the publisher. The
	 * Purposes are numerically identified and published in the Global Vendor List.
	 *
	 * @return all purposes the user consented to
	 */
	IntStream getAllConsentedPurposes();

	/**
	 * The Purpose’s transparency requirements are met for each Purpose established on the legal basis of legitimate
	 * interest and the user has not exercised their “Right to Object” to that Purpose.
	 *
	 * By default or if the user has exercised their “Right to Object to a Purpose, the corresponding bit for that
	 * purpose is set to 0.
	 *
	 * @param purpose id of the purpose
	 * @return if the purpose's transparency requirement has been met
	 */
	boolean isPurposeLegitimateInterest(final int purpose);

	/**
	 * The Purpose’s transparency requirements are met for each Purpose established on the legal basis of legitimate
	 * interest and the user has not exercised their “Right to Object” to that Purpose.
	 *
	 * By default or if the user has exercised their “Right to Object to a Purpose, the corresponding bit for that
	 * purpose is set to 0.
	 *
	 * @return all purpose's that have met their transparency requirement
	 */
	IntStream getAllLegitimateInterestPurposes();

	/**
	 * @param customPurpose id of the custom purpose
	 * @return if the user consented to this custom purpose
	 */
	boolean isCustomPurposeConsented(final int customPurpose);

	/**
	 * @return all custom purposes the user consented to
	 */
	IntStream getAllConsentedCustomPurposes();

	/**
	 * @param customPurpose id of the custom purpose
	 * @return if the custom purpose's transparency requirement has been met
	 */
	boolean isCustomPurposeLegitimateInterest(final int customPurpose);

	/**
	 * @return all custom purpose's that have met their transparency requirement
	 */
	IntStream getAllLegitimateInterestCustomPurposes();
}
