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
import static com.iabtcf.v1.FieldConstants.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iabtcf.ByteBitVector;

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
        return bitVector.readBits6(VERSION_BIT_OFFSET);
    }

    @Override
    public Instant consentRecordCreated() {
        return deciSeconds(bitVector, CREATED_BIT_OFFSET);
    }

    @Override
    public Instant consentRecordLastUpdated() {
        return deciSeconds(bitVector, UPDATED_BIT_OFFSET);
    }

    @Override
    public int cmpId() {
        return bitVector.readBits12(CMP_ID_OFFSET);
    }

    @Override
    public int cmpVersion() {
        return bitVector.readBits12(CMP_VERSION_OFFSET);
    }

    @Override
    public int consentScreen() {
        return bitVector.readBits6(CONSENT_SCREEN_OFFSET);
    }

    @Override
    public String consentLanguage() {
        return readStr2(bitVector, CONSENT_LANGUAGE_OFFSET);
    }

    @Override
    public int vendorListVersion() {
        return bitVector.readBits12(VENDOR_LIST_VERSION_OFFSET);
    }

    @Override
    public Set<Integer> allowedPurposeIds() {
        final Set<Integer> allowedPurposes = new HashSet<>();
        for (int i = 0; i < PURPOSES_SIZE; i++) {
            if (bitVector.readBits1(PURPOSES_OFFSET + i)) {
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
        return bitVector.readBits16(MAX_VENDOR_ID_OFFSET);
    }

    @Override
    public boolean isPurposeAllowed(int purposeId) {
        return bitVector.readBits1(PURPOSES_OFFSET + purposeId - 1);
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
        int encodingType = bitVector.readBits1(ENCODING_TYPE_OFFSET) ? 1 : 0;
        if (encodingType == VENDOR_ENCODING_RANGE) {
            final boolean defaultConsent = bitVector.readBits1(DEFAULT_CONSENT_OFFSET);
            final boolean present = isVendorPresentInRange(vendorId);
            return present != defaultConsent;
        } else {
            return bitVector.readBits1(VENDOR_BITFIELD_OFFSET + (vendorId - 1));
        }
    }

    @Override
    public Set<Integer> allowedVendorIds() {
        Set<Integer> allowedVendorIds = new HashSet<>();
        int maxVendorId = maxVendorId();
        int encodingType = bitVector.readBits1(ENCODING_TYPE_OFFSET) ? 1 : 0;
        if (encodingType == VENDOR_ENCODING_RANGE) {
            Set<Integer> vendorIds = new HashSet<>();
            boolean isDefaultConsent = bitVector.readBits1(DEFAULT_CONSENT_OFFSET);
            int numEntries = bitVector.readBits12(NUM_ENTRIES_OFFSET);
            int currentOffset = RANGE_ENTRY_OFFSET;
            for (int i = 0; i < numEntries; i++) {
                boolean isRange = bitVector.readBits1(currentOffset);
                currentOffset++;
                if (isRange) {
                    int startVendorId = bitVector.readBits16(currentOffset);
                    currentOffset += VENDOR_ID_SIZE;
                    int endVendorId = bitVector.readBits16(currentOffset);
                    currentOffset += VENDOR_ID_SIZE;
                    IntStream.rangeClosed(startVendorId, endVendorId).forEach(vendorIds::add);
                } else {
                    int singleVendorId = bitVector.readBits16(currentOffset);
                    currentOffset += VENDOR_ID_SIZE;
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
            for (int i = VENDOR_BITFIELD_OFFSET; i < VENDOR_BITFIELD_OFFSET + maxVendorId; i++) {
                if (bitVector.readBits1(i)) {
                    allowedVendorIds.add(i - VENDOR_BITFIELD_OFFSET + 1);
                }
            }
        }
        return allowedVendorIds;
    }

    private boolean isVendorPresentInRange(int vendorId) {
        final int numEntries = bitVector.readBits12(NUM_ENTRIES_OFFSET);
        int shortLength = VENDOR_ID_SIZE;
        int currentOffset = RANGE_ENTRY_OFFSET;
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