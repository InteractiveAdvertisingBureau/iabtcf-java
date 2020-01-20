package com.iabtcf.encoder;

public interface BaseEncoder<T> {
//    String encode(T value);
    T decode(String value);

}
