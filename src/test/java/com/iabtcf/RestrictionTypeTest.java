package com.iabtcf;

import com.iabtcf.RestrictionType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestrictionTypeTest {

	@Test
	public void testDecode() {
		assertEquals(RestrictionType.NOT_ALLOWED, RestrictionType.fromId(RestrictionType.NOT_ALLOWED.getValue()));
		assertEquals(RestrictionType.REQUIRE_CONSENT, RestrictionType.fromId(RestrictionType.REQUIRE_CONSENT.getValue()));
		assertEquals(RestrictionType.REQUIRE_LEGITIMATE_INTEREST, RestrictionType.fromId(RestrictionType.REQUIRE_LEGITIMATE_INTEREST.getValue()));
		assertEquals(RestrictionType.UNDEFINED, RestrictionType.fromId(RestrictionType.UNDEFINED.getValue()));
	}

}