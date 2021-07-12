package com.iabtcf.encoder;

/*-
 * #%L
 * IAB TCF Java Encoder Library
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

import java.util.BitSet;
import java.util.Iterator;

import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterator;

/**
 * Encodes a bit / range field, typically used for encoding vendor lists.
 */
class VendorFieldEncoder {
    private final BitSet vendors;
    private int maxVendorId;
    private boolean defaultConsent;
    private boolean emitRangeEncoding;
    private boolean emitMaxVendorId;
    private boolean emitIsRangeEncoding;

    public VendorFieldEncoder() {
        this(new BitSet(), 0, false, false, true, true);
    }

    public VendorFieldEncoder(VendorFieldEncoder prototype) {
        this(prototype.vendors.length() == 0 ? new BitSet()
                : prototype.vendors.get(0,
                        prototype.vendors.length()),
                prototype.maxVendorId,
                prototype.defaultConsent,
                prototype.emitRangeEncoding,
                prototype.emitMaxVendorId,
                prototype.emitIsRangeEncoding);
    }

    private VendorFieldEncoder(BitSet vendors, int maxVendorId, boolean defaultConsent, boolean emitRangeEncoding,
            boolean emitMaxVendorId, boolean emitIsRangeEncoding) {
        this.vendors = vendors;
        this.maxVendorId = maxVendorId;
        this.defaultConsent = defaultConsent;
        this.emitRangeEncoding = emitRangeEncoding;
        this.emitMaxVendorId = emitMaxVendorId;
        this.emitIsRangeEncoding = emitIsRangeEncoding;
    }

    /**
     * Whether to force range encoding even if it consumes more bits than bit field encoding.
     */
    public VendorFieldEncoder emitRangeEncoding(boolean value) {
        this.emitRangeEncoding = value;
        return this;
    }

    /**
     * Whether to emit the maximum Vendor ID in this encoding. This should be set to false when encoding
     * PublisherRestriction segment.
     */
    public VendorFieldEncoder emitMaxVendorId(boolean value) {
        this.emitMaxVendorId = value;
        return this;
    }

    /**
     * Whether to emit the IsRangeEncoding flag. When set to false, the field is not encoded. This
     * should be set to false when encoding PublisherRestriction section.
     */
    public VendorFieldEncoder emitIsRangeEncoding(boolean value) {
        this.emitIsRangeEncoding = value;
        return this;
    }

    /**
     * For V1, default consent for VendorIds not covered by a RangeEntry. VendorIds covered by a
     * RangeEntry have a consent value the opposite of DefaultConsent. Defaults to false.
     *
     */
    public VendorFieldEncoder defaultConsent(boolean value) {
        this.defaultConsent = value;

        return this;
    }

    public VendorFieldEncoder add(int vendorId) {
        if (vendorId <= 0) {
            throw new IndexOutOfBoundsException("vendorId < 1: " + vendorId);
        }
        vendors.set(vendorId - 1);

        return this;
    }

    public VendorFieldEncoder add(Iterable<Integer> vendorIds) {
        for (Iterator<Integer> i = vendorIds.iterator(); i.hasNext();) {
            add(i.next());
        }

        return this;
    }

    public VendorFieldEncoder add(int... vendorIds) {
        for (int i = 0; i < vendorIds.length; i++) {
            add(vendorIds[i]);
        }

        return this;
    }

    public VendorFieldEncoder add(IntIterable ii) {
        for (IntIterator i = ii.intIterator(); i.hasNext();) {
            add(i.nextInt());
        }

        return this;
    }

    /**
     * Emit the specified max vendor id. By default, the maximum vendorId that was added is emitted.
     *
     * @VisibleForTesting
     */
    VendorFieldEncoder setMaxVendorId(int maxVendorId) {
        this.maxVendorId = maxVendorId;

        return this;
    }

    public BitWriter buildV1() {
        return build(true);
    }

    /**
     * Returns a BitWriter that represents the encoded vendor ids. The BitWriter may either encode a
     * bit field or a range encoding; depending on which is smaller.
     */
    public BitWriter build() {
        return build(false);
    }

    private BitWriter build(boolean emitDefaultConsent) {
        BitWriter bv = new BitWriter();

        if (vendors.length() == 0) {
            bv.writeV(0, FieldDefs.CORE_VENDOR_MAX_VENDOR_ID);
            bv.write(false, FieldDefs.CORE_VENDOR_IS_RANGE_ENCODING);
            return bv;
        }

        maxVendorId = Math.max(vendors.length(), maxVendorId);

        // create the range bit section
        BitWriter rangeBits = new BitWriter();
        int idxSet = vendors.get(0) ? 0 : vendors.nextSetBit(0);
        int idxClr;
        int numEntries = 0;
        do {
            idxClr = vendors.nextClearBit(idxSet);
            int length = idxClr - idxSet;

            if (length == 1) {
                rangeBits.write(false, FieldDefs.CORE_VENDOR_IS_RANGE_ENCODING);
                rangeBits.writeV(idxSet + 1, FieldDefs.START_OR_ONLY_VENDOR_ID);
            } else {
                rangeBits.write(true, FieldDefs.CORE_VENDOR_IS_RANGE_ENCODING);
                rangeBits.writeV(idxSet + 1, FieldDefs.START_OR_ONLY_VENDOR_ID);
                rangeBits.writeV(idxClr, FieldDefs.END_VENDOR_ID);
            }
            numEntries++;
        } while ((idxSet = vendors.nextSetBit(idxClr)) > 0
                && (rangeBits.length() < vendors.length() || emitRangeEncoding));

        // emit max vendor id
        if (emitMaxVendorId) {
            bv.writeV(maxVendorId, FieldDefs.CORE_VENDOR_MAX_VENDOR_ID);
        }

        if (rangeBits.length() < vendors.length() || emitRangeEncoding) {
            // emit range bits

            // don't emit IS_A_RANGE when we forced a range encoding
            if (emitIsRangeEncoding) {
                bv.write(true, FieldDefs.IS_A_RANGE);
            }
            if (emitDefaultConsent) {
                bv.write(defaultConsent, FieldDefs.V1_VENDOR_DEFAULT_CONSENT);
            }
            bv.writeV(numEntries, FieldDefs.NUM_ENTRIES);
            bv.write(rangeBits);
        } else {
            // emit bit field
            bv.write(false, FieldDefs.IS_A_RANGE);

            int rem = vendors.length() % Long.SIZE;
            if (rem == 0) {
                rem = Long.SIZE;
            }

            long[] bits = vendors.toLongArray();
            for (int i = 0; i < bits.length - 1; i++) {
                bv.write(Long.reverse(bits[i]), Long.SIZE);
            }
            bv.write(Long.reverse(bits[bits.length - 1]) >>> (Long.SIZE - rem), rem);
            bv.enforcePrecision(maxVendorId - vendors.length());
        }

        return bv;
    }
}
