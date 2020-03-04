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

import com.iabtcf.ByteBitVector;
import com.iabtcf.v2.Field.CoreString;
import com.iabtcf.v2.Field.PublisherRestrictions;
import com.iabtcf.v2.Field.PublisherTC;
import com.iabtcf.v2.Field.Vendors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.IntStream;

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
        this.version = bv.readByte(CoreString.VERSION);
        this.consentRecordCreated = Instant.ofEpochMilli(bv.readBits36(CoreString.CREATED.getOffset()) * 100);
        this.consentRecordLastUpdated = Instant.ofEpochMilli(bv.readBits36(CoreString.LAST_UPDATED.getOffset()) * 100);
        this.consentManagerProviderId = bv.readBits12(CoreString.CMP_ID.getOffset());
        this.consentManagerProviderVersion = bv.readBits12(CoreString.CMP_VERSION.getOffset());
        this.consentScreen = bv.readByte(CoreString.CONSENT_SCREEN);
        this.consentLanguage = readStr2(bv, CoreString.CONSENT_LANGUAGE.getOffset());
        this.vendorListVersion = bv.readBits12(CoreString.VENDOR_LIST_VERSION.getOffset());
        this.policyVersion = bv.readByte(CoreString.TCF_POLICY_VERSION);
        this.isServiceSpecific = bv.readBit(CoreString.IS_SERVICE_SPECIFIC);
        this.useNonStandardStacks = bv.readBit(CoreString.USE_NON_STANDARD_STACKS);
        this.specialFeaturesOptInts = bv.readSet(CoreString.SPECIAL_FEATURE_OPT_INS);
        this.purposesConsent = bv.readSet(CoreString.PURPOSES_CONSENT);
        this.purposesLITransparency = bv.readSet(CoreString.PURPOSE_LI_TRANSPARENCY);
        this.isPurposeOneTreatment = bv.readBit(CoreString.PURPOSE_ONE_TREATMENT);
        this.publisherCountryCode = readStr2(bv, CoreString.PUBLISHER_CC.getOffset());

        int currentPointer = CoreString.PUBLISHER_CC.getOffset() + CoreString.PUBLISHER_CC.getLength(); // publisher cc offset
        this.vendorConsents = new HashSet<>();
        currentPointer = this.fetchSet(this.vendorConsents, currentPointer, bv);

        this.vendorLegitimateInterests = new HashSet<>();
        currentPointer = this.fetchSet(this.vendorLegitimateInterests, currentPointer, bv);

        this.publisherRestrictions = new ArrayList<>();
        this.fillPublisherRestrictions(publisherRestrictions, currentPointer, bv);

        this.disclosedVendors = new HashSet<>();
        this.allowedVendors = new HashSet<>();
        this.publisherPurposesConsent = new HashSet<>();
        this.publisherPurposesLITransparency = new HashSet<>();
        this.customPurposesConsent = new HashSet<>();
        this.customPurposesLITransparency = new HashSet<>();
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
        return String.valueOf(new char[] {
                (char) ('A' + bv.readByte(offset, Field.CHAR_LENGTH)),
                (char) ('A' + bv.readByte(offset + Field.CHAR_LENGTH, Field.CHAR_LENGTH))
        });
    }

    private void fillRemainingVector(ByteBitVector bitVector) {
        int segmentType = bitVector.readByte(Field.OptionalSegment.SEGMENT_TYPE);
        switch (SegmentType.valueOf(segmentType)) {
            case DISCLOSED_VENDOR:
                fetchSet(this.disclosedVendors, Vendors.MAX_VENDOR_ID.getOffset(), bitVector);
                break;
            case ALLOWED_VENDOR:
                fetchSet(this.allowedVendors, Vendors.MAX_VENDOR_ID.getOffset(), bitVector);
                break;
            case PUBLISHER_TC:
                fillPublisherPurposesTC(bitVector);
                break;
        }
    }

    private void fillPublisherPurposesTC(final ByteBitVector bv) {
        this.publisherPurposesConsent.addAll(bv.readSet(PublisherTC.PURPOSE_CONSENT));

        this.publisherPurposesLITransparency.addAll(bv.readSet(PublisherTC.PURPOSES_LI_TRANSPARENCY));

        int numberOfCustomPurposes = bv.readByte(PublisherTC.NUM_CUSTOM_PURPOSES);

        this.customPurposesConsent.addAll(bv.readSet(
                PublisherTC.CUSTOM_PURPOSES_CONSENT.getOffset(),
                numberOfCustomPurposes));
        this.customPurposesLITransparency.addAll(bv.readSet(
                PublisherTC.CUSTOM_PURPOSES_CONSENT.getOffset() + numberOfCustomPurposes,
                numberOfCustomPurposes));
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
        currentPointer += Vendors.MAX_VENDOR_ID.getLength();
        boolean isRangeEncoding = bitVector.readBit(currentPointer++);

        if (isRangeEncoding) {
            currentPointer = vendorIdsFromRange(set, bitVector, currentPointer);
        } else {
            for (int i = 0; i < maxVendor; i++) {
                boolean hasVendorConsent = bitVector.readBit(currentPointer++);
                if (hasVendorConsent) {
                    set.add(i + 1);
                }
            }
        }

        return currentPointer;
    }

    private int vendorIdsFromRange(Collection<Integer> vendorIds, ByteBitVector bitVector, int currentPointer) {
        int numberOfVendorEntries = bitVector.readBits12(currentPointer);
        currentPointer += Vendors.NUM_ENTRIES.getLength();
        for (int j = 0; j < numberOfVendorEntries; j++) {
            boolean isRangeEntry = bitVector.readBit(currentPointer++);
            int startOrOnlyVendorId = bitVector.readBits16(currentPointer);
            currentPointer += Vendors.START_OR_ONLY_VENDOR_ID.getLength();
            if (isRangeEntry) {
                int endVendorId = bitVector.readBits16(currentPointer);
                currentPointer += Vendors.END_VENDOR_ID.getLength();
                IntStream.rangeClosed(startOrOnlyVendorId, endVendorId).forEach(vendorIds::add);
            } else {
                vendorIds.add(startOrOnlyVendorId);
            }
        }
        return currentPointer;
    }

    private void fillPublisherRestrictions(List<PublisherRestriction> publisherRestrictions,
                                           int currentPointer,
                                           ByteBitVector bitVector) {
        int numberOfPublisherRestrictions = bitVector.readBits12(currentPointer);
        currentPointer += PublisherRestrictions.NUM_PUB_RESTRICTIONS.getLength();

        for (int i = 0; i < numberOfPublisherRestrictions; i++) {
            int purposeId = bitVector.readByte(currentPointer, PublisherRestrictions.PURPOSE_ID.getLength());
            currentPointer += PublisherRestrictions.PURPOSE_ID.getLength();

            int restrictionTypeId = bitVector.readByte(currentPointer, PublisherRestrictions.RESTRICTION_TYPE.getLength());
            currentPointer += PublisherRestrictions.RESTRICTION_TYPE.getLength();
            RestrictionType restrictionType = RestrictionType.fromId(restrictionTypeId);

            List<Integer> vendorIds = new ArrayList<>();
            currentPointer = vendorIdsFromRange(vendorIds, bitVector, currentPointer);

            PublisherRestriction publisherRestriction = new PublisherRestriction(purposeId, restrictionType, vendorIds);
            publisherRestrictions.add(publisherRestriction);
        }
    }
}
