package com.iabtcf.decoder;

import com.iabtcf.CoreString;
import org.junit.Test;

import java.time.Instant;

import static com.iabtcf.decoder.Field.CoreString.VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author ewhite 2/22/20
 */
public class CoreStringDecoderTest {

	 /**
	  * the string was created here https://www.iabtcf.com/#/encode
	  */
	@Test
	public void testParse() {
		String base64CoreString = "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA";
		final BitVector bitVector = Util.vectorFromBase64String(base64CoreString);
		CoreString coreString = CoreStringDecoder.decode(bitVector.readInt(VERSION), bitVector);

		assertEquals(2, coreString.getVersion());
		assertEquals(Instant.parse("2020-01-26T17:01:00Z"), coreString.getConsentRecordCreated());
		assertEquals(Instant.parse("2021-02-02T17:01:00Z"), coreString.getConsentRecordLastUpdated());
		assertEquals(675, coreString.getConsentManagerProviderId());
		assertEquals(2, coreString.getConsentManagerProviderVersion());
		assertEquals(1, coreString.getConsentScreen());
		assertEquals(15, coreString.getVendorListVersion());
		assertEquals(2, coreString.getPolicyVersion());
		assertEquals("EN", coreString.getConsentLanguage());
		assertEquals("AA", coreString.getPublisherCountryCode());
		assertFalse(coreString.isServiceSpecific());
		assertTrue(coreString.isPurposeOneTreatment());
		assertFalse(coreString.isUseNonStandardStacks());

		assertEquals(Util.setOf(1), coreString.getSpecialFeaturesOptInts());
		assertEquals(Util.setOf(2, 10), coreString.getPurposesConsent());
		assertEquals(Util.setOf(2, 9), coreString.getPurposesLITransparency());
	}
}
