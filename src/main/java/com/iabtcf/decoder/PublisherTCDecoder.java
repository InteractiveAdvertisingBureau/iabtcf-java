package com.iabtcf.decoder;

import java.util.BitSet;

import static com.iabtcf.decoder.Field.PublisherTC.NUM_CUSTOM_PURPOSES;
import static com.iabtcf.decoder.Field.PublisherTC.PUB_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.decoder.Field.PublisherTC.PUB_PURPOSE_CONSENT;

/**
 * @author evanwht1
 */
class PublisherTCDecoder {

	static PublisherTCImpl decode(BitVector bitVector) {
		final BitSet consents = bitVector.readBitSet(PUB_PURPOSE_CONSENT.getLength());
		final BitSet liTransparency = bitVector.readBitSet(PUB_PURPOSES_LI_TRANSPARENCY.getLength());

		final int numberOfCustomPurposes = bitVector.readInt(NUM_CUSTOM_PURPOSES);
		final BitSet customPurposes = bitVector.readBitSet(numberOfCustomPurposes);
		final BitSet customLiTransparency = bitVector.readBitSet(numberOfCustomPurposes);

		return new PublisherTCImpl(consents, liTransparency, customPurposes, customLiTransparency);
	}
}
