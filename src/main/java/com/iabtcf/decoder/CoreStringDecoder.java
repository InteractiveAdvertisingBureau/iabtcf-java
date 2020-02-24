package com.iabtcf.decoder;

import com.iabtcf.PublisherRestriction;
import com.iabtcf.RestrictionType;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static com.iabtcf.decoder.Field.CoreString.CMP_ID;
import static com.iabtcf.decoder.Field.CoreString.CMP_VERSION;
import static com.iabtcf.decoder.Field.CoreString.CONSENT_LANGUAGE;
import static com.iabtcf.decoder.Field.CoreString.CONSENT_SCREEN;
import static com.iabtcf.decoder.Field.CoreString.CREATED;
import static com.iabtcf.decoder.Field.CoreString.IS_SERVICE_SPECIFIC;
import static com.iabtcf.decoder.Field.CoreString.LAST_UPDATED;
import static com.iabtcf.decoder.Field.CoreString.PUBLISHER_CC;
import static com.iabtcf.decoder.Field.CoreString.PURPOSES_CONSENT;
import static com.iabtcf.decoder.Field.CoreString.PURPOSE_LI_TRANSPARENCY;
import static com.iabtcf.decoder.Field.CoreString.PURPOSE_ONE_TREATMENT;
import static com.iabtcf.decoder.Field.CoreString.SPECIAL_FEATURE_OPT_INS;
import static com.iabtcf.decoder.Field.CoreString.TCF_POLICY_VERSION;
import static com.iabtcf.decoder.Field.CoreString.USE_NON_STANDARD_STACKS;
import static com.iabtcf.decoder.Field.CoreString.VENDOR_LIST_VERSION;
import static com.iabtcf.decoder.Field.PublisherRestrictions.NUM_PUB_RESTRICTIONS;
import static com.iabtcf.decoder.Field.PublisherRestrictions.PURPOSE_ID;
import static com.iabtcf.decoder.Field.PublisherRestrictions.RESTRICTION_TYPE;

/**
 * @author evanwht1
 */
class CoreStringDecoder {

	/**
	 * Builds a {@link CoreStringImpl} from the given bit vector.
	 *
	 * @param bitVector BitVector created from the first segment of the web safe 64 encoded TC string
	 * @return a CoreString with all fields parsed
	 */
	static CoreStringImpl decode(final int version, BitVector bitVector) {
		// Read fields in order!
		return CoreStringImpl.newBuilder()
		                     .version(version)
		                     .consentRecordCreated(bitVector.readInstantFromDeciSecond(CREATED))
		                     .consentRecordLastUpdated(bitVector.readInstantFromDeciSecond(LAST_UPDATED))
		                     .consentManagerProviderId(bitVector.readInt(CMP_ID))
		                     .consentManagerProviderVersion(bitVector.readInt(CMP_VERSION))
		                     .consentScreen(bitVector.readInt(CONSENT_SCREEN))
		                     .consentLanguage(bitVector.readString(CONSENT_LANGUAGE))
		                     .vendorListVersion(bitVector.readInt(VENDOR_LIST_VERSION))
		                     .policyVersion(bitVector.readInt(TCF_POLICY_VERSION))
		                     .isServiceSpecific(bitVector.readBit(IS_SERVICE_SPECIFIC))
		                     .useNonStandardStacks(bitVector.readBit(USE_NON_STANDARD_STACKS))
		                     .specialFeaturesOptInts(bitVector.readBitSet(SPECIAL_FEATURE_OPT_INS.getLength()))
		                     .purposesConsent(bitVector.readBitSet(PURPOSES_CONSENT.getLength()))
		                     .purposesLITransparency(bitVector.readBitSet(PURPOSE_LI_TRANSPARENCY.getLength()))
		                     .isPurposeOneTreatment(bitVector.readBit(PURPOSE_ONE_TREATMENT))
		                     .publisherCountryCode(bitVector.readString(PUBLISHER_CC))
		                     .vendorConsents(VendorsDecoder.decode(bitVector))
		                     .vendorLegitimateInterests(VendorsDecoder.decode(bitVector))
		                     .publisherRestrictions(decodePublisherRestrictions(bitVector))
		                     .build();
	}

	/**
	 * Builds a map of purpose id to the restrction type and a list of vendors it applies to.
	 *
	 * @param bitVector bit vector to read from
	 * @return map of purpose to publisher restriction and vendors it applies to
	 */
	static Map<Integer, PublisherRestriction> decodePublisherRestrictions(BitVector bitVector) {
		final Map<Integer, PublisherRestriction> restrictions = new HashMap<>();
		int numberOfPublisherRestrictions = bitVector.readInt(NUM_PUB_RESTRICTIONS);

		for (int i = 0; i < numberOfPublisherRestrictions; i++) {
			int purposeId = bitVector.readInt(PURPOSE_ID);
			int restrictionTypeId = bitVector.readInt(RESTRICTION_TYPE);
			RestrictionType restrictionType = RestrictionType.fromId(restrictionTypeId);

			BitSet vendorIds = VendorsDecoder.vendorIdsFromRange(bitVector, numberOfPublisherRestrictions);

			restrictions.put(purposeId, new PublisherRestriction(restrictionType, vendorIds));
		}
		return restrictions;
	}
}
