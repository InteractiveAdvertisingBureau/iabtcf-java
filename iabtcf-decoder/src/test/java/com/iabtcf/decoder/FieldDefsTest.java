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

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Base64;

import org.junit.Test;

import com.iabtcf.decoder.SegmentInputStream;
import com.iabtcf.utils.BitReader;
import com.iabtcf.utils.FieldDefs;

public class FieldDefsTest {

    @Test
    public void testPPTCFields() {
        String tcString =
                "COtybn4PA_zT4KjACBENAPCIAEBAAECAAIAAAAAAAAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";

        SegmentInputStream sis = new SegmentInputStream(tcString, 0);
        InputStream is = Base64.getUrlDecoder().wrap(sis);
        BitReader bitVector = new BitReader(is);

        // read the fields twice
        for (int i = 0; i < 2; i++) {
            assertEquals(0, FieldDefs.PPTC_SEGMENT_TYPE.getOffset(bitVector));
            assertEquals(3, FieldDefs.PPTC_SEGMENT_TYPE.getLength(bitVector));

            assertEquals(0 + 3, FieldDefs.PPTC_PUB_PURPOSES_CONSENT.getOffset(bitVector));
            assertEquals(24, FieldDefs.PPTC_PUB_PURPOSES_CONSENT.getLength(bitVector));

            assertEquals(0 + 3 + 24, FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY.getOffset(bitVector));
            assertEquals(24, FieldDefs.PPTC_PUB_PURPOSES_LI_TRANSPARENCY.getLength(bitVector));

            assertEquals(0 + 3 + 24 + 24, FieldDefs.PPTC_NUM_CUSTOM_PURPOSES.getOffset(bitVector));
            assertEquals(6, FieldDefs.PPTC_NUM_CUSTOM_PURPOSES.getLength(bitVector));

            assertEquals(0 + 3 + 24 + 24 + 6, FieldDefs.PPTC_CUSTOM_PURPOSES_CONSENT.getOffset(bitVector));
            assertEquals(7, FieldDefs.PPTC_CUSTOM_PURPOSES_CONSENT.getLength(bitVector));

            assertEquals(0 + 3 + 24 + 24 + 6 + 7, FieldDefs.PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY.getOffset(bitVector));
            assertEquals(7, FieldDefs.PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY.getLength(bitVector));
        }
    }

    @Test
    public void testV1FieldsBitFIeld() {
        String tcString = "BOv5oL3Ov5oL3ABABBAAABAAAAABMAAA";

        SegmentInputStream sis = new SegmentInputStream(tcString, 0);
        InputStream is = Base64.getUrlDecoder().wrap(sis);
        BitReader bitVector = new BitReader(is);

        assertEquals(6, FieldDefs.V1_VERSION.getLength(bitVector));
        assertEquals(0, FieldDefs.V1_VERSION.getOffset(bitVector));
        assertEquals(36, FieldDefs.V1_CREATED.getLength(bitVector));
        assertEquals(6, FieldDefs.V1_CREATED.getOffset(bitVector));
        assertEquals(36, FieldDefs.V1_LAST_UPDATED.getLength(bitVector));
        assertEquals(42, FieldDefs.V1_LAST_UPDATED.getOffset(bitVector));
        assertEquals(12, FieldDefs.V1_CMP_ID.getLength(bitVector));
        assertEquals(78, FieldDefs.V1_CMP_ID.getOffset(bitVector));
        assertEquals(12, FieldDefs.V1_CMP_VERSION.getLength(bitVector));
        assertEquals(90, FieldDefs.V1_CMP_VERSION.getOffset(bitVector));
        assertEquals(6, FieldDefs.V1_CONSENT_SCREEN.getLength(bitVector));
        assertEquals(102, FieldDefs.V1_CONSENT_SCREEN.getOffset(bitVector));
        assertEquals(12, FieldDefs.V1_CONSENT_LANGUAGE.getLength(bitVector));
        assertEquals(108, FieldDefs.V1_CONSENT_LANGUAGE.getOffset(bitVector));
        assertEquals(12, FieldDefs.V1_VENDOR_LIST_VERSION.getLength(bitVector));
        assertEquals(120, FieldDefs.V1_VENDOR_LIST_VERSION.getOffset(bitVector));
        assertEquals(24, FieldDefs.V1_PURPOSES_ALLOW.getLength(bitVector));
        assertEquals(132, FieldDefs.V1_PURPOSES_ALLOW.getOffset(bitVector));
        assertEquals(16, FieldDefs.V1_VENDOR_MAX_VENDOR_ID.getLength(bitVector));
        assertEquals(156, FieldDefs.V1_VENDOR_MAX_VENDOR_ID.getOffset(bitVector));
        assertEquals(1, FieldDefs.V1_VENDOR_IS_RANGE_ENCODING.getLength(bitVector));
        assertEquals(172, FieldDefs.V1_VENDOR_IS_RANGE_ENCODING.getOffset(bitVector));
        assertEquals(1, FieldDefs.V1_VENDOR_DEFAULT_CONSENT.getLength(bitVector));
        assertEquals(173, FieldDefs.V1_VENDOR_BITRANGE_FIELD.getOffset(bitVector));
        assertEquals(19, FieldDefs.V1_VENDOR_BITRANGE_FIELD.getLength(bitVector));
    }

    @Test
    public void testV1FieldsRangeFIeld() {
        // 19 total range entries, 2 with start/end ranges
        String tcString =
                "BOv5oL3Ov5oL3ABABBAAABAAAAABOATAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        SegmentInputStream sis = new SegmentInputStream(tcString, 0);
        InputStream is = Base64.getUrlDecoder().wrap(sis);
        BitReader bitVector = new BitReader(is);

        assertEquals(1, FieldDefs.V1_VENDOR_DEFAULT_CONSENT.getLength(bitVector));
        assertEquals(173, FieldDefs.V1_VENDOR_BITRANGE_FIELD.getOffset(bitVector));
        assertEquals(12 + (17 * (1 + 16)) + (2 * (1 + 16 + 16)),
                FieldDefs.V1_VENDOR_BITRANGE_FIELD.getLength(bitVector));
    }
}
