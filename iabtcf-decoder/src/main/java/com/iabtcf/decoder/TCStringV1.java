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

import static com.iabtcf.utils.FieldDefs.V1_CMP_ID;
import static com.iabtcf.utils.FieldDefs.V1_CMP_VERSION;
import static com.iabtcf.utils.FieldDefs.V1_CONSENT_LANGUAGE;
import static com.iabtcf.utils.FieldDefs.V1_CONSENT_SCREEN;
import static com.iabtcf.utils.FieldDefs.V1_CREATED;
import static com.iabtcf.utils.FieldDefs.V1_LAST_UPDATED;
import static com.iabtcf.utils.FieldDefs.V1_PURPOSES_ALLOW;
import static com.iabtcf.utils.FieldDefs.V1_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.utils.FieldDefs.V1_VENDOR_LIST_VERSION;
import static com.iabtcf.utils.FieldDefs.V1_VENDOR_MAX_VENDOR_ID;
import static com.iabtcf.utils.FieldDefs.V1_VERSION;

import java.time.Instant;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.iabtcf.exceptions.InvalidRangeFieldException;
import com.iabtcf.utils.BitReader;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.PublisherRestriction;

class TCStringV1 implements TCString {

    private final BitReader bbv;

    private TCStringV1(BitReader bitVector) {
        this.bbv = bitVector;
    }

    public static TCStringV1 fromBitVector(BitReader bitVector) {
        return new TCStringV1(bitVector);
    }

    @Override
    public int getVersion() {
        return bbv.readBits6(V1_VERSION);
    }

    @Override
    public Instant getCreated() {
        return Instant.ofEpochMilli(bbv.readBits36(V1_CREATED) * 100);
    }

    @Override
    public Instant getLastUpdated() {
        return Instant.ofEpochMilli(bbv.readBits36(V1_LAST_UPDATED) * 100);
    }

    @Override
    public int getCmpId() {
        return bbv.readBits12(V1_CMP_ID);
    }

    @Override
    public int getCmpVersion() {
        return bbv.readBits12(V1_CMP_VERSION);
    }

    @Override
    public int getConsentScreen() {
        return bbv.readBits6(V1_CONSENT_SCREEN);
    }

    @Override
    public String getConsentLanguage() {
        return bbv.readStr2(V1_CONSENT_LANGUAGE);
    }

    @Override
    public int getVendorListVersion() {
        return bbv.readBits12(V1_VENDOR_LIST_VERSION);
    }

    @Override
    public IntIterable getVendorConsent() {
        return fillVendorsV1(bbv, V1_VENDOR_MAX_VENDOR_ID, V1_VENDOR_BITRANGE_FIELD);
    }

    @Override
    public boolean getDefaultVendorConsent() {
        return bbv.readBits1(FieldDefs.V1_VENDOR_IS_RANGE_ENCODING)
                && bbv.readBits1(FieldDefs.V1_VENDOR_DEFAULT_CONSENT);
    }

    @Override
    public IntIterable getPurposesConsent() {
        return TCStringV2.fillBitSet(bbv, V1_PURPOSES_ALLOW);
    }

    @Override
    public int getTcfPolicyVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isServiceSpecific() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getUseNonStandardStacks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getSpecialFeatureOptIns() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getPurposesLITransparency() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getPurposeOneTreatment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPublisherCC() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getVendorLegitimateInterest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PublisherRestriction> getPublisherRestrictions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getAllowedVendors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getDisclosedVendors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getPubPurposesConsent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getPubPurposesLITransparency() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getCustomPurposesConsent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntIterable getCustomPurposesLITransparency() {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws InvalidRangeFieldException
     */
    private IntIterable fillVendorsV1(BitReader bbv, FieldDefs maxVendor, FieldDefs vendorField) {
        BitSet bs = new BitSet();

        int maxV = bbv.readBits16(maxVendor);
        boolean isRangeEncoding = bbv.readBits1(maxVendor.getEnd(bbv));

        if (isRangeEncoding) {
            boolean defaultConsent = bbv.readBits1(FieldDefs.V1_VENDOR_DEFAULT_CONSENT);
            TCStringV2.vendorIdsFromRange(bbv, bs, FieldDefs.V1_VENDOR_NUM_ENTRIES.getOffset(bbv),
                    Optional.of(maxVendor));

            if (defaultConsent) {
                bs.flip(1, maxV + 1);
            }
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

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getCreated(), getLastUpdated(), getCmpId(), getCmpVersion(),
                getConsentScreen(), getConsentLanguage(), getVendorListVersion(), getVendorConsent(),
                getDefaultVendorConsent(), getPurposesConsent());
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
        TCStringV1 other = (TCStringV1) obj;
        return getVersion() == other.getVersion()
                && Objects.equals(getCreated(), other.getCreated())
                && Objects.equals(getLastUpdated(), other.getLastUpdated())
                && getCmpId() == other.getCmpId()
                && getCmpVersion() == other.getCmpVersion()
                && getConsentScreen() == other.getConsentScreen()
                && Objects.equals(getConsentLanguage(), other.getConsentLanguage())
                && getVendorListVersion() == other.getVendorListVersion()
                && Objects.equals(getVendorConsent(), other.getVendorConsent())
                && getDefaultVendorConsent() == other.getDefaultVendorConsent()
                && Objects.equals(getPurposesConsent(), other.getPurposesConsent());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TCStringV1 [getVersion()=");
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
        builder.append(", getVendorConsent()=");
        builder.append(getVendorConsent());
        builder.append(", getDefaultVendorConsent()=");
        builder.append(getDefaultVendorConsent());
        builder.append(", getPurposesConsent()=");
        builder.append(getPurposesConsent());
        builder.append("]");
        return builder.toString();
    }
}
