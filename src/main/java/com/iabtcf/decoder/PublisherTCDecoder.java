package com.iabtcf.decoder;

import java.util.Set;

import static com.iabtcf.decoder.Field.PublisherTC.NUM_CUSTOM_PURPOSES;
import static com.iabtcf.decoder.Field.PublisherTC.PUB_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.decoder.Field.PublisherTC.PUB_PURPOSE_CONSENT;

/**
 * @author evanwht1
 */
class PublisherTCDecoder {

	static PublisherTCImpl decode(BitVector bitVector) {
		final Set<Integer> consents = bitVector.readSet(PUB_PURPOSE_CONSENT.getLength());
		final Set<Integer> liTransparency = bitVector.readSet(PUB_PURPOSES_LI_TRANSPARENCY.getLength());

		final int numberOfCustomPurposes = bitVector.readInt(NUM_CUSTOM_PURPOSES);
		final Set<Integer> customPurposes = bitVector.readSet(numberOfCustomPurposes);
		final Set<Integer> customLiTransparency = bitVector.readSet(numberOfCustomPurposes);

		return new PublisherTCImpl(consents, liTransparency, customPurposes, customLiTransparency);
	}
}
