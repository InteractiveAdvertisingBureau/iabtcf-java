package com.iabtcf.encoder.segment;

import com.iabtcf.TCModel;

public interface BaseSegmentEncoder {
    void decode(String encodedString, TCModel tcModel);
}
