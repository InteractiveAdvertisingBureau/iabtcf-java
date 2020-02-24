package com.iabtcf.decoder;

import com.iabtcf.GDPRTransparencyAndConsent;

import java.util.Base64;

import static com.iabtcf.decoder.Field.Vendors.SEGMENT_TYPE;

/**
 * Decoder to read all information from a IAB TC v2 String.
 *
 * @author SleimanJneidi
 * @author evanwht1
 */
public class TCModelDecoder {

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public static GDPRTransparencyAndConsent decode(String consentString) {
        final String[] split = consentString.split("\\.");
        final BitVector coreStringVector = vectorFromString(split[0]);

        int version = coreStringVector.readInt(Field.CoreString.VERSION);
        switch (version) {
            case 1:
                // TODO : add version1
                throw new UnsupportedOperationException("Version 1 not supported yet");
            case 2:
                final BitVectorGDPRTCModel.Builder builder = BitVectorGDPRTCModel
                        .newBuilder()
                        .coreString(CoreStringDecoder.decode(version, coreStringVector));
                for (int i = 1; i < split.length; i++){
                    readOptionalVector(builder, vectorFromString(split[i]));
                }
                return builder.build();
            default:
                throw new UnsupportedOperationException("Version " + version + " unsupported");
        }
    }


    private static void readOptionalVector(BitVectorGDPRTCModel.Builder builder, BitVector bitVector) {
        SegmentType segmentType = SegmentType.get(bitVector.readInt(SEGMENT_TYPE));
        switch (segmentType) {
            case DISCLOSED_VENDOR:
                builder.disclosedVendors(VendorsDecoder.decode(bitVector));
                break;
            case ALLOWED_VENDOR:
                builder.allowedVendors(VendorsDecoder.decode(bitVector));
                break;
            case PUBLISHER_TC:
                builder.publisherPurposes(PublisherTCDecoder.decode(bitVector));
                break;
        }
    }

    private static BitVector vectorFromString(String base64UrlEncodedString) {
        byte[] bytes = DECODER.decode(base64UrlEncodedString);
        return BitVector.from(bytes);
    }
}
