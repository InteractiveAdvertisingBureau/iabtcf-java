package com.iabtcf.gdpr.phase2.encoder.field;

import com.iabtcf.gdpr.phase2.encoder.BaseEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class DateEncoder implements BaseEncoder<Date> {
//    public String encode(Date value,int numBits) {
//        return IntEncoder.encode(Math.round(value.getTime()/100),numBits);
//    }

    private DateEncoder() {
    }

    private static final DateEncoder instance = new DateEncoder();
    public static DateEncoder getInstance() {
        return instance;
    }
    private static final Logger logger = LogManager.getLogger(DateEncoder.class);
    public Date decode(String value) {
        try {
            Date date = new Date(Long.parseLong(value, 2) * 100);
            return date;
        } catch (Exception e) {
            logger.error("Date encoder failed: " + e.getMessage());
        }
        return null;
    }
}
