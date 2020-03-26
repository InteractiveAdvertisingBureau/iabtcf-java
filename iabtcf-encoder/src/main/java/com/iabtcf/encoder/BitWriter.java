package com.iabtcf.encoder;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;

public class BitWriter {

    private byte[] buffer = new byte[1 << 10];
    private byte currentByte;
    private int size;
    private int bitPointer = 7;

    void write(long value, int length) {
        if (value < 0) {
            throw new IllegalArgumentException(value + " is a non positive value");
        }
        ensureCapacity(length);
        for (int i = length - 1; i >= 0; i--) {
            long mask = 1L << i;
            if ((value & (mask)) > 0) {
                currentByte |= 1 << (bitPointer);
            }
            advanceBitPointer();
        }
    }

    void write(BitSet bitSet, int length) {
        for (int i = 0; i < length; i++) {
            writeBit(bitSet.get(i));
        }
    }

    void writeInstant(Instant instant) {
        long deciSeconds = instant.toEpochMilli() / 100;
        write(deciSeconds, 36);
    }

    void writeStr(String str) {
        for (int i = 0; i < str.length(); i++) {
            int value = str.charAt(i) - 'A';
            write(value, 6);
        }
    }

    void writeBit(boolean bit) {
        int value = bit ? 1 : 0;
        write(value, 1);
    }

    private void advanceBitPointer() {
        bitPointer--;
        if (bitPointer < 0) {
            bitPointer = 7;
            buffer[size++] = currentByte;
            currentByte = 0;
        }
    }

    private void ensureCapacity(int requestCapacity) {
        if (requestCapacity + size >= buffer.length) {
            byte[] newBuffer = new byte[(int) (buffer.length * 1.25)];
            System.arraycopy(buffer, 0, newBuffer, 0, size);
            this.buffer = newBuffer;
            ensureCapacity(requestCapacity);
        }
    }

    public byte[] array() {
        if (bitPointer < 7) { // clear remaining
            ensureCapacity(1);
            buffer[size++] = currentByte;
        }
        byte[] copy = Arrays.copyOf(buffer, size);
        return copy;
    }

    public String toBase64() {
        byte[] encoded = this.array();
        return Base64.getUrlEncoder().encodeToString(encoded);
    }
}
