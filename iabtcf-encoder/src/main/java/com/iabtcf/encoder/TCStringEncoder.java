package com.iabtcf.encoder;

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

import java.time.Instant;
import java.util.BitSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.iabtcf.utils.IntIterable;
import com.iabtcf.utils.IntIterableUtils;
import com.iabtcf.utils.IntIterator;
import com.iabtcf.v2.SegmentType;

public class TCStringEncoder {

    private int version;
    private Instant created;
    private Instant updated;
    private int cmpId;
    private int cmpVersion;
    private int consentScreen;
    private String consentLanguage;
    private int vendorListVersion;
    private IntIterable purposesConsent;
    private IntIterable vendorsConsent;
    private int tcfPolicyVersion;
    private boolean isServiceSpecific;
    private boolean useNonStandardStacks;
    private IntIterable specialFeatureOptIns;
    private IntIterable purposesLITransparency;
    private boolean purposeOneTreatment;
    private String publisherCC;
    private IntIterable vendorLegitimateInterest;
    private IntIterable disclosedVendors;
    private IntIterable allowedVendors;
    private IntIterable pubPurposesConsent;
    private int numberOfCustomPurposes;
    private IntIterable customPurposesConsent;
    private IntIterable customPurposesLITransparency;
    private IntIterable pubPurposesLITransparency;

    private static final BitSet EMPTY_BIT_SET = new BitSet();

    private TCStringEncoder() {

    }

    public static TCStringEncoder newBuilder() {
        return new TCStringEncoder();
    }

    public static TCStringEncoder newBuilder(TCStringEncoder tcStringEncoder) {
        TCStringEncoder encoder = new TCStringEncoder();
        encoder.version = tcStringEncoder.version;
        encoder.created = tcStringEncoder.created;
        encoder.updated = tcStringEncoder.updated;
        encoder.cmpId = tcStringEncoder.cmpId;
        encoder.cmpVersion = tcStringEncoder.cmpVersion;
        encoder.consentScreen = tcStringEncoder.consentScreen;
        encoder.consentLanguage = tcStringEncoder.consentLanguage;
        encoder.vendorListVersion = tcStringEncoder.vendorListVersion;
        encoder.purposesConsent = tcStringEncoder.purposesConsent;
        encoder.vendorsConsent = tcStringEncoder.vendorsConsent;
        encoder.tcfPolicyVersion = tcStringEncoder.tcfPolicyVersion;
        encoder.isServiceSpecific = tcStringEncoder.isServiceSpecific;
        encoder.useNonStandardStacks = tcStringEncoder.useNonStandardStacks;
        encoder.specialFeatureOptIns = tcStringEncoder.specialFeatureOptIns;
        encoder.purposesLITransparency = tcStringEncoder.purposesLITransparency;
        encoder.purposeOneTreatment = tcStringEncoder.purposeOneTreatment;
        encoder.publisherCC = tcStringEncoder.publisherCC;
        encoder.vendorLegitimateInterest = tcStringEncoder.vendorLegitimateInterest;
        encoder.disclosedVendors = tcStringEncoder.disclosedVendors;
        encoder.allowedVendors = tcStringEncoder.allowedVendors;
        return encoder;
    }

    public TCStringEncoder withVersion(int version) {
        if (version < 1 || version > 2) {
            throw new IllegalArgumentException(version + " not supported");
        }
        this.version = version;
        return this;
    }

    public TCStringEncoder withCreated(Instant created) {
        this.created = created;
        return this;
    }

    public TCStringEncoder withLastUpdated(Instant updated) {
        this.updated = updated;
        return this;
    }

    public TCStringEncoder withCmpId(int cmpId) {
        this.cmpId = cmpId;
        return this;
    }

    public TCStringEncoder withCmpVersion(int cmpVersion) {
        this.cmpVersion = cmpVersion;
        return this;
    }

    public TCStringEncoder withConsentScreen(int consentScreen) {
        this.consentScreen = consentScreen;
        return this;
    }

    public TCStringEncoder withConsentLanguage(String consentLanguage) {
        validateStr(consentLanguage);
        this.consentLanguage = consentLanguage;
        return this;
    }

    public TCStringEncoder withVendorListVersion(int version) {
        this.vendorListVersion = version;
        return this;
    }

    public TCStringEncoder withPurposesConsent(IntIterable purposesConsent) {
        this.purposesConsent = purposesConsent;
        return this;
    }

    public TCStringEncoder withVendorsConsent(IntIterable vendorsConsent) {
        this.vendorsConsent = vendorsConsent;
        return this;
    }

    private void validateStr(String str) {
        if (str.length() > 2) {
            throw new IllegalArgumentException(str + " length is greater than 2, found " + str.length());
        }
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                throw new IllegalArgumentException("lowercase characters not allowed");
            }
        }
    }

    public TCStringEncoder withTcfPolicyVersion(int tcfPolicyVersion) {
        this.tcfPolicyVersion = tcfPolicyVersion;
        return this;
    }

    public TCStringEncoder withIsServiceSpecific(boolean isServiceSpecific) {
        this.isServiceSpecific = isServiceSpecific;
        return this;
    }

    public TCStringEncoder withUseNonStandardStacks(boolean useNonStandardStacks) {
        this.useNonStandardStacks = useNonStandardStacks;
        return this;
    }

    public TCStringEncoder withSpecialFeatureOptIns(IntIterable specialFeatureOptIns) {
        this.specialFeatureOptIns = specialFeatureOptIns;
        return this;
    }

    public TCStringEncoder withPurposesLITransparency(IntIterable purposesLITransparency) {
        this.purposesLITransparency = purposesLITransparency;
        return this;
    }

    public TCStringEncoder withPurposeOneTreatment(boolean purposeOneTreatment) {
        this.purposeOneTreatment = purposeOneTreatment;
        return this;
    }

    public TCStringEncoder withPublisherCC(String publisherCC) {
        this.publisherCC = publisherCC;
        return this;
    }

    public TCStringEncoder withVendorLegitimateInterest(IntIterable vendorLegitimateInterest) {
        this.vendorLegitimateInterest = vendorLegitimateInterest;
        return this;
    }

    public TCStringEncoder withDisclosedVendors(IntIterable disclosedVendors) {
        this.disclosedVendors = disclosedVendors;
        return this;
    }

    public TCStringEncoder withAllowedVendors(IntIterable allowedVendors) {
        this.allowedVendors = allowedVendors;
        return this;
    }

    public TCStringEncoder withPubPurposesConsent(IntIterable pubPurposesConsent) {
        this.pubPurposesConsent = pubPurposesConsent;
        return this;
    }

    public TCStringEncoder withPubPurposesLITransparency(IntIterable pubPurposesLITransparency) {
        this.pubPurposesLITransparency = pubPurposesLITransparency;
        return this;
    }

    public TCStringEncoder withNumberOfCustomPurposesConsent(int numberOfCustomPurposes) {
        this.numberOfCustomPurposes = numberOfCustomPurposes;
        return this;
    }

    public TCStringEncoder withCustomPurposesConsent(IntIterable customPurposesConsent) {
        this.customPurposesConsent = customPurposesConsent;
        return this;
    }

    public TCStringEncoder withCustomPurposesLITransparency(IntIterable customPurposesLITransparency) {
        this.customPurposesLITransparency = customPurposesLITransparency;
        return this;
    }

    public String toTCFFormat() {
        if (this.version == 1) {
            return encodeVersion1();
        } else if (this.version == 2) {
            return Stream.of(encodeCoreString(), encodeDisclosedVendors(), encodeAllowedVendors(), encodePubPurposesConsent())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

        }
        throw new UnsupportedOperationException(this.version + " is unsupported");
    }

    private String encodeVersion1() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(this.version, 6); // version
        bitWriter.writeInstant(this.created);
        bitWriter.writeInstant(this.updated);
        bitWriter.write(this.cmpId, 12);
        bitWriter.write(this.cmpVersion, 12);
        bitWriter.write(this.consentScreen, 6);
        bitWriter.writeStr(this.consentLanguage);
        bitWriter.write(this.vendorListVersion, 12);
        bitWriter.write(bitSet(this.purposesConsent), 24);
        writeBitField(bitWriter, this.vendorsConsent);
        return bitWriter.toBase64();
    }

    private void writeBitField(BitWriter bitWriter, IntIterable intIterable) {
        int max = max(intIterable);
        bitWriter.write(max, 16);
        bitWriter.writeBit(false); // bit field encoding
        bitWriter.write(bitSet(intIterable), max);
    }

    private String encodeCoreString() {
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(this.version, 6); // version
        bitWriter.writeInstant(this.created);
        bitWriter.writeInstant(this.updated);
        bitWriter.write(this.cmpId, 12);
        bitWriter.write(this.cmpVersion, 12);
        bitWriter.write(this.consentScreen, 6);
        bitWriter.writeStr(this.consentLanguage);
        bitWriter.write(this.vendorListVersion, 12);
        bitWriter.write(this.tcfPolicyVersion, 6);
        bitWriter.writeBit(this.isServiceSpecific);
        bitWriter.writeBit(this.useNonStandardStacks);
        bitWriter.write(bitSet(this.specialFeatureOptIns), 12);
        bitWriter.write(bitSet(this.purposesConsent), 24);
        bitWriter.write(bitSet(this.purposesLITransparency), 24);
        bitWriter.writeBit(this.purposeOneTreatment);
        bitWriter.writeStr(this.publisherCC);
        writeBitField(bitWriter, this.vendorsConsent);
        writeBitField(bitWriter, this.vendorLegitimateInterest);
        return bitWriter.toBase64();
    }

    private String encodeOptional(SegmentType segmentType, IntIterable intIterable) {
        if (intIterable == null) {
            return null;
        }
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(segmentType.value(), 3);
        writeBitField(bitWriter, intIterable);
        return bitWriter.toBase64();
    }

    private String encodeDisclosedVendors() {
        return encodeOptional(SegmentType.DISCLOSED_VENDOR, this.disclosedVendors);
    }

    private String encodeAllowedVendors() {
        return encodeOptional(SegmentType.ALLOWED_VENDOR, this.allowedVendors);
    }

    private String encodePubPurposesConsent() {
        if (this.pubPurposesConsent == null) {
            return null;
        }
        BitWriter bitWriter = new BitWriter();
        bitWriter.write(SegmentType.PUBLISHER_TC.value(), 3);
        bitWriter.write(bitSet(this.pubPurposesConsent), 24);
        bitWriter.write(bitSet(this.pubPurposesLITransparency), 24);
        bitWriter.write(this.numberOfCustomPurposes, 6);
        bitWriter.write(bitSet(this.customPurposesConsent), this.numberOfCustomPurposes);
        bitWriter.write(bitSet(this.customPurposesLITransparency), this.numberOfCustomPurposes);

        return bitWriter.toBase64();
    }

    private BitSet bitSet(IntIterable integers) {
        if (integers == null) {
            return EMPTY_BIT_SET;
        }
        IntIterator iterator = integers.intIterator();
        BitSet bitSet = new BitSet();
        while (iterator.hasNext()) {
            bitSet.set(iterator.next() - 1);
        }
        return bitSet;
    }

    private int max(IntIterable integers) {
        return IntIterableUtils.toStream(integers)
                .max().orElse(0);
    }
}
