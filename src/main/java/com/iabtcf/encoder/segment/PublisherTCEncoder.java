package com.iabtcf.encoder.segment;

import com.iabtcf.TCModel;
import com.iabtcf.TCModelEnum;
import com.iabtcf.encoder.Base64Url;
import com.iabtcf.encoder.BaseEncoder;
import com.iabtcf.encoder.BitLength;
import com.iabtcf.encoder.field.FieldEncoderMap;
import com.iabtcf.encoder.sequence.PublisherFieldSequence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.*;

public class PublisherTCEncoder implements BaseSegmentEncoder {
    private PublisherTCEncoder() {
    }
    private static final PublisherTCEncoder instance = new PublisherTCEncoder();
    public static PublisherTCEncoder getInstance() {
        return instance;
    }
    private static final Logger logger = LogManager.getLogger(PublisherTCEncoder.class);

    public TCModel decode(String encodedString, TCModel tcModel) {
        try {
            Map<String, BaseEncoder> encMap = FieldEncoderMap.getInstance().fieldMap;
            PublisherFieldSequence pubFieldSequence = PublisherFieldSequence.getInstance();
            List<String> encodedSequence = null;
            if (tcModel.getVersion() == 1) {
                encodedSequence = pubFieldSequence.one;
            } else {
                encodedSequence = pubFieldSequence.two;
            }
            AtomicInteger bStringIdx = new AtomicInteger();
            String bitField = Base64Url.decode(encodedString);
            String finalBitField = bitField.substring(Optional.ofNullable(BitLength.fieldLengths.get("segmentType")).orElse(0));
            encodedSequence.forEach(key -> {
                BaseEncoder encoder = encMap.get(key);
                Object keyLength = null;
                keyLength = Optional.ofNullable(BitLength.fieldLengths.get(key)).orElse(0);
                int numBits = (int) keyLength > 0 ? (int) keyLength : tcModel.getNumCustomPurposes();
                TCModelEnum.valueOf(key).setValue(tcModel, encoder.decode(finalBitField.substring(bStringIdx.get(), bStringIdx.get() + numBits)));
                bStringIdx.addAndGet(numBits);
            });
            return tcModel;
        } catch (Exception e) {
            logger.error("PublisherTCEncoder's decoder failed: " + e.getMessage());
        }
        return null;
    }
}
