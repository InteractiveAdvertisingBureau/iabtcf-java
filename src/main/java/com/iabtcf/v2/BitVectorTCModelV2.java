package com.iabtcf.v2;

import static com.iabtcf.FieldDefs.AV_MAX_VENDOR_ID;
import static com.iabtcf.FieldDefs.AV_VENDOR_BITRANGE_FIELD;
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
import static com.iabtcf.FieldDefs.CORE_CMP_ID;
import static com.iabtcf.FieldDefs.CORE_CMP_VERSION;
import static com.iabtcf.FieldDefs.CORE_CONSENT_LANGUAGE;
import static com.iabtcf.FieldDefs.CORE_CONSENT_SCREEN;
import static com.iabtcf.FieldDefs.CORE_CREATED;
import static com.iabtcf.FieldDefs.CORE_IS_SERVICE_SPECIFIC;
import static com.iabtcf.FieldDefs.CORE_LAST_UPDATED;
import static com.iabtcf.FieldDefs.CORE_NUM_PUB_RESTRICTION;
import static com.iabtcf.FieldDefs.CORE_PUBLISHER_CC;
import static com.iabtcf.FieldDefs.CORE_PUB_RESTRICTION_ENTRY;
import static com.iabtcf.FieldDefs.CORE_PURPOSES_CONSENT;
import static com.iabtcf.FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.FieldDefs.CORE_PURPOSE_ONE_TREATMENT;
import static com.iabtcf.FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS;
import static com.iabtcf.FieldDefs.CORE_TCF_POLICY_VERSION;
import static com.iabtcf.FieldDefs.CORE_USE_NON_STANDARD_STOCKS;
import static com.iabtcf.FieldDefs.CORE_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.FieldDefs.CORE_VENDOR_LIST_VERSION;
import static com.iabtcf.FieldDefs.CORE_VENDOR_LI_BITRANGE_FIELD;
import static com.iabtcf.FieldDefs.CORE_VENDOR_LI_MAX_VENDOR_ID;
import static com.iabtcf.FieldDefs.CORE_VENDOR_MAX_VENDOR_ID;
import static com.iabtcf.FieldDefs.CORE_VERSION;
import static com.iabtcf.FieldDefs.DV_MAX_VENDOR_ID;
import static com.iabtcf.FieldDefs.DV_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.FieldDefs.OOB_SEGMENT_TYPE;
import static com.iabtcf.FieldDefs.PPTC_CUSTOM_PURPOSES_CONSENT;
import static com.iabtcf.FieldDefs.PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.FieldDefs.PPTC_PUB_PURPOSES_CONSENT;
import static com.iabtcf.FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.v2.FieldConstants.Type.MEDIUM;
import static com.iabtcf.v2.FieldConstants.Type.SHORT;
import static com.iabtcf.v2.FieldConstants.Type.TINY_INT;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import com.iabtcf.ByteBitVector;
import com.iabtcf.FieldDefs;
import com.iabtcf.utils.ByteBitVectorUtils;

public class BitVectorTCModelV2 implements TCModelV2 {

    private int version;
    private Instant consentRecordCreated;
    private Instant consentRecordLastUpdated;
    private int consentManagerProviderId;
    private int consentManagerProviderVersion;
    private int consentScreen;
    private String consentLanguage;
    private int vendorListVersion;
    private int policyVersion;
    private boolean isServiceSpecific;
    private boolean useNonStandardStacks;
    private Set<Integer> specialFeaturesOptInts;
    private Set<Integer> purposesConsent;
    private Set<Integer> purposesLITransparency;
    private boolean isPurposeOneTreatment;
    private String publisherCountryCode;
    private Set<Integer> vendorConsents;
    private Set<Integer> vendorLegitimateInterests;
    private List<PublisherRestriction> publisherRestrictions;
    private Set<Integer> disclosedVendors;
    private Set<Integer> allowedVendors;
    private Set<Integer> publisherPurposesConsent;
    private Set<Integer> publisherPurposesLITransparency;
    private Set<Integer> customPurposesConsent;
    private Set<Integer> customPurposesLITransparency;

    private final EnumSet<FieldDefs> cache = EnumSet.noneOf(FieldDefs.class);
    private final ByteBitVector bbv;
    private final Collection<ByteBitVector> remainingVectors;

    private BitVectorTCModelV2(ByteBitVector bbv) {
        this(bbv, new ByteBitVector[] {});
    }

    private BitVectorTCModelV2(ByteBitVector bbv, ByteBitVector... theRest) {
        this.bbv = bbv;
        this.remainingVectors = Arrays.asList(theRest);
    }

    public static BitVectorTCModelV2 fromBitVector(ByteBitVector coreBitVector, ByteBitVector... remainingVectors) {
        return new BitVectorTCModelV2(coreBitVector, remainingVectors);
    }

    private ByteBitVector getSegment(SegmentType segmentType) {
        if (segmentType == SegmentType.DEFAULT) {
            return bbv;
        }

        for (ByteBitVector rbbv : remainingVectors) {
            int rSegmentType = rbbv.readBits3(OOB_SEGMENT_TYPE);
            if (segmentType == SegmentType.from(rSegmentType)) {
                return rbbv;
            }
        }
        return null;
    }

    @Override
    public int version() {
        if (cache.add(CORE_VERSION)) {
            version = bbv.readBits6(CORE_VERSION);
        }
        return version;
    }

    @Override
    public Instant consentRecordCreated() {
        if (cache.add(CORE_CREATED)) {
            consentRecordCreated = Instant.ofEpochMilli(bbv.readBits36(CORE_CREATED) * 100);
        }
        return consentRecordCreated;
    }

    @Override
    public Instant consentRecordLastUpdated() {
        if (cache.add(CORE_LAST_UPDATED)) {
            consentRecordLastUpdated = Instant.ofEpochMilli(bbv.readBits36(CORE_LAST_UPDATED) * 100);
        }
        return consentRecordLastUpdated;
    }

    @Override
    public int consentManagerProviderId() {
        if (cache.add(CORE_CMP_ID)) {
            consentManagerProviderId = bbv.readBits12(CORE_CMP_ID);
        }
        return consentManagerProviderId;
    }

    @Override
    public int consentManagerProviderVersion() {
        if (cache.add(CORE_CMP_VERSION)) {
            consentManagerProviderVersion = bbv.readBits12(CORE_CMP_VERSION);
        }
        return consentManagerProviderVersion;
    }

    @Override
    public int consentScreen() {
        if (cache.add(CORE_CONSENT_SCREEN)) {
            consentScreen = bbv.readBits6(CORE_CONSENT_SCREEN);
        }
        return consentScreen;
    }

    @Override
    public String consentLanguage() {
        if (cache.add(CORE_CONSENT_LANGUAGE)) {
            consentLanguage = ByteBitVectorUtils.readStr2(bbv, CORE_CONSENT_LANGUAGE);
        }
        return consentLanguage;
    }

    @Override
    public int vendorListVersion() {
        if (cache.add(CORE_VENDOR_LIST_VERSION)) {
            vendorListVersion = bbv.readBits12(CORE_VENDOR_LIST_VERSION);
        }
        return vendorListVersion;
    }

    @Override
    public int policyVersion() {
        if (cache.add(CORE_TCF_POLICY_VERSION)) {
            policyVersion = bbv.readBits6(CORE_TCF_POLICY_VERSION);
        }
        return policyVersion;
    }

    @Override
    public boolean isServiceSpecific() {
        if (cache.add(CORE_IS_SERVICE_SPECIFIC)) {
            isServiceSpecific = bbv.readBits1(CORE_IS_SERVICE_SPECIFIC);
        }
        return isServiceSpecific;
    }

    @Override
    public boolean useNonStandardStacks() {
        if (cache.add(CORE_USE_NON_STANDARD_STOCKS)) {
            useNonStandardStacks = bbv.readBits1(CORE_USE_NON_STANDARD_STOCKS);
        }
        return useNonStandardStacks;
    }

    @Override
    public Set<Integer> specialFeatureOptIns() {
        if (cache.add(CORE_SPECIAL_FEATURE_OPT_INS)) {
            specialFeaturesOptInts = fillSet(
                    CORE_SPECIAL_FEATURE_OPT_INS.getOffset(bbv),
                    CORE_SPECIAL_FEATURE_OPT_INS.getLength(bbv),
                    bbv);
        }
        return specialFeaturesOptInts;
    }

    @Override
    public Set<Integer> purposesConsent() {
        if (cache.add(CORE_PURPOSES_CONSENT)) {
            purposesConsent = fillSet(
                    CORE_PURPOSES_CONSENT.getOffset(bbv),
                    CORE_PURPOSES_CONSENT.getLength(bbv),
                    bbv);
        }
        return purposesConsent;
    }

    @Override
    public Set<Integer> purposesLITransparency() {
        if (cache.add(CORE_PURPOSES_LI_TRANSPARENCY)) {
            purposesLITransparency = fillSet(
                    CORE_PURPOSES_LI_TRANSPARENCY.getOffset(bbv),
                    CORE_PURPOSES_LI_TRANSPARENCY.getLength(bbv),
                    bbv);
        }
        return purposesLITransparency;
    }

    @Override
    public boolean isPurposeOneTreatment() {
        if (cache.add(CORE_PURPOSE_ONE_TREATMENT)) {
            isPurposeOneTreatment = bbv.readBits1(CORE_PURPOSE_ONE_TREATMENT);
        }
        return isPurposeOneTreatment;
    }

    @Override
    public String publisherCountryCode() {
        if (cache.add(CORE_PUBLISHER_CC)) {
            publisherCountryCode = ByteBitVectorUtils.readStr2(bbv, CORE_PUBLISHER_CC);
        }
        return publisherCountryCode;
    }

    @Override
    public Set<Integer> vendorConsents() {
        if (cache.add(CORE_VENDOR_BITRANGE_FIELD)) {
            vendorConsents = new TreeSet<>();
            fetchSet(vendorConsents, CORE_VENDOR_MAX_VENDOR_ID.getOffset(bbv), bbv);
        }
        return vendorConsents;
    }

    @Override
    public Set<Integer> vendorLegitimateInterests() {
        if (cache.add(CORE_VENDOR_LI_BITRANGE_FIELD)) {
            vendorLegitimateInterests = new TreeSet<>();
            fetchSet(vendorLegitimateInterests, CORE_VENDOR_LI_MAX_VENDOR_ID.getOffset(bbv), bbv);
        }
        return vendorLegitimateInterests;
    }

    @Override
    public List<PublisherRestriction> publisherRestrictions() {
        if (cache.add(CORE_PUB_RESTRICTION_ENTRY)) {
            publisherRestrictions = new ArrayList<>();
            fillPublisherRestrictions(publisherRestrictions, CORE_NUM_PUB_RESTRICTION.getOffset(bbv), bbv);
        }
        return publisherRestrictions;
    }

    @Override
    public Set<Integer> publisherPurposesConsent() {
        if (cache.add(PPTC_PUB_PURPOSES_CONSENT)) {
            publisherPurposesConsent = new TreeSet<>();

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                publisherPurposesConsent = fillSet(
                        PPTC_PUB_PURPOSES_CONSENT.getOffset(dvBbv),
                        PPTC_PUB_PURPOSES_CONSENT.getLength(dvBbv),
                        dvBbv);
            }
        }
        return publisherPurposesConsent;
    }

    @Override
    public Set<Integer> publisherPurposesLITransparency() {
        if (cache.add(PPTC_PUB_PURPOSES_LI_TRANSPARENCY)) {
            publisherPurposesLITransparency = new TreeSet<>();

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                publisherPurposesLITransparency = fillSet(
                        PPTC_PUB_PURPOSES_LI_TRANSPARENCY.getOffset(dvBbv),
                        PPTC_PUB_PURPOSES_LI_TRANSPARENCY.getLength(dvBbv),
                        dvBbv);
            }
        }
        return publisherPurposesLITransparency;
    }

    @Override
    public Set<Integer> customPurposesConsent() {
        if (cache.add(PPTC_CUSTOM_PURPOSES_CONSENT)) {
            customPurposesConsent = new TreeSet<>();

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                customPurposesConsent = fillSet(
                        PPTC_CUSTOM_PURPOSES_CONSENT.getOffset(dvBbv),
                        PPTC_CUSTOM_PURPOSES_CONSENT.getLength(dvBbv),
                        dvBbv);
            }
        }
        return customPurposesConsent;
    }

    @Override
    public Set<Integer> customPurposesLITransparency() {
        if (cache.add(PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY)) {
            customPurposesLITransparency = new TreeSet<>();

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                customPurposesLITransparency = fillSet(
                        PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY.getOffset(dvBbv),
                        PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY.getLength(dvBbv),
                        dvBbv);
            }
        }
        return customPurposesLITransparency;
    }

    @Override
    public Set<Integer> disclosedVendors() {
        if (cache.add(DV_VENDOR_BITRANGE_FIELD)) {
            disclosedVendors = new TreeSet<>();

            ByteBitVector dvBbv = getSegment(SegmentType.DISCLOSED_VENDOR);
            if (dvBbv != null) {
                fetchSet(disclosedVendors, DV_MAX_VENDOR_ID.getOffset(dvBbv), dvBbv);
            }
        }
        return disclosedVendors;
    }

    @Override
    public Set<Integer> allowedVendors() {
        if (cache.add(AV_VENDOR_BITRANGE_FIELD)) {
            allowedVendors = new TreeSet<>();

            ByteBitVector dvBbv = getSegment(SegmentType.ALLOWED_VENDOR);
            if (dvBbv != null) {
                fetchSet(allowedVendors, AV_MAX_VENDOR_ID.getOffset(dvBbv), dvBbv);
            }
        }
        return allowedVendors;
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
            RestrictionType restrictionType = RestrictionType.from(restrictionTypeId);

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
