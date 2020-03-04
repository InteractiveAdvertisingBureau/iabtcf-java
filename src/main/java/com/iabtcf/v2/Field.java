package com.iabtcf.v2;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public interface Field {

	int CHAR_LENGTH = 6;

	int getOffset();

	int getLength();

	enum CoreString implements Field {

		VERSION                     (0, 6),
		CREATED                     (6, 36),
		LAST_UPDATED                (42, 36),
		CMP_ID                      (78, 12),
		CMP_VERSION                 (90, 12),
		CONSENT_SCREEN              (102, 6),
		CONSENT_LANGUAGE            (108, 12),
		VENDOR_LIST_VERSION         (120, 12),
		TCF_POLICY_VERSION          (132, 6),
		IS_SERVICE_SPECIFIC         (138, 1),
		USE_NON_STANDARD_STACKS     (139, 1),
		SPECIAL_FEATURE_OPT_INS     (140, 12),
		PURPOSES_CONSENT            (152, 24),
		PURPOSE_LI_TRANSPARENCY     (176, 24),
		PURPOSE_ONE_TREATMENT       (200, 1),
		PUBLISHER_CC                (201, 12)
		// vendor consents
		// vendor legitimate interests
		// publisher restriction
		;

		private final int offset;
		private final int length;

		CoreString(final int offset, final int length) {
			this.offset = offset;
			this.length = length;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum PublisherRestrictions implements Field {

		// first offset is undefined as these fields come after 2 variable length fields
		// other offsets are for reference and are relative to the NUM_PUB_RESTRICTIONS
		NUM_PUB_RESTRICTIONS	(-1, 12),
		PURPOSE_ID              (12, 6),
		RESTRICTION_TYPE        (18, 2),
		NUM_ENTRIES             (20, 12),
		IS_A_RANGE              (32, 1),
		START_OR_ONLY_VENDOR_ID (33, 16),
		END_VENDOR_ID           (49, 16),;

		private final int offset;
		private final int length;

		PublisherRestrictions(final int offset, final int length) {
			this.offset = offset;
			this.length = length;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum OptionalSegment implements Field {
		SEGMENT_TYPE            (0, 3),
		;

		private final int offset;
		private final int length;

		OptionalSegment(final int offset, final int length) {
			this.offset = offset;
			this.length = length;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum Vendors implements Field {

		// these offsets are correct for OOB segments up until you read past the first bit in a BIT_FIELD
		// or the first range range in a range encoding
		MAX_VENDOR_ID           (OptionalSegment.SEGMENT_TYPE.length, 16),
		IS_RANGE_ENCODING       (19, 1),
		BIT_FIELD               (20, 1),
		NUM_ENTRIES             (20, 12),
		IS_A_RANGE              (32, 1),
		START_OR_ONLY_VENDOR_ID (33, 16),
		END_VENDOR_ID           (49, 16)
		;

		private final int offset;
		private final int length;

		Vendors(final int offset, final int length) {
			this.offset = offset;
			this.length = length;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public int getLength() {
			return length;
		}
	}

	enum PublisherTC implements Field {

		// Theses offsets are correct up until CUSTOM_PURPOSES_CONTENT. That and CUSTOM_PURPOSES_LI_TRANSPARENCY
		// are variable length based on the value of CUSTOM_PURPOSES
		PURPOSE_CONSENT                 (OptionalSegment.SEGMENT_TYPE.length, 24),
		PURPOSES_LI_TRANSPARENCY        (27, 24),
		NUM_CUSTOM_PURPOSES             (51, 6),
		CUSTOM_PURPOSES_CONSENT         (57, 1),
		CUSTOM_PURPOSES_LI_TRANSPARENCY (-1, 1);

		private final int offset;
		private final int length;

		PublisherTC(final int offset, final int length) {
			this.offset = offset;
			this.length = length;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public int getLength() {
			return length;
		}
	}
}
