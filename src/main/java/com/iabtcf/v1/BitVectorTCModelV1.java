package com.iabtcf.v1;

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

import static com.iabtcf.utils.ByteBitVectorUtils.deciSeconds;
import static com.iabtcf.utils.ByteBitVectorUtils.readStr2;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iabtcf.ByteBitVector;
import com.iabtcf.FieldDefs;

public class BitVectorTCModelV1 implements TCModelV1 {

    private final ByteBitVector bitVector;

    private BitVectorTCModelV1(ByteBitVector bitVector) {
        this.bitVector = bitVector;
    }

    public static BitVectorTCModelV1 fromBitVector(ByteBitVector bitVector) {
        return new BitVectorTCModelV1(bitVector);
    }

    @Override
    public int version() {
        return bitVector.readBits6(FieldDefs.V1_VERSION);
    }

    @Override
    public Instant consentRecordCreated() {
        return deciSeconds(bitVector, FieldDefs.V1_CREATED);
    }

    @Override
    public Instant consentRecordLastUpdated() {
        return deciSeconds(bitVector, FieldDefs.V1_LAST_UPDATED);
    }

    @Override
    public int cmpId() {
        return bitVector.readBits12(FieldDefs.V1_CMP_ID);
    }

    @Override
    public int cmpVersion() {
        return bitVector.readBits12(FieldDefs.V1_CMP_VERSION);
    }

    @Override
    public int consentScreen() {
        return bitVector.readBits6(FieldDefs.V1_CONSENT_SCREEN);
    }

    @Override
    public String consentLanguage() {
        return readStr2(bitVector, FieldDefs.V1_CONSENT_LANGUAGE);
    }

    @Override
    public int vendorListVersion() {
        return bitVector.readBits12(FieldDefs.V1_VENDOR_LIST_VERSION);
    }

    @Override
    public Set<Integer> allowedPurposeIds() {
        final Set<Integer> allowedPurposes = new HashSet<>();
        final int purposesLength = FieldDefs.V1_PURPOSES_ALLOW.getLength(bitVector);
        final int purposesOffset = FieldDefs.V1_PURPOSES_ALLOW.getOffset(bitVector);

        for (int i = 0; i < purposesLength; i++) {
            if (bitVector.readBits1(purposesOffset + i)) {
                allowedPurposes.add(i + 1);
            }
        }
        return allowedPurposes;
    }

    @Override
    public Set<Purpose> allowedPurposes() {
        return allowedPurposeIds().stream().map(Purpose::fromId).collect(Collectors.toSet());
    }

    @Override
    public int maxVendorId() {
        return bitVector.readBits16(FieldDefs.V1_VENDOR_MAX_VENDOR_ID);
    }

    @Override
    public boolean isPurposeAllowed(int purposeId) {
        return bitVector.readBits1(FieldDefs.V1_PURPOSES_ALLOW.getOffset(bitVector) + purposeId - 1);
    }

    @Override
    public boolean isPurposeAllowed(Purpose purpose) {
        return isPurposeAllowed(purpose.getId());
    }

    @Override
    public boolean isVendorAllowed(int vendorId) {
        int maxVendorId = maxVendorId();
        if (vendorId < 1 || vendorId > maxVendorId) {
            return false;
        }
        int isRangeEncodingOffset = FieldDefs.V1_VENDOR_IS_RANGE_ENCODING.getOffset(bitVector);
        boolean isRangeEncoding = bitVector.readBits1(isRangeEncodingOffset);
        if (isRangeEncoding) {
            final boolean defaultConsent =
                    bitVector.readBits1(FieldDefs.V1_VENDOR_IS_RANGE_ENCODING.getEnd(bitVector));
            final boolean present = isVendorPresentInRange(vendorId);
            return present != defaultConsent;
        } else {
            return bitVector.readBits1(FieldDefs.V1_VENDOR_BITRANGE_FIELD.getOffset(bitVector) + (vendorId - 1));
        }
    }

    @Override
    public Set<Integer> allowedVendorIds() {
        Set<Integer> allowedVendorIds = new HashSet<>();
        int maxVendorId = maxVendorId();
        boolean isRangeEncoding = bitVector.readBits1(FieldDefs.V1_VENDOR_IS_RANGE_ENCODING);
        if (isRangeEncoding) {
            Set<Integer> vendorIds = new HashSet<>();
            boolean isDefaultConsent = bitVector.readBits1(FieldDefs.V1_VENDOR_DEFAULT_CONSENT);
            int numEntries = bitVector.readBits12(FieldDefs.V1_VENDOR_NUM_ENTRIES);
            int currentOffset = FieldDefs.V1_VENDOR_NUM_ENTRIES.getEnd(bitVector);
            for (int i = 0; i < numEntries; i++) {
                boolean isRange = bitVector.readBits1(currentOffset);
                currentOffset++;
                if (isRange) {
                    int startVendorId = bitVector.readBits16(currentOffset);
                    currentOffset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bitVector);
                    int endVendorId = bitVector.readBits16(currentOffset);
                    currentOffset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bitVector);
                    IntStream.rangeClosed(startVendorId, endVendorId).forEach(vendorIds::add);
                } else {
                    int singleVendorId = bitVector.readBits16(currentOffset);
                    currentOffset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bitVector);
                    vendorIds.add(singleVendorId);
                }
            }
            if (isDefaultConsent) {
                IntStream.rangeClosed(1, maxVendorId)
                    .filter(id -> !vendorIds.contains(id))
                    .forEach(allowedVendorIds::add);
            } else {
                allowedVendorIds.addAll(vendorIds);
            }
        } else {
            int offset = FieldDefs.V1_VENDOR_BITRANGE_FIELD.getOffset(bitVector);
            int length = FieldDefs.V1_VENDOR_BITRANGE_FIELD.getLength(bitVector);
            for (int i = 0; i < length; i++) {
                if (bitVector.readBits1(i + offset)) {
                    allowedVendorIds.add(i + 1);
                }
            }
        }
        return allowedVendorIds;
    }

    private boolean isVendorPresentInRange(int vendorId) {
        final int numEntries = bitVector.readBits12(FieldDefs.V1_VENDOR_NUM_ENTRIES);
        int shortLength = FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bitVector);
        int currentOffset = FieldDefs.V1_VENDOR_NUM_ENTRIES.getEnd(bitVector);
        for (int i = 0; i < numEntries; i++) {
            boolean range = bitVector.readBits1(currentOffset);
            currentOffset++;
            if (range) {
                int startVendorId = bitVector.readBits16(currentOffset);
                currentOffset += shortLength;
                int endVendorId = bitVector.readBits16(currentOffset);
                currentOffset += shortLength;

                if (vendorId >= startVendorId && vendorId <= endVendorId) {
                    return true;
                }

            } else {
                int singleVendorId = bitVector.readBits16(currentOffset);
                currentOffset += shortLength;

                if (singleVendorId == vendorId) {
                    return true;
                }
            }
        }
        return false;
    }
}
