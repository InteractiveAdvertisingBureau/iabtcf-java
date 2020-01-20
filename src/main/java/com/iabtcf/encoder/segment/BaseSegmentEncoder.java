package com.iabtcf.encoder.segment;

import com.iabtcf.TCModel;

public interface BaseSegmentEncoder {
    TCModel decode(String encodedString, TCModel tcModel);
}
