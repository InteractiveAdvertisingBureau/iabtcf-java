package com.iabtcf.decoder;

import java.util.BitSet;

import static com.iabtcf.decoder.Field.PublisherTC.NUM_CUSTOM_PURPOSES;
import static com.iabtcf.decoder.Field.PublisherTC.PUB_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.decoder.Field.PublisherTC.PUB_PURPOSE_CONSENT;

/**
 * Decoding Util to read the Publisher Transparency and Consent fields from a TC String
 *
 * @author evanwht1
 */
class PublisherTCDecoder {

	/**
	 * Reads the Publisher Transparency and Consent fields from a BitVector.
	 *
	 * WARNING: This assumes you are at the correct position in the bit vector to read it's field. {@link TCModelDecoder}
	 *          will have already read the {@link Field.PublisherTC#SEGMENT_TYPE} and therefor the bit vector should be at the
	 *          4th bit before calling this. Unit tests should mimic this behavior accordingly.
	 *
	 * @param bitVector bitvector to read from
	 * @return PublisherTC fields contained in the bit vector
	 */
	static PublisherTCImpl decode(BitVector bitVector) {
		final BitSet consents = bitVector.readBitSet(PUB_PURPOSE_CONSENT.getLength());
		final BitSet liTransparency = bitVector.readBitSet(PUB_PURPOSES_LI_TRANSPARENCY.getLength());

		final int numberOfCustomPurposes = bitVector.readInt(NUM_CUSTOM_PURPOSES);
		final BitSet customPurposes = bitVector.readBitSet(numberOfCustomPurposes);
		final BitSet customLiTransparency = bitVector.readBitSet(numberOfCustomPurposes);

		return new PublisherTCImpl(consents, liTransparency, customPurposes, customLiTransparency);
	}
}
