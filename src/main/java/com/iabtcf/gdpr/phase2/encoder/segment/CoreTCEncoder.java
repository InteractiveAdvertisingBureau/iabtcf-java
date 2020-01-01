package com.iabtcf.gdpr.phase2.encoder.segment;

import com.iabtcf.gdpr.phase2.Utils.TCModel;
import com.iabtcf.gdpr.phase2.Utils.TCModelEnum;
import com.iabtcf.gdpr.phase2.encoder.Base64Url;
import com.iabtcf.gdpr.phase2.encoder.BaseEncoder;
import com.iabtcf.gdpr.phase2.encoder.field.FieldEncoderMap;
import com.iabtcf.gdpr.phase2.encoder.field.FixedVectorEncoder;
import com.iabtcf.gdpr.phase2.encoder.field.PurposeRestrictionVectorEncoder;
import com.iabtcf.gdpr.phase2.encoder.field.VendorVectorEncoder;
import com.iabtcf.gdpr.phase2.encoder.sequence.CoreFieldSequence;
import com.iabtcf.gdpr.phase2.model.PurposeRestrictionVector;
import com.iabtcf.gdpr.phase2.model.SortedVector;
import com.iabtcf.gdpr.phase2.encoder.BitLength;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreTCEncoder implements BaseSegmentEncoder {
    private CoreTCEncoder(){}
    private static final CoreTCEncoder instance = new CoreTCEncoder();
    public static CoreTCEncoder getInstance() {
        return instance;
    }

    private static final Logger logger = LogManager.getLogger(CoreTCEncoder.class);

    public TCModel decode(String encodedString, TCModel tcModel) {
        try {
            final Map<String, BaseEncoder> encMap = FieldEncoderMap.getInstance().getFieldMap();
            final CoreFieldSequence coreFieldSequence = CoreFieldSequence.getInstance();
            List<String> encodeSequence = null;
            if (tcModel.getVersion() == 1) {
                encodeSequence = coreFieldSequence.one;
            } else if (tcModel.getVersion() == 2) {
                encodeSequence = coreFieldSequence.two;
            }
            String bitField = Base64Url.decode(encodedString);
            AtomicInteger bStringIdx = new AtomicInteger();
            if (encodeSequence != null && !encodeSequence.isEmpty()) {
                encodeSequence.forEach(key -> {
                    BaseEncoder encoder = encMap.get(key);
                    if (encoder != null) {
                        Optional<Integer> keyLength;
                        String bits = "";
                        keyLength = Optional.ofNullable(BitLength.fieldLengths.get(key));
                        if (keyLength.isPresent()) {
                            bits = bitField.substring(bStringIdx.get(), bStringIdx.addAndGet(keyLength.get()));
                            TCModelEnum.valueOf(key).setValue(tcModel, encoder.decode(bits));
                        } else {
                            bits = bitField.substring(bStringIdx.get());
                            int tcKeyLength = 0;
                            if (encoder instanceof PurposeRestrictionVectorEncoder) {
                                PurposeRestrictionVector purposeRestrictionVector = (PurposeRestrictionVector) encoder.decode(bits);
                                TCModelEnum.valueOf(key).setValue(tcModel, purposeRestrictionVector);
                                tcKeyLength = purposeRestrictionVector.bitLength;

                            } else if (encoder instanceof FixedVectorEncoder || encoder instanceof VendorVectorEncoder) {
                                SortedVector vector = (SortedVector) encoder.decode(bits);
                                TCModelEnum.valueOf(key).setValue(tcModel, vector);
                                tcKeyLength = vector.getBitLength();
                            } else {
                                logger.error("Decoder instance in incorrect " + encoder.getClass());
                            }
                            bStringIdx.addAndGet(tcKeyLength);
                        }
                    } else {
                        logger.error("Decoder is absent for the given key: " + key);
                    }
                });
            }
            return tcModel;
        } catch (Exception e){
            logger.error("CoreTCEncoder's decoder failed: " + e.getMessage());
        }
        return null;
    }





}
