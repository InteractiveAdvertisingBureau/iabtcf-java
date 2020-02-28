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

import static com.iabtcf.v2.FieldConstants.CoreStringConstants.CMP_ID_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.CMP_VERSION_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.CONSENT_LANGUAGE_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.CONSENT_SCREEN_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.CREATED_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.IS_SERVICE_SPECIFIC_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.LAST_UPDATED_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.PUBLISHER_CC_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.PURPOSES_CONSENT_LENGTH;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.PURPOSES_CONSENT_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.PURPOSE_LI_TRANSPARENCY_LENGTH;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.PURPOSE_LI_TRANSPARENCY_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.PURPOSE_ONE_TREATMENT_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SEGMENT_TYPE_ALLOWED_VENDOR;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SEGMENT_TYPE_DEFAULT;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SEGMENT_TYPE_DISCLOSED_VENDOR;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SEGMENT_TYPE_LENGTH;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SEGMENT_TYPE_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SEGMENT_TYPE_PUBLISHER_TC;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SPECIAL_FEATURE_OPT_INS_LENGTH;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.SPECIAL_FEATURE_OPT_INS_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.TCF_POLICY_VERSION_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.USE_NON_STANDARD_STACKS_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.VENDOR_LIST_VERSION_OFFSET;
import static com.iabtcf.v2.FieldConstants.CoreStringConstants.VERSION_OFFSET;
import static com.iabtcf.v2.FieldConstants.Type.CHAR;
import static com.iabtcf.v2.FieldConstants.Type.MEDIUM;
import static com.iabtcf.v2.FieldConstants.Type.SHORT;
import static com.iabtcf.v2.FieldConstants.Type.TINY_INT;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import com.iabtcf.ByteBitVector;

public class BitVectorTCModelV2 implements TCModelV2 {

    private final int version;
    private final Instant consentRecordCreated;
    private final Instant consentRecordLastUpdated;
    private final int consentManagerProviderId;
    private final int consentManagerProviderVersion;
    private final int consentScreen;
    private final String consentLanguage;
    private final int vendorListVersion;
    private final int policyVersion;
    private final boolean isServiceSpecific;
    private final boolean useNonStandardStacks;
    private final boolean isPurposeOneTreatment;
    private final String publisherCountryCode;
    private final Set<Integer> specialFeaturesOptInts;
    private final Set<Integer> purposesConsent;
    private final Set<Integer> purposesLITransparency;
    private final Set<Integer> vendorConsents;
    private final Set<Integer> vendorLegitimateInterests;
    private final List<PublisherRestriction> publisherRestrictions;

    private final Set<Integer> disclosedVendors;
    private final Set<Integer> allowedVendors;
    private final Set<Integer> publisherPurposesConsent;
    private final Set<Integer> publisherPurposesLITransparency;
    private final Set<Integer> customPurposesConsent;
    private final Set<Integer> customPurposesLITransparency;

    private BitVectorTCModelV2(ByteBitVector bv) {
        this.version = bv.readBits6(VERSION_OFFSET);
        this.consentRecordCreated = Instant.ofEpochMilli(bv.readBits36(CREATED_OFFSET) * 100);
        this.consentRecordLastUpdated = Instant.ofEpochMilli(bv.readBits36(LAST_UPDATED_OFFSET) * 100);
        this.consentManagerProviderId = bv.readBits12(CMP_ID_OFFSET);
        this.consentManagerProviderVersion = bv.readBits12(CMP_VERSION_OFFSET);
        this.consentScreen = bv.readBits6(CONSENT_SCREEN_OFFSET);
        this.consentLanguage = readStr2(bv, CONSENT_LANGUAGE_OFFSET);
        this.vendorListVersion = bv.readBits12(VENDOR_LIST_VERSION_OFFSET);
        this.policyVersion = bv.readBits6(TCF_POLICY_VERSION_OFFSET);
        this.isServiceSpecific = bv.readBits1(IS_SERVICE_SPECIFIC_OFFSET);
        this.useNonStandardStacks = bv.readBits1(USE_NON_STANDARD_STACKS_OFFSET);
        this.specialFeaturesOptInts =
                fillSet(SPECIAL_FEATURE_OPT_INS_OFFSET, SPECIAL_FEATURE_OPT_INS_LENGTH, bv);
        this.purposesConsent = fillSet(PURPOSES_CONSENT_OFFSET, PURPOSES_CONSENT_LENGTH, bv);
        this.purposesLITransparency =
                fillSet(PURPOSE_LI_TRANSPARENCY_OFFSET, PURPOSE_LI_TRANSPARENCY_LENGTH, bv);
        this.isPurposeOneTreatment = bv.readBits1(PURPOSE_ONE_TREATMENT_OFFSET);
        this.publisherCountryCode = readStr2(bv, PUBLISHER_CC_OFFSET);

        int currentPointer = PUBLISHER_CC_OFFSET + (CHAR.length() * 2); // publisher cc offset
        this.vendorConsents = new TreeSet<>();
        currentPointer = this.fetchSet(this.vendorConsents, currentPointer, bv);

        this.vendorLegitimateInterests = new TreeSet<>();
        currentPointer = this.fetchSet(this.vendorLegitimateInterests, currentPointer, bv);

        this.publisherRestrictions = new ArrayList<>();
        this.fillPublisherRestrictions(publisherRestrictions, currentPointer, bv);

        this.disclosedVendors = new TreeSet<>();
        this.allowedVendors = new TreeSet<>();
        this.publisherPurposesConsent = new TreeSet<>();
        this.publisherPurposesLITransparency = new TreeSet<>();
        this.customPurposesConsent = new TreeSet<>();
        this.customPurposesLITransparency = new TreeSet<>();
    }

    private BitVectorTCModelV2(ByteBitVector coreVector, ByteBitVector... theRest) {
        this(coreVector);
        for (ByteBitVector bitVector : theRest) {
            fillRemainingVector(bitVector);
        }
    }

    public static BitVectorTCModelV2 fromBitVector(
            ByteBitVector coreBitVector, ByteBitVector... remainingVectors) {
        return new BitVectorTCModelV2(coreBitVector, remainingVectors);
    }

    public static String readStr2(ByteBitVector bv, int offset) {
        return String
            .valueOf(new char[] {(char) ('A' + bv.readBits6(offset)), (char) ('A' + bv.readBits6(offset + 6))});
    }

    private int fillRemainingVector(ByteBitVector bitVector) {
        int currentPointer = 0;
        int segmentType = bitVector.readBits3(SEGMENT_TYPE_OFFSET);
        currentPointer += SEGMENT_TYPE_OFFSET + SEGMENT_TYPE_LENGTH;
        switch (segmentType) {
            case SEGMENT_TYPE_DEFAULT:
                break;
            case SEGMENT_TYPE_DISCLOSED_VENDOR:
                currentPointer = fetchSet(this.disclosedVendors, currentPointer, bitVector);
                break;
            case SEGMENT_TYPE_ALLOWED_VENDOR:
                currentPointer = fetchSet(this.allowedVendors, currentPointer, bitVector);
                break;
            case SEGMENT_TYPE_PUBLISHER_TC:
                currentPointer = fillPublisherPurposesTC(currentPointer, bitVector);
                break;
        }
        return currentPointer;
    }

    private int fillPublisherPurposesTC(int currentPointer, ByteBitVector bitVector) {
        this.publisherPurposesConsent.addAll(
                fillSet(currentPointer, PURPOSES_CONSENT_LENGTH, bitVector));
        currentPointer += PURPOSES_CONSENT_LENGTH;

        this.publisherPurposesLITransparency.addAll(
                fillSet(currentPointer, PURPOSE_LI_TRANSPARENCY_LENGTH, bitVector));
        currentPointer += PURPOSE_LI_TRANSPARENCY_LENGTH;

        int numberOfCustomPurposes = bitVector.readBits6(currentPointer);
        currentPointer += TINY_INT.length();

        this.customPurposesConsent.addAll(fillSet(currentPointer, numberOfCustomPurposes, bitVector));
        currentPointer += numberOfCustomPurposes;

        this.customPurposesLITransparency.addAll(
                fillSet(currentPointer, numberOfCustomPurposes, bitVector));
        currentPointer += numberOfCustomPurposes;

        return currentPointer;
    }

    @Override
    public int version() {
        return this.version;
    }

    @Override
    public Instant consentRecordCreated() {
        return this.consentRecordCreated;
    }

    @Override
    public Instant consentRecordLastUpdated() {
        return this.consentRecordLastUpdated;
    }

    @Override
    public int consentManagerProviderId() {
        return this.consentManagerProviderId;
    }

    @Override
    public int consentManagerProviderVersion() {
        return this.consentManagerProviderVersion;
    }

    @Override
    public int consentScreen() {
        return this.consentScreen;
    }

    @Override
    public String consentLanguage() {
        return this.consentLanguage;
    }

    @Override
    public int vendorListVersion() {
        return this.vendorListVersion;
    }

    @Override
    public int policyVersion() {
        return this.policyVersion;
    }

    @Override
    public boolean isServiceSpecific() {
        return this.isServiceSpecific;
    }

    @Override
    public boolean useNonStandardStacks() {
        return this.useNonStandardStacks;
    }

    @Override
    public Set<Integer> specialFeatureOptIns() {
        return this.specialFeaturesOptInts;
    }

    @Override
    public Set<Integer> purposesConsent() {
        return this.purposesConsent;
    }

    @Override
    public Set<Integer> purposesLITransparency() {
        return this.purposesLITransparency;
    }

    @Override
    public boolean isPurposeOneTreatment() {
        return this.isPurposeOneTreatment;
    }

    @Override
    public String publisherCountryCode() {
        return this.publisherCountryCode;
    }

    @Override
    public Set<Integer> vendorConsents() {
        return this.vendorConsents;
    }

    @Override
    public Set<Integer> vendorLegitimateInterests() {
        return this.vendorLegitimateInterests;
    }

    @Override
    public List<PublisherRestriction> publisherRestrictions() {
        return this.publisherRestrictions;
    }

    @Override
    public Set<Integer> publisherPurposesConsent() {
        return this.publisherPurposesConsent;
    }

    @Override
    public Set<Integer> publisherPurposesLITransparency() {
        return this.publisherPurposesLITransparency;
    }

    @Override
    public Set<Integer> customPurposesConsent() {
        return this.customPurposesConsent;
    }

    @Override
    public Set<Integer> customPurposesLITransparency() {
        return this.customPurposesLITransparency;
    }

    @Override
    public Set<Integer> disclosedVendors() {
        return this.disclosedVendors;
    }

    @Override
    public Set<Integer> allowedVendors() {
        return this.allowedVendors;
    }

    private int fetchSet(Set<Integer> set, int currentPointer, ByteBitVector bitVector) {
        int maxVendor = bitVector.readBits16(currentPointer);
        currentPointer += MEDIUM.length();
        boolean isRangeEncoding = bitVector.readBits1(currentPointer++);

        if (isRangeEncoding) {
            currentPointer = vendorIdsFromRange(set, bitVector, currentPointer);
        } else {
            for (int i = 0; i < maxVendor; i++) {
                boolean hasVendorConsent = bitVector.readBits1(currentPointer++);
                if (hasVendorConsent) {
                    set.add(i + 1);
                }
            }
        }

        return currentPointer;
    }

    private int vendorIdsFromRange(
            Collection<Integer> vendorIds, ByteBitVector bitVector, int currentPointer) {
        int numberOfVendorEntries = bitVector.readBits12(currentPointer);
        currentPointer += SHORT.length();
        for (int j = 0; j < numberOfVendorEntries; j++) {
            boolean isRangeEntry = bitVector.readBits1(currentPointer++);
            int startOrOnlyVendorId = bitVector.readBits16(currentPointer);
            currentPointer += MEDIUM.length();
            if (isRangeEntry) {
                int endVendorId = bitVector.readBits16(currentPointer);
                currentPointer += MEDIUM.length();
                IntStream.rangeClosed(startOrOnlyVendorId, endVendorId).forEach(vendorIds::add);
            } else {
                vendorIds.add(startOrOnlyVendorId);
            }
        }
        return currentPointer;
    }

    private int fillPublisherRestrictions(
            List<PublisherRestriction> publisherRestrictions, int currentPointer, ByteBitVector bitVector) {

        int numberOfPublisherRestrictions = bitVector.readBits12(currentPointer);
        currentPointer += SHORT.length();

        for (int i = 0; i < numberOfPublisherRestrictions; i++) {
            int purposeId = bitVector.readBits6(currentPointer);
            currentPointer += TINY_INT.length();

            int restrictionTypeId = bitVector.readBits2(currentPointer);
            currentPointer += 2;
            RestrictionType restrictionType = RestrictionType.fromId(restrictionTypeId);

            List<Integer> vendorIds = new ArrayList<>();
            currentPointer = vendorIdsFromRange(vendorIds, bitVector, currentPointer);

            PublisherRestriction publisherRestriction =
                    new PublisherRestriction(purposeId, restrictionType, vendorIds);
            publisherRestrictions.add(publisherRestriction);
        }
        return currentPointer;
    }

    private Set<Integer> fillSet(int offset, int length, ByteBitVector bitVector) {
        Set<Integer> set = new TreeSet<>();
        for (int i = 0; i < length; i++) {
            if (bitVector.readBits1(offset + i)) {
                set.add(i + 1);
            }
        }
        return set;
    }
}
