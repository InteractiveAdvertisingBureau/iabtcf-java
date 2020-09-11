package com.iabtcf.decoder;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.Instant;
import java.util.List;

import com.iabtcf.exceptions.ByteParseException;
import com.iabtcf.exceptions.TCStringDecodeException;
import com.iabtcf.exceptions.UnsupportedVersionException;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.PublisherRestriction;

public interface TCString {

    /**
     * Decodes an iabtcf compliant encoded string.
     *
     * @throws ByteParseException if version field failed to parse
     * @throws UnsupportedVersionException invalid version field
     * @throws IllegalArgumentException if consentString is not in valid Base64 scheme
     */
    static TCString decode(String consentString, DecoderOption... options)
            throws IllegalArgumentException, ByteParseException, UnsupportedVersionException {
        return TCStringDecoder.decode(consentString, options);
    }

    /**
     * Version number of the encoding format
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return the version number
     */
    int getVersion();

    /**
     * Epoch deciseconds (0.1 of a second) when this TC String was first created
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return timestamp the record was first created
     */
    Instant getCreated();

    /**
     * Epoch deciseconds (0.1 of a second) when TC String was last updated
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return timestamp record was last updated
     */
    Instant getLastUpdated();

    /**
     * Consent Management Platform ID that last updated the TC String
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return the Consent Management Platform ID
     */
    int getCmpId();

    /**
     * Consent Management Platform version of the CMP that last updated this TC String
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return version of the Consent Management Platform that updated this record
     */
    int getCmpVersion();

    /**
     *
     * CMP Screen number at which consent was given for a user with the CMP that last updated this
     * TC String.
     *
     * The number is a CMP internal designation and is CmpVersion specific. The number is used for
     * identifying on which screen a user gave consent as a record.
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return the screen number identifier
     */
    int getConsentScreen();

    /**
     * Two-letter ISO 639-1 language code in which the CMP UI was presented.
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return the language string
     */
    String getConsentLanguage();

    /**
     * Number corresponds to GVL vendorListVersion. Version of the GVL used to create this TC
     * String.
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return the version number
     */
    int getVendorListVersion();

    /**
     * The user’s consent value for each Purpose established on the legal basis of consent. The
     * Purposes are numerically identified and published in the Global Vendor List.
     *
     * An alias for PurposesAllowed
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return The integer values for each established Purpose.
     */
    IntIterable getPurposesConsent();

    /**
     * The vendor identifiers that have consent to process this users personal data. The vendor
     * identifiers are published in the GVL.
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return the vendor identifiers.
     */
    IntIterable getVendorConsent();

    /**
     * Default consent for VendorIds not covered by a RangeEntry. VendorIds covered by a RangeEntry
     * have a consent value the opposite of DefaultConsent.
     *
     * This field is not used by Transparency and Consent String v2.0 specifications and always
     * returns false.
     *
     * @since 1.0
     * @throws TCStringDecodeException
     * @return all vendors that have consent to process this users personal data
     */
    boolean getDefaultVendorConsent();

    /**
     * From the corresponding field in the GVL that was used for obtaining consent. A new policy
     * version invalidates existing strings and requires CMPs to re-establish transparency and
     * consent from users.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return version of policy used within GVL
     */
    int getTcfPolicyVersion();

    /**
     * Whether the signals encoded in this TC String were from service-specific storage versus
     * global consesu.org shared storage.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return if signals are service-specific or global
     */
    boolean isServiceSpecific();

    /**
     * Setting this to field to true means that a publisher-run CMP – that is still IAB Europe
     * registered – is using customized Stack descriptions and not the standard stack descriptions
     * defined in the Policies (Appendix A section E). A CMP that services multiple publishers sets
     * this value to false.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return true if if the CMP used non-IAB standard stacks during consent gathering; false
     *         otherwise.
     */
    boolean getUseNonStandardStacks();

    /**
     * The TCF Policies designates certain Features as "special" which means a CMP must afford the
     * user a means to opt in to their use. These "Special Features" are published and numerically
     * identified in the Global Vendor List separately from normal Features.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return the Special Features the Vendor may utilize when performing some declared Purposes
     *         processing.
     */
    IntIterable getSpecialFeatureOptIns();

    /**
     * The Purpose’s transparency requirements are met for each Purpose on the legal basis of
     * legitimate interest and the user has not exercised their "Right to Object" to that Purpose.
     *
     * By default or if the user has exercised their "Right to Object" to a Purpose, the
     * corresponding identifier for that Purpose is set to false.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return The purpose identifiers for which the legal basis of legitimate interest are
     *         established.
     */
    IntIterable getPurposesLITransparency();

    /**
     * CMPs can use the PublisherCC field to indicate the legal jurisdiction the publisher is under
     * to help vendors determine whether the vendor needs consent for Purpose 1.
     *
     * In a globally-scoped TC string, this field must always have a value of false. When a CMP
     * encounters a globally-scoped TC String with PurposeOneTreatment set to true then it is
     * considered invalid and the CMP must discard it and re-establish transparency and consent.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return true if Purpose 1 was NOT disclosed; false otherwise.
     */
    boolean getPurposeOneTreatment();

    /**
     * The country code of the country that determines legislation of reference. Commonly, this
     * corresponds to the country in which the publisher’s business entity is established.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return ISO 3166-1 alpha-2 country code
     */
    String getPublisherCC();

    /**
     * If a user exercises their "Right To Object" to a vendor’s processing based on a legitimate
     * interest.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return vendor identifiers that can process this user based on legitimate interest
     */
    IntIterable getVendorLegitimateInterest();

    /**
     * The restrictions of a vendor's data processing by a publisher within the context of the users
     * trafficking their digital property.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return the list of publisher restrictions.
     */
    List<PublisherRestriction> getPublisherRestrictions();

    /**
     * Part of the OOB segments expressing that a Vendor is using legal bases outside of the TCF to
     * process personal data.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return A list of Vendors that the publisher allows to use out-of-band legal bases.
     */
    IntIterable getAllowedVendors();

    /**
     * Part of the OOB segments expressing that a Vendor is using legal bases outside of the TCF to
     * process personal data.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return A list of Vendors that disclosed to the user.
     */
    IntIterable getDisclosedVendors();

    /**
     * Part of the Publisher Transparency and Consent segment of a TC String that publishers may use
     * to establish transparency with and receive consent from users for their own legal bases to
     * process personal data or to share with vendors if they so choose.
     *
     * The user's consent value for each Purpose established on the legal basis of consent, for the
     * publisher
     *
     * The Purposes are numerically identified and published in the Global Vendor List.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return the consent value for each Purpose
     */
    IntIterable getPubPurposesConsent();

    /**
     * Part of the Publisher Transparency and Consent segment of a TC String that publishers may use
     * to establish transparency with and receive consent from users for their own legal bases to
     * process personal data or to share with vendors if they so choose.
     *
     * The Purpose’s transparency requirements are met for each Purpose established on the legal
     * basis of legitimate interest and the user has not exercised their "Right to Object" to that
     * Purpose.
     *
     * By default or if the user has exercised their "Right to Object" to a Purpose, the
     * corresponding identifier for that Purpose is set to false.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return The consent value for each Purpose where legitimate interest was established.
     */
    IntIterable getPubPurposesLITransparency();

    /**
     * Part of the Publisher Transparency and Consent segment of a TC String that publishers may use
     * to establish transparency with and receive consent from users for their own legal bases to
     * process personal data or to share with vendors if they so choose.
     *
     * Custom purposes will be defined by the publisher and displayed to a user in a CMP user
     * interface.
     *
     * @since 2.0
     * @throws TCStringDecodeException
     * @return The established custom purpose consent values
     */
    IntIterable getCustomPurposesConsent();

    /**
     * Part of the Publisher Transparency and Consent segment of a TC String that publishers may use
     * to establish transparency with and receive consent from users for their own legal bases to
     * process personal data or to share with vendors if they so choose.
     *
     * @throws TCStringDecodeException
     * @return The custom purpose consent values with established legitimate interest disclosure.
     */
    IntIterable getCustomPurposesLITransparency();
}
