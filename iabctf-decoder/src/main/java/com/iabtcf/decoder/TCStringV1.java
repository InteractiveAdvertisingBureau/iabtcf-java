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

import static com.iabtcf.FieldDefs.V1_CMP_ID;
import static com.iabtcf.FieldDefs.V1_CMP_VERSION;
import static com.iabtcf.FieldDefs.V1_CONSENT_LANGUAGE;
import static com.iabtcf.FieldDefs.V1_CONSENT_SCREEN;
import static com.iabtcf.FieldDefs.V1_CREATED;
import static com.iabtcf.FieldDefs.V1_LAST_UPDATED;
import static com.iabtcf.FieldDefs.V1_PURPOSES_ALLOW;
import static com.iabtcf.FieldDefs.V1_VENDOR_BITRANGE_FIELD;
import static com.iabtcf.FieldDefs.V1_VENDOR_LIST_VERSION;
import static com.iabtcf.FieldDefs.V1_VENDOR_MAX_VENDOR_ID;
import static com.iabtcf.FieldDefs.V1_VERSION;
import static com.iabtcf.utils.ByteBitVectorUtils.deciSeconds;
import static com.iabtcf.utils.ByteBitVectorUtils.readStr2;

import java.time.Instant;
import java.util.BitSet;
import java.util.List;

import com.iabtcf.ByteBitVector;
import com.iabtcf.FieldDefs;
import com.iabtcf.utils.BitSetIntIterable;
import com.iabtcf.utils.IntIterable;
import com.iabtcf.v2.PublisherRestriction;

class TCStringV1 implements TCString {

    private final ByteBitVector bbv;

    private TCStringV1(ByteBitVector bitVector) {
        this.bbv = bitVector;
    }

    public static TCStringV1 fromBitVector(ByteBitVector bitVector) {
        return new TCStringV1(bitVector);
    }

    @Override
    public int getVersion() {
        return bbv.readBits6(V1_VERSION);
    }

    @Override
    public Instant getCreated() {
        return deciSeconds(bbv, V1_CREATED);
    }

    @Override
    public Instant getLastUpdated() {
        return deciSeconds(bbv, V1_LAST_UPDATED);
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
        return readStr2(bbv, V1_CONSENT_LANGUAGE);
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

    private IntIterable fillVendorsV1(ByteBitVector bbv, FieldDefs maxVendor, FieldDefs vendorField) {
        BitSet bs = new BitSet();

        int maxV = bbv.readBits16(maxVendor);
        boolean isRangeEncoding = bbv.readBits1(maxVendor.getEnd(bbv));

        if (isRangeEncoding) {
            boolean defaultConsent = bbv.readBits1(FieldDefs.V1_VENDOR_DEFAULT_CONSENT);
            TCStringV2.vendorIdsFromRange(bbv, bs, FieldDefs.V1_VENDOR_NUM_ENTRIES.getOffset(bbv));

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

        return new BitSetIntIterable(bs);
    }
}
