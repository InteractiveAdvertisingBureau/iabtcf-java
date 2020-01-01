package com.iabtcf.gdpr.phase2.encoder.field;

import com.iabtcf.gdpr.phase2.encoder.BaseEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntEncoder implements BaseEncoder<Integer> {
    private IntEncoder() {
    }

    private static final IntEncoder instance = new IntEncoder();
    public static IntEncoder getInstance() {
        return instance;
    }
    private static final Logger logger = LogManager.getLogger(IntEncoder.class);
//    public static String encode(Integer value,int numBits) {
//        String bitString="";
//        bitString = value.toBinaryString(2);
//
//        if(bitString.length() > numBits || value.equals(0)) {
//            // throw error
//        }
//
//        if(bitString.length() < numBits) {
//            bitString = new String(new char[numBits - bitString.length()]).replace("\0", "0") + bitString;
//        }
//        return bitString;
//    }

    public Integer decode(String value) {
        try {
            return Integer.parseInt(value,2);
        } catch(Exception e){
            logger.error("IntEncoder's decoding failed:" + e.getMessage());
        }
        return null;
    }
}
