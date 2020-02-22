package com.iabtcf.decoder;

import org.junit.Test;

import java.util.Set;

import static com.iabtcf.decoder.Field.Vendors.SEGMENT_TYPE;
import static org.junit.Assert.assertEquals;

/**
 * @author ewhite 2/22/20
 */
public class VendorsDecoderTest {

	@Test
	public void testParseDisclosedVendors() {
		String base64CoreString = "IBAgAAAgAIAwgAgAAAAEAAAACA";
		final BitVector bitVector = Util.vectorFromBase64String(base64CoreString);
		bitVector.readInt(SEGMENT_TYPE);
		Set<Integer> vendors = VendorsDecoder.decode(bitVector);
		assertEquals(Util.setOf(23, 37, 47, 48, 53, 65, 98, 129), vendors);
	}

	@Test
	public void testParseAllowedVendors() {
		String base64CoreString = "QAagAQAgAIAwgA";
		final BitVector bitVector = Util.vectorFromBase64String(base64CoreString);
		bitVector.readInt(SEGMENT_TYPE);
		Set<Integer> vendors = VendorsDecoder.decode(bitVector);
		assertEquals(Util.setOf(12, 23, 37, 47, 48, 53), vendors);
	}
}
