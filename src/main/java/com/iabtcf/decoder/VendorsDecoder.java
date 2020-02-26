package com.iabtcf.decoder;

import java.util.BitSet;

import static com.iabtcf.decoder.Field.Vendors.BIT_FIELD;
import static com.iabtcf.decoder.Field.Vendors.END_VENDOR_ID;
import static com.iabtcf.decoder.Field.Vendors.IS_A_RANGE;
import static com.iabtcf.decoder.Field.Vendors.IS_RANGE_ENCODING;
import static com.iabtcf.decoder.Field.Vendors.MAX_VENDOR_ID;
import static com.iabtcf.decoder.Field.Vendors.NUM_ENTRIES;
import static com.iabtcf.decoder.Field.Vendors.START_OR_ONLY_VENDOR_ID;

/**
 * @author SleimanJneidi
 * @author evanwht1
 */
final class VendorsDecoder {

	/**
	 * Reads the Vendors fields from a TCF segment string. This can either be the entire OOB Disclosed Vendors or
	 * OOB Allowed Vendors segments, or the Vendor Consent Section or Vendor Legitimate Interest Section parts of
	 * the Core String.
	 *
	 * WARNING: This assumes you are at the correct position in the bit vector to read it's field. {@link TCModelDecoder}
	 *          will have already read the {@link Field.Vendors#SEGMENT_TYPE} for the OOB segments and will have read up
	 *          to {@link Field.CoreString#PUBLISHER_CC}, therefor the bit vector should be at the correct position
	 *          before calling this. Unit tests should mimic this behavior accordingly.
	 *
	 * @param bitVector bit vector to read data from
	 * @return the new position that was read to
	 */
	static BitSet decode(BitVector bitVector) {
		int maxVendor = bitVector.readInt(MAX_VENDOR_ID);
		boolean isRangeEncoding = bitVector.readBit(IS_RANGE_ENCODING);

		if (!isRangeEncoding) {
			final BitSet set = new BitSet(maxVendor);
			for (int i = 0; i < maxVendor; i++) {
				boolean hasVendorConsent = bitVector.readBit(BIT_FIELD);
				if (hasVendorConsent) {
					// vendors are 1 indexed so add 1 to current index
					set.set(i + 1);
				}
			}
			return set;
		} else {
			return vendorIdsFromRange(bitVector, maxVendor);
		}
	}

	static BitSet vendorIdsFromRange(BitVector bitVector, int maxVendor) {
		final BitSet set = new BitSet(maxVendor);
		int numberOfVendorEntries = bitVector.readInt(NUM_ENTRIES);
		for (int i = 0; i < numberOfVendorEntries; i++) {
			boolean isRangeEntry = bitVector.readBit(IS_A_RANGE);
			int startOrOnlyVendorId = bitVector.readInt(START_OR_ONLY_VENDOR_ID);
			if (isRangeEntry) {
				int endVendorId = bitVector.readInt(END_VENDOR_ID);
				set.set(startOrOnlyVendorId, endVendorId + 1);
			} else {
				set.set(startOrOnlyVendorId);
			}
		}
		return set;
	}
}
