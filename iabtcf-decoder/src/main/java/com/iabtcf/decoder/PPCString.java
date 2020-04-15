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
import static com.iabtcf.utils.FieldDefs.V1_VENDOR_LIST_VERSION;
import static com.iabtcf.utils.FieldDefs.V1_VERSION;

import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

import com.iabtcf.exceptions.ByteParseException;
import com.iabtcf.exceptions.UnsupportedVersionException;
import com.iabtcf.utils.BitReader;
import com.iabtcf.utils.FieldDefs;
import com.iabtcf.utils.IntIterable;

/**
 * Parses TCFv1 Publisher Purposes Consent String Format
 */
public class PPCString {
    private final BitReader bbv;

    private PPCString(BitReader bitVector) {
        this.bbv = bitVector;
    }

    public static PPCString decode(String consentString)
            throws IllegalArgumentException, ByteParseException, UnsupportedVersionException {
        byte[] bytes = Base64.getUrlDecoder().decode(consentString);
        return new PPCString(new BitReader(bytes));
    }

    public int getVersion() {
        return bbv.readBits6(V1_VERSION);
    }

    public Instant getCreated() {
        return Instant.ofEpochMilli(bbv.readBits36(V1_CREATED) * 100);
    }

    public Instant getLastUpdated() {
        return Instant.ofEpochMilli(bbv.readBits36(V1_LAST_UPDATED) * 100);
    }

    public int getCmpId() {
        return bbv.readBits12(V1_CMP_ID);
    }

    public int getCmpVersion() {
        return bbv.readBits12(V1_CMP_VERSION);
    }

    public int getConsentScreen() {
        return bbv.readBits6(V1_CONSENT_SCREEN);
    }

    public String getConsentLanguage() {
        return bbv.readStr2(V1_CONSENT_LANGUAGE);
    }

    public int getVendorListVersion() {
        return bbv.readBits12(V1_VENDOR_LIST_VERSION);
    }

    public int getPublisherPurposesVersion() {
        return bbv.readBits12(FieldDefs.V1_PPC_PUBLISHER_PURPOSES_VERSION);
    }

    public IntIterable getStandardPurposesAllowed() {
        return TCStringV2.fillBitSet(bbv, FieldDefs.V1_PPC_STANDARD_PURPOSES_ALLOWED);
    }

    public IntIterable getCustomPurposesBitField() {
        return TCStringV2.fillBitSet(bbv, FieldDefs.V1_PPC_CUSTOM_PURPOSES_BITFIELD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getCreated(), getLastUpdated(), getCmpId(), getCmpVersion(),
                getConsentScreen(), getConsentLanguage(), getVendorListVersion(), getPublisherPurposesVersion(),
                getStandardPurposesAllowed(), getCustomPurposesBitField());
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
        PPCString other = (PPCString) obj;
        return getVersion() == other.getVersion()
                && Objects.equals(getCreated(), other.getCreated())
                && Objects.equals(getLastUpdated(), other.getLastUpdated())
                && getCmpId() == other.getCmpId()
                && getCmpVersion() == other.getCmpVersion()
                && getConsentScreen() == other.getConsentScreen()
                && Objects.equals(getConsentLanguage(), other.getConsentLanguage())
                && getVendorListVersion() == other.getVendorListVersion()
                && Objects.equals(getPublisherPurposesVersion(), other.getPublisherPurposesVersion())
                && Objects.equals(getStandardPurposesAllowed(), other.getStandardPurposesAllowed())
                && Objects.equals(getCustomPurposesBitField(), other.getCustomPurposesBitField());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PPCString [getVersion()=");
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
        builder.append(", getPublisherPurposesVersion()=");
        builder.append(getPublisherPurposesVersion());
        builder.append(", getStandardPurposesAllowed()=");
        builder.append(getStandardPurposesAllowed());
        builder.append(", getCustomPurposesBitField()=");
        builder.append(getCustomPurposesBitField());
        builder.append("]");
        return builder.toString();
    }
}
