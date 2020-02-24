package com.iabtcf.decoder;

import org.junit.Test;

import java.util.BitSet;

import static com.iabtcf.decoder.Field.Vendors.SEGMENT_TYPE;
import static org.junit.Assert.assertTrue;

/**
 * @author ewhite 2/22/20
 */
public class VendorsDecoderTest {

	@Test
	public void testParseDisclosedVendors() {
		String base64CoreString = "IBAgAAAgAIAwgAgAAAAEAAAACA";
		final BitVector bitVector = Util.vectorFromBase64String(base64CoreString);
		bitVector.readInt(SEGMENT_TYPE);
		BitSet vendors = VendorsDecoder.decode(bitVector);
		assertTrue(vendors.get(23));
		assertTrue(vendors.get(37));
		assertTrue(vendors.get(47));
		assertTrue(vendors.get(48));
		assertTrue(vendors.get(53));
		assertTrue(vendors.get(65));
		assertTrue(vendors.get(98));
		assertTrue(vendors.get(129));
	}

	@Test
	public void testParseAllowedVendors() {
		String base64CoreString = "QAagAQAgAIAwgA";
		final BitVector bitVector = Util.vectorFromBase64String(base64CoreString);
		bitVector.readInt(SEGMENT_TYPE);
		BitSet vendors = VendorsDecoder.decode(bitVector);
		assertTrue(vendors.get(12));
		assertTrue(vendors.get(23));
		assertTrue(vendors.get(37));
		assertTrue(vendors.get(47));
		assertTrue(vendors.get(48));
		assertTrue(vendors.get(53));
	}
}
