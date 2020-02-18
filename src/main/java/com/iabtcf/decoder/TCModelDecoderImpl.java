package com.iabtcf.decoder;

import java.util.Base64;

import com.iabtcf.BitVector;
import com.iabtcf.model.TCModel;
import com.iabtcf.v2.BitVectorTCModelV2;
import com.iabtcf.v2.FieldConstants;

public class TCModelDecoderImpl implements TCModelDecoder {

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @Override
    public TCModel decode(String consentString) {
        String[] split = consentString.split("\\.");
        String base64UrlEncodedString = split[0];
        BitVector bitVector = vectorFromString(base64UrlEncodedString);

        int version =
                bitVector.readUnsignedInt(
                        FieldConstants.CoreStringConstants.VERSION_OFFSET,
                        FieldConstants.Type.TINY_INT.length());
        switch (version) {
            case 1:
                // TODO : add version1
                throw new UnsupportedOperationException("Version 1 is unsupported yet");
            case 2:
                if (split.length > 1) {
                    String secondPartBase64 = split[1];
                    BitVector secondPartBitVector = vectorFromString(secondPartBase64);
                    if (split.length > 2) {
                        String thirdPartBase64 = split[2];
                        BitVector thirdPartBitVector = vectorFromString(thirdPartBase64);
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

    private BitVector vectorFromString(String base64UrlEncodedString) {
        byte[] bytes = DECODER.decode(base64UrlEncodedString);
        return BitVector.from(bytes);
    }
}
