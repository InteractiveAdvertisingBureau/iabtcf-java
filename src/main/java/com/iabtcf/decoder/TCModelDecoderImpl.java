package com.iabtcf.decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import com.iabtcf.BitVector;
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
        SegmentInputStream sis = new SegmentInputStream(base64UrlEncodedString, 0);
        InputStream is = DECODER.wrap(sis);

        byte[] bytes = toByteArray(is, base64UrlEncodedString.length());

        return BitVector.from(bytes);
    }

    private byte[] toByteArray(InputStream is, int lengthHint) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(32, lengthHint));

        try {
            byte[] buffer = new byte[4096];
            int n = 0;

            while ((n = is.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }
}
