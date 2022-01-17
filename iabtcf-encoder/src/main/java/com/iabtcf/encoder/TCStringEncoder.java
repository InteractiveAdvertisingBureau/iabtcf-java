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

import static com.iabtcf.encoder.Bounds.checkBounds;
import static com.iabtcf.encoder.Bounds.checkBoundsBits;
import static com.iabtcf.utils.FieldDefs.CORE_CMP_ID;
import static com.iabtcf.utils.FieldDefs.CORE_CMP_VERSION;
import static com.iabtcf.utils.FieldDefs.CORE_CONSENT_LANGUAGE;
import static com.iabtcf.utils.FieldDefs.CORE_CONSENT_SCREEN;
import static com.iabtcf.utils.FieldDefs.CORE_CREATED;
import static com.iabtcf.utils.FieldDefs.CORE_IS_SERVICE_SPECIFIC;
import static com.iabtcf.utils.FieldDefs.CORE_LAST_UPDATED;
import static com.iabtcf.utils.FieldDefs.CORE_PUBLISHER_CC;
import static com.iabtcf.utils.FieldDefs.CORE_PURPOSES_CONSENT;
import static com.iabtcf.utils.FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.utils.FieldDefs.CORE_PURPOSE_ONE_TREATMENT;
import static com.iabtcf.utils.FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS;
import static com.iabtcf.utils.FieldDefs.CORE_TCF_POLICY_VERSION;
import static com.iabtcf.utils.FieldDefs.CORE_USE_NON_STANDARD_STOCKS;
import static com.iabtcf.utils.FieldDefs.CORE_VENDOR_LIST_VERSION;
import static com.iabtcf.utils.FieldDefs.CORE_VERSION;
import static com.iabtcf.utils.FieldDefs.OOB_SEGMENT_TYPE;
import static com.iabtcf.utils.FieldDefs.PPTC_NUM_CUSTOM_PURPOSES;
import static com.iabtcf.utils.FieldDefs.PPTC_PUB_PURPOSES_CONSENT;
import static com.iabtcf.utils.FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.utils.FieldDefs.PPTC_SEGMENT_TYPE;
import static com.iabtcf.utils.FieldDefs.V1_CMP_ID;
import static com.iabtcf.utils.FieldDefs.V1_CMP_VERSION;
import static com.iabtcf.utils.FieldDefs.V1_CONSENT_LANGUAGE;
import static com.iabtcf.utils.FieldDefs.V1_CONSENT_SCREEN;
import static com.iabtcf.utils.FieldDefs.V1_CREATED;
import static com.iabtcf.utils.FieldDefs.V1_LAST_UPDATED;
import static com.iabtcf.utils.FieldDefs.V1_PURPOSES_ALLOW;
import static com.iabtcf.utils.FieldDefs.V1_VENDOR_LIST_VERSION;
import static com.iabtcf.utils.FieldDefs.V1_VERSION;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.iabtcf.decoder.TCString;
import com.iabtcf.encoder.exceptions.ValueOverflowException;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.SegmentType;

public interface TCStringEncoder {

    /**
     * Returns a base64 url encoded iabtcf compliant consent string
     *
     * @throws IllegalArgumentException if the version is invalid
     * @throws ValueOverflowException if an attempt was made to encode a value beyond it's limit.
     */
    String encode() throws IllegalArgumentException, ValueOverflowException;

    /**
     * Returns a TCString representation
     *
     * @throws IllegalArgumentException if the version is invalid
     * @throws ValueOverflowException if an attempt was made to encode a value beyond it's limit.
     */
    TCString toTCString() throws IllegalArgumentException, ValueOverflowException;

    static class TCStringEncoderV1 implements TCStringEncoder {
        private final int version;
        private final Instant created;
        private final Instant updated;
        private final int cmpId;
        private final int cmpVersion;
        private final int consentScreen;
        private final String consentLanguage;
        private final int vendorListVersion;
        private final IntIterable purposesConsent;
        private final IntIterable vendorsConsent;
        private final boolean defaultConsent;

        public TCStringEncoderV1(TCStringEncoder.Builder builder) {
            if (builder.version != 1) {
                throw new IllegalArgumentException("version must be 1: " + builder.version);
            }
            this.version = builder.version;
            this.created = builder.created;
            this.updated = builder.updated;
            this.cmpId = builder.cmpId;
            this.cmpVersion = builder.cmpVersion;
            this.consentScreen = builder.consentScreen;
            this.consentLanguage = builder.consentLanguage;
            this.vendorListVersion = builder.vendorListVersion;
            this.purposesConsent = builder.purposesConsent.build();
            this.vendorsConsent = builder.vendorConsent.build();
            this.defaultConsent = builder.defaultConsent;
        }

        @Override
        public String encode() {
            BitWriter bitWriter = new BitWriter();
            bitWriter.write(version, V1_VERSION);
            bitWriter.write(created, V1_CREATED);
            bitWriter.write(updated, V1_LAST_UPDATED);
            bitWriter.write(cmpId, V1_CMP_ID);
            bitWriter.write(cmpVersion, V1_CMP_VERSION);
            bitWriter.write(consentScreen, V1_CONSENT_SCREEN);
            bitWriter.write(consentLanguage, V1_CONSENT_LANGUAGE);
            bitWriter.write(vendorListVersion, V1_VENDOR_LIST_VERSION);
            bitWriter.write(purposesConsent, V1_PURPOSES_ALLOW);

            BitWriter vendorConsentBits = new VendorFieldEncoder()
                .defaultConsent(defaultConsent)
                .add(vendorsConsent)
                .buildV1();

            bitWriter.write(vendorConsentBits);

            return bitWriter.toBase64();
        }

        @Override
        public TCString toTCString() {
            return TCString.decode(encode());
        }
    }

    static class TCStringEncoderV2 implements TCStringEncoder {
        private final int version;
        private final Instant created;
        private final Instant updated;
        private final int cmpId;
        private final int cmpVersion;
        private final int consentScreen;
        private final String consentLanguage;
        private final int vendorListVersion;
        private final IntIterable purposesConsent;
        private final IntIterable vendorsConsent;
        private final int tcfPolicyVersion;
        private final boolean isServiceSpecific;
        private final boolean useNonStandardStacks;
        private final IntIterable specialFeatureOptIns;
        private final IntIterable purposesLITransparency;
        private final boolean purposeOneTreatment;
        private final String publisherCC;
        private final IntIterable vendorLegitimateInterest;
        private final IntIterable disclosedVendors;
        private final IntIterable allowedVendors;
        private final IntIterable pubPurposesConsent;
        private final int numberOfCustomPurposes;
        private final IntIterable customPurposesConsent;
        private final IntIterable customPurposesLITransparency;
        private final IntIterable pubPurposesLITransparency;
        private final List<PublisherRestrictionEntry> publisherRestrictions;

        /**
         * @throws IllegalArgumentException if the version is invalid
         * @throws ValueOverflowException
         */
        private TCStringEncoderV2(TCStringEncoder.Builder builder)
                throws IllegalArgumentException, ValueOverflowException {
            if (builder.version != 2) {
                throw new IllegalArgumentException("version must be 2: " + builder.version);
            }

            version = checkBounds(builder.version, FieldDefs.CORE_CMP_VERSION);
            created = Objects.requireNonNull(builder.created);
            updated = Objects.requireNonNull(builder.updated);
            cmpId = checkBounds(builder.cmpId, FieldDefs.CORE_CMP_ID);
            cmpVersion = checkBounds(builder.cmpVersion, FieldDefs.CORE_CMP_VERSION);
            consentScreen = checkBounds(builder.consentScreen, FieldDefs.CORE_CONSENT_SCREEN);
            consentLanguage = Objects.requireNonNull(builder.consentLanguage);
            vendorListVersion = checkBounds(builder.vendorListVersion, FieldDefs.CORE_VENDOR_LIST_VERSION);
            purposesConsent = checkBoundsBits(builder.purposesConsent, FieldDefs.CORE_PURPOSES_CONSENT).build();
            vendorsConsent = checkBounds(builder.vendorConsent, FieldDefs.CORE_VENDOR_MAX_VENDOR_ID).build();
            tcfPolicyVersion = checkBounds(builder.tcfPolicyVersion, FieldDefs.CORE_TCF_POLICY_VERSION);
            isServiceSpecific = builder.isServiceSpecific;
            useNonStandardStacks = builder.useNonStandardStacks;
            specialFeatureOptIns =
                    checkBoundsBits(builder.specialFeatureOptIns, FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS).build();
            purposesLITransparency =
                    checkBoundsBits(builder.purposesLITransparency, FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY).build();
            purposeOneTreatment = builder.purposeOneTreatment;
            publisherCC = Objects.requireNonNull(builder.publisherCC);
            vendorLegitimateInterest =
                    checkBounds(builder.vendorLegitimateInterest, FieldDefs.CORE_VENDOR_MAX_VENDOR_ID).build();
            disclosedVendors = checkBounds(builder.disclosedVendors, FieldDefs.CORE_VENDOR_MAX_VENDOR_ID).build();
            allowedVendors = checkBounds(builder.allowedVendors, FieldDefs.CORE_VENDOR_MAX_VENDOR_ID).build();
            pubPurposesLITransparency =
                    checkBoundsBits(builder.pubPurposesLITransparency, FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY)
                        .build();
            pubPurposesConsent =
                    checkBoundsBits(builder.pubPurposesConsent, FieldDefs.PPTC_PUB_PURPOSES_CONSENT).build();
            numberOfCustomPurposes =
                    checkBounds(
                            Math.max(builder.customPurposesLITransparency.max(), builder.customPurposesConsent.max()),
                            FieldDefs.PPTC_NUM_CUSTOM_PURPOSES);
            customPurposesLITransparency = builder.customPurposesLITransparency.build();
            customPurposesConsent = builder.customPurposesConsent.build();
            publisherRestrictions = checkBounds(new ArrayList<>(builder.publisherRestrictions));
        }

        private String encodeSegment(SegmentType segmentType) {
            IntIterable intIterable = BitSetIntIterable.EMPTY;

            switch (segmentType) {
                case DISCLOSED_VENDOR:
                    intIterable = disclosedVendors;
                    break;

                case ALLOWED_VENDOR:
                    intIterable = allowedVendors;
                    break;

                default:
                    throw new IllegalArgumentException("invalid segment type: " + segmentType);
            }

            if (intIterable.isEmpty()) {
                return "";
            }

            BitWriter bitWriter = new BitWriter();
            bitWriter.write(segmentType.value(), OOB_SEGMENT_TYPE);
            bitWriter.write(new VendorFieldEncoder().add(intIterable).build());

            return bitWriter.toBase64();
        }

        /**
         * Disclosed Vendors (OOB)
         */
        private String encodeDisclosedVendors() {
            return encodeSegment(SegmentType.DISCLOSED_VENDOR);
        }

        /**
         * Allowed Vendors (OOB) segment
         */
        private String encodeAllowedVendors() {
            return encodeSegment(SegmentType.ALLOWED_VENDOR);
        }

        private String encodeCoreString() {
            BitWriter bitWriter = new BitWriter();
            bitWriter.write(version, CORE_VERSION);
            bitWriter.writeDays(created, CORE_CREATED);
            bitWriter.writeDays(updated, CORE_LAST_UPDATED);
            bitWriter.write(cmpId, CORE_CMP_ID);
            bitWriter.write(cmpVersion, CORE_CMP_VERSION);
            bitWriter.write(consentScreen, CORE_CONSENT_SCREEN);
            bitWriter.write(consentLanguage, CORE_CONSENT_LANGUAGE);
            bitWriter.write(vendorListVersion, CORE_VENDOR_LIST_VERSION);
            bitWriter.write(tcfPolicyVersion, CORE_TCF_POLICY_VERSION);
            bitWriter.write(isServiceSpecific, CORE_IS_SERVICE_SPECIFIC);
            bitWriter.write(useNonStandardStacks, CORE_USE_NON_STANDARD_STOCKS);
            bitWriter.write(specialFeatureOptIns, CORE_SPECIAL_FEATURE_OPT_INS);
            bitWriter.write(purposesConsent, CORE_PURPOSES_CONSENT);
            bitWriter.write(purposesLITransparency, CORE_PURPOSES_LI_TRANSPARENCY);
            bitWriter.write(purposeOneTreatment, CORE_PURPOSE_ONE_TREATMENT);
            bitWriter.write(publisherCC, CORE_PUBLISHER_CC);
            bitWriter.write(new VendorFieldEncoder().add(vendorsConsent).build());
            bitWriter.write(new VendorFieldEncoder().add(vendorLegitimateInterest).build());

            bitWriter.write(publisherRestrictions.size(), FieldDefs.CORE_NUM_PUB_RESTRICTION);

            for (PublisherRestrictionEntry pre : publisherRestrictions) {
                bitWriter.write(pre.getPurposeId(), FieldDefs.PURPOSE_ID);
                bitWriter.write(pre.getRestrictionType().ordinal(), FieldDefs.RESTRICTION_TYPE);
                VendorFieldEncoder v = new VendorFieldEncoder()
                    .emitRangeEncoding(true)
                    .emitMaxVendorId(false)
                    .emitIsRangeEncoding(false)
                    .add(pre.getVendors());
                bitWriter.write(v.build());
            }

            return bitWriter.toBase64();
        }

        /**
         * Publisher Purposes Transparency and Consent segment
         */
        private String encodePPTC() {
            if (pubPurposesConsent.isEmpty() && pubPurposesLITransparency.isEmpty() && numberOfCustomPurposes == 0) {
                return "";
            }

            BitWriter bitWriter = new BitWriter();
            bitWriter.write(SegmentType.PUBLISHER_TC.value(), PPTC_SEGMENT_TYPE);
            bitWriter.write(pubPurposesConsent, PPTC_PUB_PURPOSES_CONSENT);
            bitWriter.write(pubPurposesLITransparency, PPTC_PUB_PURPOSES_LI_TRANSPARENCY);
            bitWriter.write(numberOfCustomPurposes, PPTC_NUM_CUSTOM_PURPOSES);
            bitWriter.write(customPurposesConsent, numberOfCustomPurposes);
            bitWriter.write(customPurposesLITransparency, numberOfCustomPurposes);

            return bitWriter.toBase64();
        }

        @Override
        public String encode() {
            return Stream
                .of(encodeCoreString(), encodeDisclosedVendors(), encodeAllowedVendors(), encodePPTC())
                .filter(str -> str != null && !str.isEmpty())
                .collect(Collectors.joining("."));
        }

        @Override
        public TCString toTCString() {
            return TCString.decode(encode());
        }
    }

    public static class Builder implements TCStringEncoder {
        private int version = 0;
        private Instant created = Instant.now(Clock.systemUTC());
        private Instant updated = created;
        private int cmpId = 0;
        private int cmpVersion = 0;
        private int consentScreen = 0;
        private String consentLanguage = "EN";
        private int vendorListVersion = 0;
        private BitSetIntIterable.Builder purposesConsent = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder vendorConsent = BitSetIntIterable.newBuilder();
        private int tcfPolicyVersion = 0;
        private boolean isServiceSpecific = false;
        private boolean useNonStandardStacks = false;
        private BitSetIntIterable.Builder specialFeatureOptIns = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder purposesLITransparency = BitSetIntIterable.newBuilder();
        private boolean purposeOneTreatment = false;
        private String publisherCC = "US";
        private BitSetIntIterable.Builder vendorLegitimateInterest = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder disclosedVendors = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder allowedVendors = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder pubPurposesConsent = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder customPurposesConsent = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder customPurposesLITransparency = BitSetIntIterable.newBuilder();
        private BitSetIntIterable.Builder pubPurposesLITransparency = BitSetIntIterable.newBuilder();
        private boolean defaultConsent = false;
        private final List<PublisherRestrictionEntry> publisherRestrictions = new ArrayList<>();

        private Builder() {
        }

        private Builder(TCStringEncoder.Builder prototype) {
            version = prototype.version;
            created = prototype.created;
            updated = prototype.updated;
            cmpId = prototype.cmpId;
            cmpVersion = prototype.cmpVersion;
            consentScreen = prototype.consentScreen;
            consentLanguage = prototype.consentLanguage;
            vendorListVersion = prototype.vendorListVersion;
            purposesConsent = prototype.purposesConsent;
            vendorConsent = prototype.vendorConsent;
            tcfPolicyVersion = prototype.tcfPolicyVersion;
            isServiceSpecific = prototype.isServiceSpecific;
            useNonStandardStacks = prototype.useNonStandardStacks;
            specialFeatureOptIns = prototype.specialFeatureOptIns;
            purposesLITransparency = prototype.purposesLITransparency;
            purposeOneTreatment = prototype.purposeOneTreatment;
            publisherCC = prototype.publisherCC;
            vendorLegitimateInterest = prototype.vendorLegitimateInterest;
            disclosedVendors = prototype.disclosedVendors;
            allowedVendors = prototype.allowedVendors;
        }

        public Builder(TCString tcString) {
            version = tcString.getVersion();
            created = tcString.getCreated();
            updated = tcString.getLastUpdated();
            cmpId = tcString.getCmpId();
            cmpVersion = tcString.getCmpVersion();
            consentScreen = tcString.getConsentScreen();
            consentLanguage = tcString.getConsentLanguage();
            vendorListVersion = tcString.getVendorListVersion();
            purposesConsent = BitSetIntIterable.newBuilder(tcString.getPurposesConsent());
            vendorConsent = BitSetIntIterable.newBuilder(tcString.getVendorConsent());

            if (version != 1) {
                tcfPolicyVersion = tcString.getTcfPolicyVersion();
                isServiceSpecific = tcString.isServiceSpecific();
                useNonStandardStacks = tcString.getUseNonStandardStacks();
                specialFeatureOptIns = BitSetIntIterable.newBuilder(tcString.getSpecialFeatureOptIns());
                purposesLITransparency = BitSetIntIterable.newBuilder(tcString.getPurposesLITransparency());
                purposeOneTreatment = tcString.getPurposeOneTreatment();
                publisherCC = tcString.getPublisherCC();
                vendorLegitimateInterest = BitSetIntIterable.newBuilder(tcString.getVendorLegitimateInterest());
                disclosedVendors = BitSetIntIterable.newBuilder(tcString.getDisclosedVendors());
                allowedVendors = BitSetIntIterable.newBuilder(tcString.getAllowedVendors());
            }
        }

        public Builder version(int version) throws IllegalArgumentException {
            this.version = validateVersion(version);
            return this;
        }

        /**
         * In V2, the encoded value will be rounded to the day.
         * It should also be the same value than {@link #lastUpdated}.
         */
        public Builder created(Instant created) {
            this.created = created;
            return this;
        }

        /**
         * In V2, the encoded value will be rounded to the day.
         */
        public Builder lastUpdated(Instant updated) {
            this.updated = updated;
            return this;
        }

        public Builder cmpId(int cmpId) {
            this.cmpId = cmpId;
            return this;
        }

        public Builder cmpVersion(int cmpVersion) {
            this.cmpVersion = cmpVersion;
            return this;
        }

        public Builder consentScreen(int consentScreen) {
            this.consentScreen = consentScreen;
            return this;
        }

        public Builder consentLanguage(String consentLanguage) throws IllegalArgumentException {
            this.consentLanguage = validateString(consentLanguage, FieldDefs.CORE_CONSENT_LANGUAGE);
            return this;
        }

        public Builder vendorListVersion(int version) {
            this.vendorListVersion = version;
            return this;
        }

        public Builder addPurposesConsent(int purposesConsent) {
            this.purposesConsent.add(purposesConsent);
            return this;
        }

        public Builder addPurposesConsent(IntIterable purposesConsent) {
            this.purposesConsent.add(purposesConsent);
            return this;
        }

        public Builder clearPurposesConsent() {
            purposesConsent.clear();
            return this;
        }

        public Builder addVendorConsent(int vendorsConsent) {
            this.vendorConsent.add(vendorsConsent);
            return this;
        }

        public Builder addVendorConsent(IntIterable vendorsConsent) {
            this.vendorConsent.add(vendorsConsent);
            return this;
        }

        public Builder clearVendorConsent() {
            this.vendorConsent.clear();
            return this;
        }

        public Builder tcfPolicyVersion(int tcfPolicyVersion) {
            this.tcfPolicyVersion = tcfPolicyVersion;
            return this;
        }

        public Builder isServiceSpecific(boolean isServiceSpecific) {
            this.isServiceSpecific = isServiceSpecific;
            return this;
        }

        public Builder useNonStandardStacks(boolean useNonStandardStacks) {
            this.useNonStandardStacks = useNonStandardStacks;
            return this;
        }

        public Builder addSpecialFeatureOptIns(int specialFeatureOptIns) {
            this.specialFeatureOptIns.add(specialFeatureOptIns);
            return this;
        }

        public Builder addSpecialFeatureOptIns(IntIterable specialFeatureOptIns) {
            this.specialFeatureOptIns.add(specialFeatureOptIns);
            return this;
        }

        public Builder clearSpecialFeatureOptIns() {
            this.specialFeatureOptIns.clear();
            return this;
        }

        public Builder addPurposesLITransparency(int purposesLITransparency) {
            this.purposesLITransparency.add(purposesLITransparency);
            return this;
        }

        public Builder addPurposesLITransparency(IntIterable purposesLITransparency) {
            this.purposesLITransparency.add(purposesLITransparency);
            return this;
        }

        public Builder clearPurposesLITransparency() {
            this.purposesLITransparency.clear();
            return this;
        }

        public Builder purposeOneTreatment(boolean purposeOneTreatment) {
            this.purposeOneTreatment = purposeOneTreatment;
            return this;
        }

        public Builder publisherCC(String publisherCC) {
            this.publisherCC = validateString(publisherCC, FieldDefs.CORE_PUBLISHER_CC);
            return this;
        }

        public Builder addVendorLegitimateInterest(int vendorLegitimateInterest) {
            this.vendorLegitimateInterest.add(vendorLegitimateInterest);
            return this;
        }

        public Builder addVendorLegitimateInterest(IntIterable vendorLegitimateInterest) {
            this.vendorLegitimateInterest.add(vendorLegitimateInterest);
            return this;
        }

        public Builder clearVendorLegitimateInterest() {
            this.vendorLegitimateInterest.clear();
            return this;
        }

        public Builder addDisclosedVendors(int disclosedVendors) {
            this.disclosedVendors.add(disclosedVendors);
            return this;
        }

        public Builder addDisclosedVendors(IntIterable disclosedVendors) {
            this.disclosedVendors.add(disclosedVendors);
            return this;
        }

        public Builder clearDisclosedVendors() {
            this.disclosedVendors.clear();
            return this;
        }

        public Builder addAllowedVendors(int allowedVendors) {
            this.allowedVendors.add(allowedVendors);
            return this;
        }

        public Builder addAllowedVendors(IntIterable allowedVendors) {
            this.allowedVendors.add(allowedVendors);
            return this;
        }

        public Builder clearAllowedVendors() {
            this.allowedVendors.clear();
            return this;
        }

        public Builder addPubPurposesConsent(int pubPurposesConsent) {
            this.pubPurposesConsent.add(pubPurposesConsent);
            return this;
        }

        public Builder addPubPurposesConsent(IntIterable pubPurposesConsent) {
            this.pubPurposesConsent.add(pubPurposesConsent);
            return this;
        }

        public Builder clearPubPurposesConsent() {
            this.pubPurposesConsent.clear();
            return this;
        }

        public Builder addCustomPurposesConsent(int customPurposesConsent) {
            this.customPurposesConsent.add(customPurposesConsent);
            return this;
        }

        public Builder addCustomPurposesConsent(IntIterable customPurposesConsent) {
            this.customPurposesConsent.add(customPurposesConsent);
            return this;
        }

        public Builder clearCustomPurposesConsent() {
            this.customPurposesConsent.clear();
            return this;
        }

        public Builder addCustomPurposesLITransparency(int customPurposesLITransparency) {
            this.customPurposesLITransparency.add(customPurposesLITransparency);
            return this;
        }

        public Builder addCustomPurposesLITransparency(IntIterable customPurposesLITransparency) {
            this.customPurposesLITransparency.add(customPurposesLITransparency);
            return this;
        }

        public Builder clearCustomPurposesLITransparency() {
            this.customPurposesLITransparency.clear();
            return this;
        }

        public Builder addPubPurposesLITransparency(int pubPurposesLITransparency) {
            this.pubPurposesLITransparency.add(pubPurposesLITransparency);
            return this;
        }

        public Builder addPubPurposesLITransparency(IntIterable pubPurposesLITransparency) {
            this.pubPurposesLITransparency.add(pubPurposesLITransparency);
            return this;
        }

        public Builder clearPubPurposesLITransparency() {
            this.pubPurposesLITransparency.clear();
            return this;
        }


        public Builder addPublisherRestrictionEntry(PublisherRestrictionEntry entry) {
            publisherRestrictions.add(entry);
            return this;
        }

        public Builder addPublisherRestrictionEntry(PublisherRestrictionEntry... entries) {
            for (int i = 0; i < entries.length; i++) {
                addPublisherRestrictionEntry(entries[i]);
            }
            return this;
        }

        public Builder addPublisherRestrictionEntry(Collection<PublisherRestrictionEntry> entries) {
            publisherRestrictions.addAll(entries);
            return this;
        }

        public Builder clearPublisherRestrictionEntry() {
            publisherRestrictions.clear();
            return this;
        }

        /**
         * For V1 range encoding, default consent for VendorIds not covered by a RangeEntry.
         * VendorIds covered by a RangeEntry have a consent value the opposite of DefaultConsent.
         *
         * Not used by V2 string.
         */
        public Builder defaultConsent(boolean defaultConsent) {
            this.defaultConsent = defaultConsent;
            return this;
        }

        @Override
        public String encode() {
            if (this.version == 1) {
                return new TCStringEncoderV1(this).encode();
            }

            return new TCStringEncoderV2(this).encode();
        }

        @Override
        public TCString toTCString() {
            return TCString.decode(encode());
        }

        private String validateString(String str, FieldDefs field) {
            if (str.length() != (field.getLength() / FieldDefs.CHAR.getLength())) {
                throw new IllegalArgumentException(str + " must be length 2 but is " + str.length());
            }

            return str.toUpperCase();
        }


        private int validateVersion(int version) {
            if (version < 1 || version > 2) {
                throw new IllegalArgumentException(version + " not supported");
            }

            return version;
        }
    }

    public static TCStringEncoder.Builder newBuilder() {
        return new TCStringEncoder.Builder();
    }

    public static TCStringEncoder.Builder newBuilder(TCStringEncoder.Builder tcStringEncoder) {
        return new TCStringEncoder.Builder(tcStringEncoder);
    }

    public static TCStringEncoder.Builder newBuilder(TCString tcString) {
        return new TCStringEncoder.Builder(tcString);
    }
}
