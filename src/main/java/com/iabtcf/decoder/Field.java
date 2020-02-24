package com.iabtcf.decoder;

/**
 * TC string fields defined in the <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#tc-string-format">IAB TC String and GVL guide</a>.
 *
 * These only define the lengths of each field as there are fields that occur multiple times based on the value of
 * other fields. These are intended to be used by reading IN ORDER from a BitVector that represents a web safe 64
 * decoded string. Unit tests will catch most mistakes made but please be sure to check that the correct fields are
 * read in order in the decoders.
 *
 * @author evanwht1
 */
interface Field {

	int getLength();

	enum CoreString implements Field {

		VERSION                     (6),
		CREATED                     (36),
		LAST_UPDATED                (36),
		CMP_ID                      (12),
		CMP_VERSION                 (12),
		CONSENT_SCREEN              (6),
		CONSENT_LANGUAGE            (12),
		VENDOR_LIST_VERSION         (12),
		TCF_POLICY_VERSION          (6),
		IS_SERVICE_SPECIFIC         (1),
		USE_NON_STANDARD_STACKS     (1),
		SPECIAL_FEATURE_OPT_INS     (12),
		PURPOSES_CONSENT            (24),
		PURPOSE_LI_TRANSPARENCY     (24),
		PURPOSE_ONE_TREATMENT       (1),
		PUBLISHER_CC                (12),
		// Vendors Field (minus SEGMENT_TYPE)
		// Vendors Field (minus SEGMENT_TYPE)
		// Publisher Restrictions
		;

		private final int length;

		CoreString(final int length) {
			this.length = length;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum PublisherRestrictions implements Field {

		NUM_PUB_RESTRICTIONS     (12),
		PURPOSE_ID               (6),
		RESTRICTION_TYPE         (2),
		NUM_ENTRIES              (12),
		IS_A_RANGE               (1),
		START_OR_ONLY_VENDOR_ID  (16),
		END_VENDOR_ID            (16),
		;

		private final int length;

		PublisherRestrictions(final int length) {
			this.length = length;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum Vendors implements Field {

		SEGMENT_TYPE            (3),
		MAX_VENDOR_ID           (16),
		IS_RANGE_ENCODING       (1),
		BIT_FIELD               (1),
		NUM_ENTRIES             (12),
		IS_A_RANGE              (1),
		START_OR_ONLY_VENDOR_ID (16),
		END_VENDOR_ID           (16)
		;

		private final int length;

		Vendors(final int length) {
			this.length = length;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum PublisherTC implements Field {

		SEGMENT_TYPE                    (3),
		PUB_PURPOSE_CONSENT             (24),
		PUB_PURPOSES_LI_TRANSPARENCY    (24),
		NUM_CUSTOM_PURPOSES             (6),
		CUSTOM_PURPOSES_CONSENT         (1),
		CUSTOM_PURPOSES_LI_TRANSPARENCY (1);

		private final int length;

		PublisherTC(final int length) {
			this.length = length;
		}

		@Override
		public int getLength() {
			return length;
		}
	}
}
