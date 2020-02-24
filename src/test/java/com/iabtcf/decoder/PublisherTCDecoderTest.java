package com.iabtcf.decoder;

import com.iabtcf.PublisherTC;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author ewhite 2/22/20
 */
public class PublisherTCDecoderTest {

	@Test
	public void testPublisherPurposes() {
		String publisherPurposes =
				"011"                           // segment type
				+ "100000000000000000000000"    // PubPurposesConsent
				+ "000000000000000000000001"    // PubPurposesLITransparency
				+ "000010"                      // number of custom purposes
				+ "01"                          // CustomPurposesConsent
				+ "11";                          // CustomPurposesLITransparency
		final BitVector publisherTCVector = Util.vectorFromBitString(publisherPurposes);

		assertEquals(SegmentType.PUBLISHER_TC.getValue(), publisherTCVector.readInt(Field.PublisherTC.SEGMENT_TYPE));
		final PublisherTC publisherTC = PublisherTCDecoder.decode(publisherTCVector);

		assertTrue(publisherTC.isPurposeConsented(1));
		assertTrue(publisherTC.isPurposeLegitimateInterest(24));
		assertTrue(publisherTC.isCustomPurposeConsented(2));
		assertTrue(publisherTC.isCustomPurposeLegitimateInterest(1));
		assertTrue(publisherTC.isCustomPurposeLegitimateInterest(2));
	}
}
