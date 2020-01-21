package com.iabtcf.encoder.field;

import com.iabtcf.encoder.BaseEncoder;
import com.iabtcf.model.SortedVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FixedVectorEncoder implements BaseEncoder<SortedVector> {
    private FixedVectorEncoder() {
    }

    BooleanEncoder booleanEncoder = BooleanEncoder.getInstance();
    private static final FixedVectorEncoder instance = new FixedVectorEncoder();
    public static FixedVectorEncoder getInstance() {
        return instance;
    }
    private static final Logger logger = LogManager.getLogger(FixedVectorEncoder.class);
//    public static String encode(SortedSet<Integer> value,int numBits) {
//        String bitString = "";
//        for(int i=1; i <=numBits; i++) {
//            bitString += BooleanEncoder.encode(value.contains(i));
//        }
//        return bitString;
//    }

    public final SortedVector decode(String value) {
        try {
            SortedVector st = new SortedVector();
            for (int i = 1; i <= value.length(); i++) {
                if (booleanEncoder.decode(String.valueOf(value.charAt(i - 1)))) {
                    st.getSet().add(i);
                    if (st.getBitLength() != 0) {
                        st.setBitLength(0);
                    }
                }
            }
            st.setBitLength(value.length());
            return st;
        } catch (Exception e) {
            logger.error("FixedVector decoder failed :" + e.getMessage());
        }
        return null;
    }
}
