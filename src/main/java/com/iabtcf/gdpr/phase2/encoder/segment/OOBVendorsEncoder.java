package com.iabtcf.gdpr.phase2.encoder.segment;

import com.iabtcf.gdpr.phase2.Utils.TCModel;
import com.iabtcf.gdpr.phase2.Utils.TCModelEnum;
import com.iabtcf.gdpr.phase2.encoder.Base64Url;
import com.iabtcf.gdpr.phase2.encoder.BaseEncoder;
import com.iabtcf.gdpr.phase2.encoder.BitLength;
import com.iabtcf.gdpr.phase2.encoder.field.FieldEncoderMap;
import com.iabtcf.gdpr.phase2.model.Fields;
import com.iabtcf.gdpr.phase2.model.SortedVector;

import java.util.Map;
import java.util.Optional;


public class OOBVendorsEncoder implements BaseSegmentEncoder {
    private OOBVendorsEncoder() {
    }
    private static final OOBVendorsEncoder instance = new OOBVendorsEncoder();
    public static OOBVendorsEncoder getInstance() {
        return instance;
    }

    public TCModel decode(String encodedString, TCModel tcModel) {
        final Map<String, BaseEncoder> encMap = FieldEncoderMap.getInstance().getFieldMap();
        String bits = Base64Url.decode(encodedString);
        int index = 0;
        int segType = (int)(encMap.get("segmentType").decode(bits.substring(index,index + Optional.ofNullable(BitLength.fieldLengths.get("segmentType")).orElse(0))));
        String segmentName = "";
        switch(segType) {
            case 1:
                segmentName = SegmentType.one;
                break;
            case 2:
                segmentName = SegmentType.two;
                /**
                 * if a vendors allowed segment exists, then support for OOB signaling is
                 * implied
                 */
                TCModelEnum.valueOf(Fields.supportOOB).setValue(tcModel,true);
                break;
        }
        BaseEncoder<SortedVector> encoder = (BaseEncoder<SortedVector>) encMap.get(segmentName);
        SortedVector vector = encoder.decode(bits.substring(Optional.ofNullable(BitLength.fieldLengths.get("segmentType")).orElse(0)));

        SortedVector set = new SortedVector();
        for(int i=0; i <= (int)vector.getSet().first(); i++) {
            if(vector.getSet().contains(i)){
                set = (SortedVector) TCModelEnum.valueOf(segmentName).getValue(tcModel);
                set.getSet().add(i);
                if(set.getBitLength()!=0) {
                    set.setBitLength(0);
                }

            }

        }
        if(set.getSet().size()>0) {
            TCModelEnum.valueOf(segmentName).setValue(tcModel, set);
        }
        return tcModel;



    }


}
