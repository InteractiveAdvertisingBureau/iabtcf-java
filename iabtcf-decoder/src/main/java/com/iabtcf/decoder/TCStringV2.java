package com.iabtcf.decoder;

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

import static com.iabtcf.FieldDefs.AV_MAX_VENDOR_ID;
import static com.iabtcf.FieldDefs.AV_VENDOR_BITRANGE_FIELD;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.iabtcf.ByteBitVector;
import com.iabtcf.FieldDefs;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.ByteBitVectorUtils;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.PublisherRestriction;
import com.iabtcf.v2.RestrictionType;
import com.iabtcf.v2.SegmentType;

class TCStringV2 implements TCString {

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
    private IntIterable specialFeaturesOptInts;
    private IntIterable purposesConsent;
    private IntIterable purposesLITransparency;
    private boolean isPurposeOneTreatment;
    private String publisherCountryCode;
    private IntIterable vendorConsents;
    private IntIterable vendorLegitimateInterests;
    private List<PublisherRestriction> publisherRestrictions;
    private IntIterable disclosedVendors;
    private IntIterable allowedVendors;
    private IntIterable publisherPurposesConsent;
    private IntIterable publisherPurposesLITransparency;
    private IntIterable customPurposesConsent;
    private IntIterable customPurposesLITransparency;

    private final EnumSet<FieldDefs> cache = EnumSet.noneOf(FieldDefs.class);
    private final ByteBitVector bbv;
    private final Collection<ByteBitVector> remainingVectors;

    private TCStringV2(ByteBitVector bbv) {
        this(bbv, new ByteBitVector[] {});
    }

    private TCStringV2(ByteBitVector bbv, ByteBitVector... theRest) {
        this.bbv = bbv;
        this.remainingVectors = Arrays.asList(theRest);
    }

    public static TCStringV2 fromBitVector(ByteBitVector coreBitVector, ByteBitVector... remainingVectors) {
        return new TCStringV2(coreBitVector, remainingVectors);
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
    public IntIterable getPubPurposesConsent() {
        if (cache.add(PPTC_PUB_PURPOSES_CONSENT)) {
            publisherPurposesConsent = BitSetIntIterable.EMPTY;

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                publisherPurposesConsent = fillBitSet(dvBbv, PPTC_PUB_PURPOSES_CONSENT);
            }
        }
        return publisherPurposesConsent;
    }

    static BitSetIntIterable fillVendors(ByteBitVector bbv, FieldDefs maxVendor, FieldDefs vendorField) {
        BitSet bs = new BitSet();

        int maxV = bbv.readBits16(maxVendor);
        boolean isRangeEncoding = bbv.readBits1(maxVendor.getEnd(bbv));

        if (isRangeEncoding) {
            vendorIdsFromRange(bbv, bs, vendorField);
        } else {
            for (int i = 0; i < maxV; i++) {
                boolean hasVendorConsent = bbv.readBits1(vendorField.getOffset(bbv) + i);
                if (hasVendorConsent) {
                    bs.set(i + 1);
                }
            }
        }
        return new BitSetIntIterable(bs);
    }

    /**
     * Returns the offset following this range entry
     */
    static int vendorIdsFromRange(ByteBitVector bbv, BitSet bs, int numberOfVendorEntriesOffset) {
        int numberOfVendorEntries = bbv.readBits12(numberOfVendorEntriesOffset);
        int offset = numberOfVendorEntriesOffset + FieldDefs.NUM_ENTRIES.getLength(bbv);

        for (int j = 0; j < numberOfVendorEntries; j++) {
            boolean isRangeEntry = bbv.readBits1(offset++);
            int startOrOnlyVendorId = bbv.readBits16(offset);
            offset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bbv);
            if (isRangeEntry) {
                int endVendorId = bbv.readBits16(offset);
                offset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bbv);
                bs.set(startOrOnlyVendorId, endVendorId + 1);
            } else {
                bs.set(startOrOnlyVendorId);
            }
        }

        return offset;
    }

    static void vendorIdsFromRange(ByteBitVector bbv, BitSet bs, FieldDefs vendorField) {
        vendorIdsFromRange(bbv, bs, vendorField.getOffset(bbv));
    }

    private int fillPublisherRestrictions(
            List<PublisherRestriction> publisherRestrictions, int currentPointer, ByteBitVector bitVector) {

        int numberOfPublisherRestrictions = bitVector.readBits12(currentPointer);
        currentPointer += FieldDefs.NUM_ENTRIES.getLength(bitVector);

        for (int i = 0; i < numberOfPublisherRestrictions; i++) {
            int purposeId = bitVector.readBits6(currentPointer);
            currentPointer += FieldDefs.PURPOSE_ID.getLength(bitVector);

            int restrictionTypeId = bitVector.readBits2(currentPointer);
            currentPointer += 2;
            RestrictionType restrictionType = RestrictionType.from(restrictionTypeId);

            BitSet bs = new BitSet();
            currentPointer = vendorIdsFromRange(bbv, bs, currentPointer);
            PublisherRestriction publisherRestriction =
                    new PublisherRestriction(purposeId, restrictionType, new BitSetIntIterable(bs));
            publisherRestrictions.add(publisherRestriction);
        }
        return currentPointer;
    }

    static BitSetIntIterable fillBitSet(ByteBitVector bbv, FieldDefs field) {
        int offset = field.getOffset(bbv);
        int length = field.getLength(bbv);

        BitSet bs = new BitSet();
        for (int i = 0; i < length; i++) {
            if (bbv.readBits1(offset + i)) {
                bs.set(i + 1);
            }
        }
        return new BitSetIntIterable(bs);
    }

    @Override
    public int getVersion() {
        if (cache.add(CORE_VERSION)) {
            version = bbv.readBits6(CORE_VERSION);
        }
        return version;
    }

    @Override
    public Instant getCreated() {
        if (cache.add(CORE_CREATED)) {
            consentRecordCreated = Instant.ofEpochMilli(bbv.readBits36(CORE_CREATED) * 100);
        }
        return consentRecordCreated;
    }

    @Override
    public Instant getLastUpdated() {
        if (cache.add(CORE_LAST_UPDATED)) {
            consentRecordLastUpdated = Instant.ofEpochMilli(bbv.readBits36(CORE_LAST_UPDATED) * 100);
        }
        return consentRecordLastUpdated;
    }

    @Override
    public int getCmpId() {
        if (cache.add(CORE_CMP_ID)) {
            consentManagerProviderId = (short) bbv.readBits12(CORE_CMP_ID);
        }
        return consentManagerProviderId;
    }

    @Override
    public int getCmpVersion() {
        if (cache.add(CORE_CMP_VERSION)) {
            consentManagerProviderVersion = (short) bbv.readBits12(CORE_CMP_VERSION);
        }
        return consentManagerProviderVersion;
    }

    @Override
    public int getConsentScreen() {
        if (cache.add(CORE_CONSENT_SCREEN)) {
            consentScreen = bbv.readBits6(CORE_CONSENT_SCREEN);
        }
        return consentScreen;
    }

    @Override
    public String getConsentLanguage() {
        if (cache.add(CORE_CONSENT_LANGUAGE)) {
            consentLanguage = ByteBitVectorUtils.readStr2(bbv, CORE_CONSENT_LANGUAGE);
        }
        return consentLanguage;
    }

    @Override
    public int getVendorListVersion() {
        if (cache.add(CORE_VENDOR_LIST_VERSION)) {
            vendorListVersion = (short) bbv.readBits12(CORE_VENDOR_LIST_VERSION);
        }
        return vendorListVersion;
    }

    @Override
    public IntIterable getPurposesConsent() {
        if (cache.add(CORE_PURPOSES_CONSENT)) {
            purposesConsent = fillBitSet(bbv, CORE_PURPOSES_CONSENT);
        }
        return purposesConsent;
    }

    @Override
    public IntIterable getVendorConsent() {
        if (cache.add(CORE_VENDOR_BITRANGE_FIELD)) {
            vendorConsents = fillVendors(bbv, CORE_VENDOR_MAX_VENDOR_ID, CORE_VENDOR_BITRANGE_FIELD);
        }
        return vendorConsents;
    }

    @Override
    public boolean getDefaultVendorConsent() {
        return false;
    }

    @Override
    public int getTcfPolicyVersion() {
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
    public boolean getUseNonStandardStacks() {
        if (cache.add(CORE_USE_NON_STANDARD_STOCKS)) {
            useNonStandardStacks = bbv.readBits1(CORE_USE_NON_STANDARD_STOCKS);
        }
        return useNonStandardStacks;
    }

    @Override
    public IntIterable getSpecialFeatureOptIns() {
        if (cache.add(CORE_SPECIAL_FEATURE_OPT_INS)) {
            specialFeaturesOptInts = fillBitSet(bbv, CORE_SPECIAL_FEATURE_OPT_INS);
        }
        return specialFeaturesOptInts;
    }

    @Override
    public IntIterable getPurposesLITransparency() {
        if (cache.add(CORE_PURPOSES_LI_TRANSPARENCY)) {
            purposesLITransparency = fillBitSet(bbv, CORE_PURPOSES_LI_TRANSPARENCY);
        }
        return purposesLITransparency;
    }

    @Override
    public boolean getPurposeOneTreatment() {
        if (cache.add(CORE_PURPOSE_ONE_TREATMENT)) {
            isPurposeOneTreatment = bbv.readBits1(CORE_PURPOSE_ONE_TREATMENT);
        }
        return isPurposeOneTreatment;
    }

    @Override
    public String getPublisherCC() {
        if (cache.add(CORE_PUBLISHER_CC)) {
            publisherCountryCode = ByteBitVectorUtils.readStr2(bbv, CORE_PUBLISHER_CC);
        }
        return publisherCountryCode;
    }

    @Override
    public IntIterable getVendorLegitimateInterest() {
        if (cache.add(CORE_VENDOR_LI_BITRANGE_FIELD)) {
            vendorLegitimateInterests =
                    fillVendors(bbv, CORE_VENDOR_LI_MAX_VENDOR_ID, FieldDefs.CORE_VENDOR_LI_BITRANGE_FIELD);
        }
        return vendorLegitimateInterests;
    }

    @Override
    public List<PublisherRestriction> getPublisherRestrictions() {
        if (cache.add(CORE_PUB_RESTRICTION_ENTRY)) {
            publisherRestrictions = new ArrayList<>();
            fillPublisherRestrictions(publisherRestrictions, CORE_NUM_PUB_RESTRICTION.getOffset(bbv), bbv);
        }
        return publisherRestrictions;
    }

    @Override
    public IntIterable getAllowedVendors() {
        if (cache.add(AV_VENDOR_BITRANGE_FIELD)) {
            allowedVendors = BitSetIntIterable.EMPTY;

            ByteBitVector dvBbv = getSegment(SegmentType.ALLOWED_VENDOR);
            if (dvBbv != null) {
                allowedVendors = fillVendors(dvBbv, AV_MAX_VENDOR_ID, AV_VENDOR_BITRANGE_FIELD);
            }
        }
        return allowedVendors;
    }

    @Override
    public IntIterable getDisclosedVendors() {
        if (cache.add(DV_VENDOR_BITRANGE_FIELD)) {
            disclosedVendors = BitSetIntIterable.EMPTY;

            ByteBitVector dvBbv = getSegment(SegmentType.DISCLOSED_VENDOR);

            if (dvBbv != null) {
                disclosedVendors = fillVendors(dvBbv, DV_MAX_VENDOR_ID, DV_VENDOR_BITRANGE_FIELD);
            }
        }
        return disclosedVendors;
    }

    @Override
    public IntIterable getPubPurposesLITransparency() {
        if (cache.add(PPTC_PUB_PURPOSES_LI_TRANSPARENCY)) {
            publisherPurposesLITransparency = BitSetIntIterable.EMPTY;

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                publisherPurposesLITransparency = fillBitSet(dvBbv, PPTC_PUB_PURPOSES_LI_TRANSPARENCY);
            }
        }
        return publisherPurposesLITransparency;
    }

    @Override
    public IntIterable getCustomPurposesConsent() {
        if (cache.add(PPTC_CUSTOM_PURPOSES_CONSENT)) {
            customPurposesConsent = BitSetIntIterable.EMPTY;

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                customPurposesConsent = fillBitSet(dvBbv, PPTC_CUSTOM_PURPOSES_CONSENT);
            }
        }
        return customPurposesConsent;
    }

    @Override
    public IntIterable getCustomPurposesLITransparency() {
        if (cache.add(PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY)) {
            customPurposesLITransparency = BitSetIntIterable.EMPTY;

            ByteBitVector dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                customPurposesLITransparency = fillBitSet(dvBbv, PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY);
            }
        }
        return customPurposesLITransparency;
    }
}
