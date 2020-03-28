package com.iabtcf.encoder;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance  the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * OUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
import static com.iabtcf.FieldDefs.CORE_PUBLISHER_CC;
import static com.iabtcf.FieldDefs.CORE_PURPOSES_CONSENT;
import static com.iabtcf.FieldDefs.CORE_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.FieldDefs.CORE_PURPOSE_ONE_TREATMENT;
import static com.iabtcf.FieldDefs.CORE_SPECIAL_FEATURE_OPT_INS;
import static com.iabtcf.FieldDefs.CORE_TCF_POLICY_VERSION;
import static com.iabtcf.FieldDefs.CORE_USE_NON_STANDARD_STOCKS;
import static com.iabtcf.FieldDefs.CORE_VENDOR_LIST_VERSION;
import static com.iabtcf.FieldDefs.CORE_VERSION;
import static com.iabtcf.FieldDefs.OOB_SEGMENT_TYPE;
import static com.iabtcf.FieldDefs.PPTC_NUM_CUSTOM_PURPOSES;
import static com.iabtcf.FieldDefs.PPTC_PUB_PURPOSES_CONSENT;
import static com.iabtcf.FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY;
import static com.iabtcf.FieldDefs.PPTC_SEGMENT_TYPE;
import static com.iabtcf.FieldDefs.V1_CMP_ID;
import static com.iabtcf.FieldDefs.V1_CMP_VERSION;
import static com.iabtcf.FieldDefs.V1_CONSENT_LANGUAGE;
import static com.iabtcf.FieldDefs.V1_CONSENT_SCREEN;
import static com.iabtcf.FieldDefs.V1_CREATED;
import static com.iabtcf.FieldDefs.V1_LAST_UPDATED;
import static com.iabtcf.FieldDefs.V1_PURPOSES_ALLOW;
import static com.iabtcf.FieldDefs.V1_VENDOR_LIST_VERSION;
import static com.iabtcf.FieldDefs.V1_VERSION;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.iabtcf.FieldDefs;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.SegmentType;

public interface TCStringEncoder {

    String encode();

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
            this.purposesConsent = builder.purposesConsent;
            this.vendorsConsent = builder.vendorsConsent;
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

        private TCStringEncoderV2(TCStringEncoder.Builder builder) {
            if (builder.version != 2) {
                throw new IllegalArgumentException("version must be 2: " + builder.version);
            }

            version = builder.version;
            created = Objects.requireNonNull(builder.created);
            updated = Objects.requireNonNull(builder.updated);
            cmpId = builder.cmpId;
            cmpVersion = builder.cmpVersion;
            consentScreen = builder.consentScreen;
            consentLanguage = Objects.requireNonNull(builder.consentLanguage);
            vendorListVersion = builder.vendorListVersion;
            purposesConsent = Objects.requireNonNull(builder.purposesConsent);
            vendorsConsent = Objects.requireNonNull(builder.vendorsConsent);
            tcfPolicyVersion = builder.tcfPolicyVersion;
            isServiceSpecific = builder.isServiceSpecific;
            useNonStandardStacks = builder.useNonStandardStacks;
            specialFeatureOptIns = Objects.requireNonNull(builder.specialFeatureOptIns);
            purposesLITransparency = Objects.requireNonNull(builder.purposesLITransparency);
            purposeOneTreatment = builder.purposeOneTreatment;
            publisherCC = Objects.requireNonNull(builder.publisherCC);
            vendorLegitimateInterest = Objects.requireNonNull(builder.vendorLegitimateInterest);
            disclosedVendors = Objects.requireNonNull(builder.disclosedVendors);
            allowedVendors = Objects.requireNonNull(builder.allowedVendors);
            pubPurposesLITransparency = Objects.requireNonNull(builder.pubPurposesLITransparency);
            numberOfCustomPurposes = builder.numberOfCustomPurposes;
            pubPurposesConsent = Objects.requireNonNull(builder.pubPurposesConsent);
            customPurposesLITransparency = Objects.requireNonNull(builder.customPurposesLITransparency);
            customPurposesConsent = Objects.requireNonNull(builder.customPurposesConsent);
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
            bitWriter.write(created, CORE_CREATED);
            bitWriter.write(updated, CORE_LAST_UPDATED);
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

            return bitWriter.toBase64();
        }

        /**
         * Publisher Purposes Transparency and Consent segment
         */
        private String encodePPTC() {
            if (pubPurposesConsent.isEmpty()) {
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
        private IntIterable purposesConsent = BitSetIntIterable.EMPTY;
        private IntIterable vendorsConsent = BitSetIntIterable.EMPTY;
        private int tcfPolicyVersion = 0;
        private boolean isServiceSpecific = false;
        private boolean useNonStandardStacks = false;
        private IntIterable specialFeatureOptIns = BitSetIntIterable.EMPTY;
        private IntIterable purposesLITransparency = BitSetIntIterable.EMPTY;
        private boolean purposeOneTreatment = false;
        private String publisherCC = "US";
        private IntIterable vendorLegitimateInterest = BitSetIntIterable.EMPTY;
        private IntIterable disclosedVendors = BitSetIntIterable.EMPTY;
        private IntIterable allowedVendors = BitSetIntIterable.EMPTY;
        private IntIterable pubPurposesConsent = BitSetIntIterable.EMPTY;
        private int numberOfCustomPurposes = 0;
        private IntIterable customPurposesConsent = BitSetIntIterable.EMPTY;
        private IntIterable customPurposesLITransparency = BitSetIntIterable.EMPTY;
        private IntIterable pubPurposesLITransparency = BitSetIntIterable.EMPTY;
        private boolean defaultConsent = false;

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
            vendorsConsent = prototype.vendorsConsent;
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

        public Builder version(int version) {
            this.version = validateVersion(version);
            return this;
        }

        public Builder created(Instant created) {
            this.created = created;
            return this;
        }

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

        public Builder consentLanguage(String consentLanguage) {
            this.consentLanguage = validateString(consentLanguage, FieldDefs.CORE_CONSENT_LANGUAGE);
            return this;
        }

        public Builder vendorListVersion(int version) {
            this.vendorListVersion = version;
            return this;
        }

        public Builder purposesConsent(IntIterable purposesConsent) {
            this.purposesConsent = purposesConsent;
            return this;
        }

        public Builder vendorsConsent(IntIterable vendorsConsent) {
            this.vendorsConsent = vendorsConsent;
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

        public Builder specialFeatureOptIns(IntIterable specialFeatureOptIns) {
            this.specialFeatureOptIns = specialFeatureOptIns;
            return this;
        }

        public Builder purposesLITransparency(IntIterable purposesLITransparency) {
            this.purposesLITransparency = purposesLITransparency;
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

        public Builder vendorLegitimateInterest(IntIterable vendorLegitimateInterest) {
            this.vendorLegitimateInterest = vendorLegitimateInterest;
            return this;
        }

        public Builder disclosedVendors(IntIterable disclosedVendors) {
            this.disclosedVendors = disclosedVendors;
            return this;
        }

        public Builder allowedVendors(IntIterable allowedVendors) {
            this.allowedVendors = allowedVendors;
            return this;
        }

        public Builder pubPurposesConsent(IntIterable pubPurposesConsent) {
            this.pubPurposesConsent = pubPurposesConsent;
            return this;
        }

        public Builder pubPurposesLITransparency(IntIterable pubPurposesLITransparency) {
            this.pubPurposesLITransparency = pubPurposesLITransparency;
            return this;
        }

        public Builder numberOfCustomPurposesConsent(int numberOfCustomPurposes) {
            this.numberOfCustomPurposes = numberOfCustomPurposes;
            return this;
        }

        public Builder customPurposesConsent(IntIterable customPurposesConsent) {
            this.customPurposesConsent = customPurposesConsent;
            return this;
        }

        public Builder customPurposesLITransparency(IntIterable customPurposesLITransparency) {
            this.customPurposesLITransparency = customPurposesLITransparency;
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
}
