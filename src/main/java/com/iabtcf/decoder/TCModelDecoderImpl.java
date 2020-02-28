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

import java.io.InputStream;
import java.util.Base64;

import com.iabtcf.ByteBitVector;
import com.iabtcf.SegmentInputStream;
import com.iabtcf.model.TCModel;
import com.iabtcf.v2.BitVectorTCModelV2;
import com.iabtcf.v2.FieldConstants;

public class TCModelDecoderImpl implements TCModelDecoder {

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @Override
    public TCModel decode(String consentString) {
        String[] split = consentString.split("\\.");
        String base64UrlEncodedString = split[0];
        ByteBitVector bitVector = vectorFromString(base64UrlEncodedString);

        int version = bitVector.readBits6(FieldConstants.CoreStringConstants.VERSION_OFFSET);

        switch (version) {
            case 1:
                // TODO : add version1
                throw new UnsupportedOperationException("Version 1 is unsupported yet");
            case 2:
                if (split.length > 1) {
                    String secondPartBase64 = split[1];
                    ByteBitVector secondPartBitVector = vectorFromString(secondPartBase64);
                    if (split.length > 2) {
                        String thirdPartBase64 = split[2];
                        ByteBitVector thirdPartBitVector = vectorFromString(thirdPartBase64);
                        return BitVectorTCModelV2.fromBitVector(
                                bitVector, secondPartBitVector, thirdPartBitVector);
                    } else {
                        return BitVectorTCModelV2.fromBitVector(bitVector, secondPartBitVector);
                    }
                } else {
                    return BitVectorTCModelV2.fromBitVector(bitVector);
                }
            default:
                throw new UnsupportedOperationException("Version " + version + "is unsupported yet");
        }
    }

    private ByteBitVector vectorFromString(String base64UrlEncodedString) {
        SegmentInputStream sis = new SegmentInputStream(base64UrlEncodedString, 0);
        InputStream is = DECODER.wrap(sis);

        return new ByteBitVector(is);
    }
}
