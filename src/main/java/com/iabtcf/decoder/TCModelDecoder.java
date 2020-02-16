package com.iabtcf.decoder;

import com.iabtcf.model.TCModel;

/**
 * A Thread-safe binary decoder
 */
public interface TCModelDecoder {

    static TCModelDecoder instance() {
        return new TCModelDecoderImpl();
    }

    TCModel decode(String consentString);
}
