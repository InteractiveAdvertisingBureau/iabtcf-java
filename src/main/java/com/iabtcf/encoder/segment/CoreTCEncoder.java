package com.iabtcf.encoder.segment;

import com.iabtcf.TCModel;
import com.iabtcf.TCModelEnum;
import com.iabtcf.encoder.Base64Url;
import com.iabtcf.encoder.BaseEncoder;
import com.iabtcf.encoder.field.*;
import com.iabtcf.encoder.sequence.CoreFieldSequence;
import com.iabtcf.model.Fields;
import com.iabtcf.model.PurposeRestrictionVector;
import com.iabtcf.model.SortedVector;
import com.iabtcf.encoder.BitLength;
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

    public void decode(String encodedString, TCModel tcModel) {
        try {
            final Map<String, BaseEncoder> encMap = FieldEncoderMap.getInstance().getFieldMap();
            final CoreFieldSequence coreFieldSequence = CoreFieldSequence.getInstance();
            List<String> encodeSequence = null;
            String bitField = Base64Url.decode(encodedString);
            int versionLength = BitLength.fieldLengths.get(Fields.version);
            int version = IntEncoder.getInstance().decode(bitField.substring(0,versionLength));
            if (version == 1) {
                encodeSequence = coreFieldSequence.one;
            } else if (version == 2) {
                encodeSequence = coreFieldSequence.two;
            }
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
        } catch (Exception e){
            logger.error("CoreTCEncoder's decoder failed: " + e.getMessage());
            tcModel.reset();
        }
    }





}
