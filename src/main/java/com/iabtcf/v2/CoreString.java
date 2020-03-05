package com.iabtcf.v2;

import java.time.Instant;
import java.util.Map;

/**
 * All details required to communicate basic vendor transparency and consent.
 *
 * @author evanwht1
 */
public interface CoreString {

	/**
	 * @return version of the encoding format
	 */
	int getVersion();

	/**
	 * The instant the record was first created as read from an Epoch Decisecond (1/10th of a second)
	 *
	 * @return when the record was first created
	 */
	Instant getConsentRecordCreated();

	/**
	 * The instant the record was last updated as read from an Epoch Decisecond (1/10th of a second)
	 *
	 * @return When the record was last updated
	 */
	Instant getConsentRecordLastUpdated();

	/**
	 * A unique ID assigned to the Consent Management Platform
	 *
	 * @return ID of the last Consent Management Platform that updated this record
	 */
	int getCMPId();

	/**
	 * Each Consent Management Platform should update their version number as a record of which version the user gave
	 * consent and transparency was established
	 *
	 * @return version of the last Consent Management Platform that updated this record
	 */
	int getCMPVersion();

	/**
	 * The number is a CMP internal designation and is CmpVersion specific. The number is used for identifying on which
	 * screen a user gave consent as a record.
	 *
	 * @return screen number at which consent was given
	 */
	int getConsentScreen();

	/**
	 * two-letter ISO 639-1 language code in which the Consent Management Platform UI was presented
	 *
	 * @return language in which the CMP UI was presented
	 */
	String getConsentLanguage();

	/**
	 * @return version of the GVL used to create this TC String
	 */
	int getVendorListVersion();

	/**
	 * From the corresponding field in the GVL that was used for obtaining consent. A new policy version invalidates
	 * existing strings and requires CMPs to re-establish transparency and consent from users.
	 *
	 * @return version of policy used within GVL
	 */
	int getPolicyVersion();

	/**
	 * Whether the signals encoded in this TC String were from service-specific storage versus global consesu.org
	 * shared storage
	 *
	 * @return if signals are service-specific or global
	 */
	boolean isServiceSpecific();

	/**
	 * Setting this to 1 means that a publisher-run CMP – that is still IAB Europe registered – is using customized
	 * Stack descriptions and not the standard stack descriptions defined in the Policies (Appendix A section E). A CMP
	 * that services multiple publishers sets this value to 0.
	 *
	 * @return if teh CMP used non-IAB standard stacks during consent gathering
	 */
	boolean isUseNonStandardStacks();

	/**
	 * The TCF Policies designates certain Features as “special” which means a CMP must afford the user a means to opt
	 * in to their use. These “Special Features” are published and numerically identified in the Global Vendor List
	 * separately from normal Features.
	 *
	 * @param specialFeature the special feature id
	 * @return if the user opted in to that special feature
	 */
	boolean isSpecialFeatureOptedIn(final int specialFeature);

	/**
	 * The user’s consent value for each Purpose established on the legal basis of consent.
	 * <br>
	 * The Purposes are numerically identified and published in the Global Vendor List. From left to right, Purpose 1
	 * maps to the 0th bit, purpose 24 maps to the bit at index 23. Special Purposes are a different ID space and not
	 * included in this field.
	 *
	 * @param purpose id of the purpose
	 * @return if the user consented to that purpose
	 */
	boolean isPurposeConsented(final int purpose);

	/**
	 * The Purpose’s transparency requirements are met for each Purpose on the legal basis of legitimate interest and
	 * the user has not exercised their “Right to Object” to that Purpose.
	 * <br>
	 * By default or if the user has exercised their “Right to Object” to a Purpose, the corresponding bit for that
	 * Purpose is set to 0. From left to right, Purpose 1 maps to the 0th bit, purpose 24 maps to the bit at index 23.
	 * Special Purposes are a different ID space and not included in this field.
	 *
	 * @param purpose id of the purpose
	 * @return if the purpose's transparency requirement has been met
	 */
	boolean isPurposeLegitimateInterest(final int purpose);

	/**
	 * CMPs can use the PublisherCC field to indicate the legal jurisdiction the publisher is under to help vendors
	 * determine whether the vendor needs consent for Purpose 1.
	 * <br>
	 * In a globally-scoped TC string, this field must always have a value of 0. When a CMP encounters a
	 * globally-scoped TC String with PurposeOneTreatment=1 then it is considered invalid and the CMP must discard it
	 * and re-establish transparency and consent.
	 *
	 * @return if purpose 1 was NOT disclosed at all
	 */
	boolean isPurposeOneTreatment();

	/**
	 * The country code of the country that determines legislation of reference. Commonly, this corresponds to the
	 * country in which the publisher’s business entity is established.
	 *
	 * @return ISO 3166-1 alpha-2 country code
	 */
	String getPublisherCountryCode();

	/**
	 * @param vendor id of the vendor
	 * @return if the user has consented to the vendor processing their personal data
	 */
	boolean isVendorConsented(final int vendor);

	/**
	 * If a user exercises their “Right To Object” to a vendor’s processing based on a legitimate interest.
	 *
	 * @param vendor id of the vendor
	 * @return if the vendor can process this user based on legitimate interest
	 */
	boolean isVendorLegitimateInterest(final int vendor);

	/**
	 * Vendors must always respect a 0 (Not Allowed) regardless of whether or not they have not declared that Purpose
	 * to be “flexible”. Values 1 and 2 are in accordance with a vendors declared flexibility. Eg. if a vendor has
	 * Purpose 2 declared as Legitimate Interest but also declares that Purpose as flexible and this field is set to
	 * 1, they must then check for the “consent” signal in the VendorConsents section to make a determination on
	 * whether they have the legal basis for processing user personal data under that Purpose.
	 *
	 * If a vendor has not declared a Purpose flexible and this value is 1 or 2 they may ignore the signal.
	 *
	 * Note: Purpose 1 is always required to be registered as a consent purpose and can not be flexible per Policies.
	 *
	 * @param vendor id of the vendor
	 * @param purpose The vendors declared purpose id
	 * @return what overriding restriction type applies (default to {@link RestrictionType#UNDEFINED})
	 */
	RestrictionType getVendorRestrictionType(final int vendor, final int purpose);

	/**
	 * Vendors must always respect a 0 (Not Allowed) regardless of whether or not they have not declared that Purpose
	 * to be “flexible”. Values 1 and 2 are in accordance with a vendors declared flexibility. Eg. if a vendor has
	 * Purpose 2 declared as Legitimate Interest but also declares that Purpose as flexible and this field is set to
	 * 1, they must then check for the “consent” signal in the VendorConsents section to make a determination on
	 * whether they have the legal basis for processing user personal data under that Purpose.
	 *
	 * If a vendor has not declared a Purpose flexible and this value is 1 or 2 they may ignore the signal.
	 *
	 * Note: Purpose 1 is always required to be registered as a consent purpose and can not be flexible per Policies.
	 *
	 * @param vendor id of the vendor
	 * @return map of purpose id to restriction type for the vendor (default to {@link RestrictionType#UNDEFINED})
	 */
	Map<Integer, RestrictionType> getVendorRestrictionType(final int vendor);
}
