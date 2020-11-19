package com.iabtcf.decoder;

/*-
 * #%L
 * IAB TCF Java Decoder Library
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

import static com.iabtcf.utils.FieldDefs.AV_MAX_VENDOR_ID;
import static com.iabtcf.utils.FieldDefs.AV_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.utils.FieldDefs.CORE_CMP_ID;
import static com.iabtcf.utils.FieldDefs.CORE_CMP_VERSION;
import static com.iabtcf.utils.FieldDefs.CORE_CONSENT_LANGUAGE;
import static com.iabtcf.utils.FieldDefs.CORE_CONSENT_SCREEN;
import static com.iabtcf.utils.FieldDefs.CORE_CREATED;
import static com.iabtcf.utils.FieldDefs.CORE_IS_SERVICE_SPECIFIC;
import static com.iabtcf.utils.FieldDefs.CORE_LAST_UPDATED;
import static com.iabtcf.utils.FieldDefs.CORE_NUM_PUB_RESTRICTION;
import static com.iabtcf.utils.FieldDefs.CORE_PUBLISHER_CC;
import static com.iabtcf.utils.FieldDefs.CORE_PUB_RESTRICTION_ENTRY;
import static com.iabtcf.utils.FieldDefs.CORE_PURPOSES_CONSENT;
import static com.iabtcf.utils.FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.utils.FieldDefs.CORE_PURPOSE_ONE_TREATMENT;
import static com.iabtcf.utils.FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS;
import static com.iabtcf.utils.FieldDefs.CORE_TCF_POLICY_VERSION;
import static com.iabtcf.utils.FieldDefs.CORE_USE_NON_STANDARD_STOCKS;
import static com.iabtcf.utils.FieldDefs.CORE_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.utils.FieldDefs.CORE_VENDOR_LIST_VERSION;
import static com.iabtcf.utils.FieldDefs.CORE_VENDOR_LI_BITRANGE_FIELD;
import static com.iabtcf.utils.FieldDefs.CORE_VENDOR_LI_MAX_VENDOR_ID;
import static com.iabtcf.utils.FieldDefs.CORE_VENDOR_MAX_VENDOR_ID;
import static com.iabtcf.utils.FieldDefs.CORE_VERSION;
import static com.iabtcf.utils.FieldDefs.DV_MAX_VENDOR_ID;
import static com.iabtcf.utils.FieldDefs.DV_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.utils.FieldDefs.OOB_SEGMENT_TYPE;
import static com.iabtcf.utils.FieldDefs.PPTC_CUSTOM_PURPOSES_CONSENT;
import static com.iabtcf.utils.FieldDefs.PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.utils.FieldDefs.PPTC_PUB_PURPOSES_CONSENT;
import static com.iabtcf.utils.FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.iabtcf.exceptions.InvalidRangeFieldException;
import com.iabtcf.utils.BitReader;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;
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
    private final BitReader bbv;
    private final Collection<BitReader> remainingVectors;

    private TCStringV2(BitReader bbv) {
        this(bbv, new BitReader[] {});
    }

    private TCStringV2(BitReader bbv, BitReader... theRest) {
        this.bbv = bbv;
        this.remainingVectors = Arrays.asList(theRest);
    }

    public static TCStringV2 fromBitVector(BitReader coreBitVector, BitReader... remainingVectors) {
        return new TCStringV2(coreBitVector, remainingVectors);
    }

    private BitReader getSegment(SegmentType segmentType) {
        if (segmentType == SegmentType.DEFAULT) {
            return bbv;
        }

        for (BitReader rbbv : remainingVectors) {
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

            BitReader dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                publisherPurposesConsent = fillBitSet(dvBbv, PPTC_PUB_PURPOSES_CONSENT);
            }
        }
        return publisherPurposesConsent;
    }

    /**
     * @throws InvalidRangeFieldException
     */
    static BitSetIntIterable fillVendors(BitReader bbv, FieldDefs maxVendor, FieldDefs vendorField) {
        BitSet bs = new BitSet();

        int maxV = bbv.readBits16(maxVendor);
        boolean isRangeEncoding = bbv.readBits1(maxVendor.getEnd(bbv));

        if (isRangeEncoding) {
            vendorIdsFromRange(bbv, bs, vendorField, Optional.of(maxVendor));
        } else {
            for (int i = 0; i < maxV; i++) {
                boolean hasVendorConsent = bbv.readBits1(vendorField.getOffset(bbv) + i);
                if (hasVendorConsent) {
                    bs.set(i + 1);
                }
            }
        }
        return BitSetIntIterable.from(bs);
    }

    /**
     * Returns the offset following this range entry
     *
     * @throws InvalidRangeFieldException
     */
    static int vendorIdsFromRange(BitReader bbv, BitSet bs, int numberOfVendorEntriesOffset,
            Optional<FieldDefs> maxVendor) {
        int numberOfVendorEntries = bbv.readBits12(numberOfVendorEntriesOffset);
        int offset = numberOfVendorEntriesOffset + FieldDefs.NUM_ENTRIES.getLength(bbv);
        int maxV = maxVendor.map(maxVF -> bbv.readBits16(maxVF)).orElse(Integer.MAX_VALUE);

        for (int j = 0; j < numberOfVendorEntries; j++) {
            boolean isRangeEntry = bbv.readBits1(offset++);
            int startOrOnlyVendorId = bbv.readBits16(offset);
            offset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bbv);
            if (isRangeEntry) {
                int endVendorId = bbv.readBits16(offset);
                offset += FieldDefs.START_OR_ONLY_VENDOR_ID.getLength(bbv);

                if (startOrOnlyVendorId > endVendorId) {
                    throw new InvalidRangeFieldException(String.format(
                            "start vendor id (%d) is greater than endVendorId (%d)", startOrOnlyVendorId,
                            endVendorId));
                }

                if (endVendorId > maxV) {
                    throw new InvalidRangeFieldException(
                            String.format("end vendor id (%d) is greater than max (%d)", endVendorId, maxV));
                }

                bs.set(startOrOnlyVendorId, endVendorId + 1);
            } else {
                bs.set(startOrOnlyVendorId);
            }
        }

        return offset;
    }

    /**
     * @throws InvalidRangeFieldException
     */
    static void vendorIdsFromRange(BitReader bbv, BitSet bs, FieldDefs vendorField, Optional<FieldDefs> maxVendor) {
        vendorIdsFromRange(bbv, bs, vendorField.getOffset(bbv), maxVendor);
    }

    /**
     * @throws InvalidRangeFieldException
     */
    private int fillPublisherRestrictions(
            List<PublisherRestriction> publisherRestrictions, int currentPointer, BitReader bitVector) {

        int numberOfPublisherRestrictions = bitVector.readBits12(currentPointer);
        currentPointer += FieldDefs.NUM_ENTRIES.getLength(bitVector);

        for (int i = 0; i < numberOfPublisherRestrictions; i++) {
            int purposeId = bitVector.readBits6(currentPointer);
            currentPointer += FieldDefs.PURPOSE_ID.getLength(bitVector);

            int restrictionTypeId = bitVector.readBits2(currentPointer);
            currentPointer += 2;
            RestrictionType restrictionType = RestrictionType.from(restrictionTypeId);

            BitSet bs = new BitSet();
            currentPointer = vendorIdsFromRange(bbv, bs, currentPointer, Optional.empty());
            PublisherRestriction publisherRestriction =
                    new PublisherRestriction(purposeId, restrictionType, BitSetIntIterable.from(bs));
            publisherRestrictions.add(publisherRestriction);
        }
        return currentPointer;
    }

    static BitSetIntIterable fillBitSet(BitReader bbv, FieldDefs field) {
        int offset = field.getOffset(bbv);
        int length = field.getLength(bbv);

        BitSetIntIterable.Builder bs = BitSetIntIterable.newBuilder();
        for (int i = 0; i < length; i++) {
            if (bbv.readBits1(offset + i)) {
                bs.add(i + 1);
            }
        }
        return bs.build();
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
            consentLanguage = bbv.readStr2(CORE_CONSENT_LANGUAGE);
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

    /**
     * @throws InvalidRangeFieldException
     */
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
            publisherCountryCode = bbv.readStr2(CORE_PUBLISHER_CC);
        }
        return publisherCountryCode;
    }

    /**
     * @throws InvalidRangeFieldException
     */
    @Override
    public IntIterable getVendorLegitimateInterest() {
        if (cache.add(CORE_VENDOR_LI_BITRANGE_FIELD)) {
            vendorLegitimateInterests =
                    fillVendors(bbv, CORE_VENDOR_LI_MAX_VENDOR_ID, FieldDefs.CORE_VENDOR_LI_BITRANGE_FIELD);
        }
        return vendorLegitimateInterests;
    }

    /**
     * @throws InvalidRangeFieldException
     */
    @Override
    public List<PublisherRestriction> getPublisherRestrictions() {
        if (cache.add(CORE_PUB_RESTRICTION_ENTRY)) {
            publisherRestrictions = new ArrayList<>();
            fillPublisherRestrictions(publisherRestrictions, CORE_NUM_PUB_RESTRICTION.getOffset(bbv), bbv);
        }
        return publisherRestrictions;
    }

    /**
     * @throws InvalidRangeFieldException
     */
    @Override
    public IntIterable getAllowedVendors() {
        if (cache.add(AV_VENDOR_BITRANGE_FIELD)) {
            allowedVendors = BitSetIntIterable.EMPTY;

            BitReader dvBbv = getSegment(SegmentType.ALLOWED_VENDOR);
            if (dvBbv != null) {
                allowedVendors = fillVendors(dvBbv, AV_MAX_VENDOR_ID, AV_VENDOR_BITRANGE_FIELD);
            }
        }
        return allowedVendors;
    }

    /**
     * @throws InvalidRangeFieldException
     */
    @Override
    public IntIterable getDisclosedVendors() {
        if (cache.add(DV_VENDOR_BITRANGE_FIELD)) {
            disclosedVendors = BitSetIntIterable.EMPTY;

            BitReader dvBbv = getSegment(SegmentType.DISCLOSED_VENDOR);

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

            BitReader dvBbv = getSegment(SegmentType.PUBLISHER_TC);
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

            BitReader dvBbv = getSegment(SegmentType.PUBLISHER_TC);
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

            BitReader dvBbv = getSegment(SegmentType.PUBLISHER_TC);
            if (dvBbv != null) {
                customPurposesLITransparency = fillBitSet(dvBbv, PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY);
            }
        }
        return customPurposesLITransparency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getAllowedVendors(),
                getConsentLanguage(),
                getCmpId(),
                getCmpVersion(),
                getCreated(),
                getLastUpdated(),
                getConsentScreen(),
                getCustomPurposesConsent(),
                getCustomPurposesLITransparency(),
                getDisclosedVendors(),
                getPurposeOneTreatment(),
                isServiceSpecific(),
                getTcfPolicyVersion(),
                getPublisherCC(),
                getPubPurposesConsent(),
                getPubPurposesLITransparency(),
                getPublisherRestrictions(),
                getPurposesConsent(),
                getPurposesLITransparency(),
                getSpecialFeatureOptIns(),
                getUseNonStandardStacks(),
                getVendorConsent(),
                getVendorLegitimateInterest(),
                getVendorListVersion(),
                getVersion());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TCStringV2 other = (TCStringV2) obj;
        return Objects.equals(getAllowedVendors(), other.getAllowedVendors())
                && Objects.equals(getConsentLanguage(), other.getConsentLanguage())
                && getCmpId() == other.getCmpId()
                && getCmpVersion() == other.getCmpVersion()
                && Objects.equals(getCreated(), other.getCreated())
                && Objects.equals(getLastUpdated(), other.getLastUpdated())
                && getConsentScreen() == other.getConsentScreen()
                && Objects.equals(getCustomPurposesConsent(), other.getCustomPurposesConsent())
                && Objects.equals(getCustomPurposesLITransparency(),
                        other.getCustomPurposesLITransparency())
                && Objects.equals(getDisclosedVendors(), other.getDisclosedVendors())
                && getPurposeOneTreatment() == other.getPurposeOneTreatment()
                && isServiceSpecific() == other.isServiceSpecific()
                && getTcfPolicyVersion() == other.getTcfPolicyVersion()
                && Objects.equals(getPublisherCC(), other.getPublisherCC())
                && Objects.equals(getPubPurposesConsent(), other.getPubPurposesConsent())
                && Objects.equals(getPubPurposesLITransparency(), other.getPubPurposesLITransparency())
                && Objects.equals(getPublisherRestrictions(), other.getPublisherRestrictions())
                && Objects.equals(getPurposesConsent(), other.getPurposesConsent())
                && Objects.equals(getPurposesLITransparency(), other.getPurposesLITransparency())
                && Objects.equals(getSpecialFeatureOptIns(), other.getSpecialFeatureOptIns())
                && getUseNonStandardStacks() == other.getUseNonStandardStacks()
                && Objects.equals(getVendorConsent(), other.getVendorConsent())
                && Objects.equals(getVendorLegitimateInterest(), other.getVendorLegitimateInterest())
                && getVendorListVersion() == other.getVendorListVersion() && getVersion() == other.getVersion();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TCStringV2 [getVersion()=");
        builder.append(getVersion());
        builder.append(", getCreated()=");
        builder.append(getCreated());
        builder.append(", getLastUpdated()=");
        builder.append(getLastUpdated());
        builder.append(", getCmpId()=");
        builder.append(getCmpId());
        builder.append(", getCmpVersion()=");
        builder.append(getCmpVersion());
        builder.append(", getConsentScreen()=");
        builder.append(getConsentScreen());
        builder.append(", getConsentLanguage()=");
        builder.append(getConsentLanguage());
        builder.append(", getVendorListVersion()=");
        builder.append(getVendorListVersion());
        builder.append(", getTcfPolicyVersion()=");
        builder.append(getTcfPolicyVersion());
        builder.append(", isServiceSpecific()=");
        builder.append(isServiceSpecific());
        builder.append(", getUseNonStandardStacks()=");
        builder.append(getUseNonStandardStacks());
        builder.append(", getSpecialFeatureOptIns()=");
        builder.append(getSpecialFeatureOptIns());
        builder.append(", getPurposesConsent()=");
        builder.append(getPurposesConsent());
        builder.append(", getPurposesLITransparency()=");
        builder.append(getPurposesLITransparency());
        builder.append(", getPurposeOneTreatment()=");
        builder.append(getPurposeOneTreatment());
        builder.append(", getPublisherCC()=");
        builder.append(getPublisherCC());
        builder.append(", getVendorConsent()=");
        builder.append(getVendorConsent());
        builder.append(", getVendorLegitimateInterest()=");
        builder.append(getVendorLegitimateInterest());
        builder.append(", getPublisherRestrictions()=");
        builder.append(getPublisherRestrictions());
        builder.append(", getDisclosedVendors()=");
        builder.append(getDisclosedVendors());
        builder.append(", getAllowedVendors()=");
        builder.append(getAllowedVendors());
        builder.append(", getPubPurposesConsent()=");
        builder.append(getPubPurposesConsent());
        builder.append(", getPubPurposesLITransparency()=");
        builder.append(getPubPurposesLITransparency());
        builder.append(", getCustomPurposesConsent()=");
        builder.append(getCustomPurposesConsent());
        builder.append(", getCustomPurposesLITransparency()=");
        builder.append(getCustomPurposesLITransparency());
        builder.append("]");
        return builder.toString();
    }
}
