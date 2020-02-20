package com.iabtcf.v2;

import static com.iabtcf.v2.FieldConstants.CoreStringConstants.*;
import static com.iabtcf.v2.FieldConstants.Type.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import com.iabtcf.BitVector;

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

    private BitVectorTCModelV2(BitVector coreBitVector) {
        this.version = coreBitVector.readUnsignedInt(VERSION_OFFSET, TINY_INT.length());
        this.consentRecordCreated =
                coreBitVector.readInstantFromDeciSecond(CREATED_OFFSET, EPOCH_TIME.length());
        this.consentRecordLastUpdated =
                coreBitVector.readInstantFromDeciSecond(LAST_UPDATED_OFFSET, EPOCH_TIME.length());
        this.consentManagerProviderId = coreBitVector.readUnsignedInt(CMP_ID_OFFSET, SHORT.length());
        this.consentManagerProviderVersion =
                coreBitVector.readUnsignedInt(CMP_VERSION_OFFSET, SHORT.length());
        this.consentScreen = coreBitVector.readUnsignedInt(CONSENT_SCREEN_OFFSET, TINY_INT.length());
        this.consentLanguage = coreBitVector.readStr(CONSENT_LANGUAGE_OFFSET, CHAR.length() * 2);
        this.vendorListVersion =
                coreBitVector.readUnsignedInt(VENDOR_LIST_VERSION_OFFSET, SHORT.length());
        this.policyVersion = coreBitVector.readUnsignedInt(TCF_POLICY_VERSION_OFFSET, TINY_INT.length());
        this.isServiceSpecific = coreBitVector.readBit(IS_SERVICE_SPECIFIC_OFFSET);
        this.useNonStandardStacks = coreBitVector.readBit(USE_NON_STANDARD_STACKS_OFFSET);
        this.specialFeaturesOptInts =
                fillSet(SPECIAL_FEATURE_OPT_INS_OFFSET, SPECIAL_FEATURE_OPT_INS_LENGTH, coreBitVector);
        this.purposesConsent = fillSet(PURPOSES_CONSENT_OFFSET, PURPOSES_CONSENT_LENGTH, coreBitVector);
        this.purposesLITransparency =
                fillSet(PURPOSE_LI_TRANSPARENCY_OFFSET, PURPOSE_LI_TRANSPARENCY_LENGTH, coreBitVector);
        this.isPurposeOneTreatment = coreBitVector.readBit(PURPOSE_ONE_TREATMENT_OFFSET);
        this.publisherCountryCode = coreBitVector.readStr(PUBLISHER_CC_OFFSET, CHAR.length() * 2);

        int currentPointer = PUBLISHER_CC_OFFSET + (CHAR.length() * 2); // publisher cc offset
        this.vendorConsents = new TreeSet<>();
        currentPointer = this.fetchSet(this.vendorConsents, currentPointer, coreBitVector);

        this.vendorLegitimateInterests = new TreeSet<>();
        currentPointer = this.fetchSet(this.vendorLegitimateInterests, currentPointer, coreBitVector);

        this.publisherRestrictions = new ArrayList<>();
        this.fillPublisherRestrictions(publisherRestrictions, currentPointer, coreBitVector);

        this.disclosedVendors = new TreeSet<>();
        this.allowedVendors = new TreeSet<>();
        this.publisherPurposesConsent = new TreeSet<>();
        this.publisherPurposesLITransparency = new TreeSet<>();
        this.customPurposesConsent = new TreeSet<>();
        this.customPurposesLITransparency = new TreeSet<>();
    }

    private BitVectorTCModelV2(BitVector coreVector, BitVector... theRest) {
        this(coreVector);
        for (BitVector bitVector : theRest) {
            fillRemainingVector(bitVector);
        }
    }

    public static BitVectorTCModelV2 fromBitVector(
            BitVector coreBitVector, BitVector... remainingVectors) {
        return new BitVectorTCModelV2(coreBitVector, remainingVectors);
    }

    private int fillRemainingVector(BitVector bitVector) {
        int currentPointer = 0;
        int segmentType = bitVector.readUnsignedInt(SEGMENT_TYPE_OFFSET, 3);
        currentPointer += SEGMENT_TYPE_OFFSET + 3;
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

    private int fillPublisherPurposesTC(int currentPointer, BitVector bitVector) {
        this.publisherPurposesConsent.addAll(
                fillSet(currentPointer, PURPOSES_CONSENT_LENGTH, bitVector));
        currentPointer += PURPOSES_CONSENT_LENGTH;

        this.publisherPurposesLITransparency.addAll(
                fillSet(currentPointer, PURPOSE_LI_TRANSPARENCY_LENGTH, bitVector));
        currentPointer += PURPOSE_LI_TRANSPARENCY_LENGTH;

        int numberOfCustomPurposes = bitVector.readUnsignedInt(currentPointer, TINY_INT.length());
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

    private int fetchSet(Set<Integer> set, int currentPointer, BitVector bitVector) {
        int maxVendor = bitVector.readUnsignedInt(currentPointer, MEDIUM.length());
        currentPointer += MEDIUM.length();
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

    private int vendorIdsFromRange(
            Collection<Integer> vendorIds, BitVector bitVector, int currentPointer) {
        int numberOfVendorEntries = bitVector.readUnsignedInt(currentPointer, SHORT.length());
        currentPointer += SHORT.length();
        for (int j = 0; j < numberOfVendorEntries; j++) {
            boolean isRangeEntry = bitVector.readBit(currentPointer++);
            int startOrOnlyVendorId = bitVector.readUnsignedInt(currentPointer, MEDIUM.length());
            currentPointer += MEDIUM.length();
            if (isRangeEntry) {
                int endVendorId = bitVector.readUnsignedInt(currentPointer, MEDIUM.length());
                currentPointer += MEDIUM.length();
                IntStream.rangeClosed(startOrOnlyVendorId, endVendorId).forEach(vendorIds::add);
            } else {
                vendorIds.add(startOrOnlyVendorId);
            }
        }
        return currentPointer;
    }

    private int fillPublisherRestrictions(
            List<PublisherRestriction> publisherRestrictions, int currentPointer, BitVector bitVector) {

        int numberOfPublisherRestrictions = bitVector.readUnsignedInt(currentPointer, SHORT.length());
        currentPointer += SHORT.length();

        for (int i = 0; i < numberOfPublisherRestrictions; i++) {
            int purposeId = bitVector.readUnsignedInt(currentPointer, TINY_INT.length());
            currentPointer += TINY_INT.length();

            int restrictionTypeId = bitVector.readUnsignedInt(currentPointer, 2);
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

    private Set<Integer> fillSet(int offset, int length, BitVector bitVector) {
        Set<Integer> set = new TreeSet<>();
        for (int i = 0; i < length; i++) {
            if (bitVector.readBit(offset + i)) {
                set.add(i + 1);
            }
        }
        return set;
    }
}
