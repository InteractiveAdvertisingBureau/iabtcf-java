package com.iabtcf.encoder.field;

import com.iabtcf.encoder.BaseEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BooleanEncoder implements BaseEncoder<Boolean> {
    private static final String TRUE = "1";
//    public final String encode(Boolean value) {
//        return value + "";
//    }

    private BooleanEncoder() {
    }

    private static final BooleanEncoder instance = new BooleanEncoder();

    public static BooleanEncoder getInstance() {
        return instance;
    }

    private static final Logger logger = LogManager.getLogger(IntEncoder.class);

    public final Boolean decode(String value) {
        return value.equals(TRUE);
    }
}
