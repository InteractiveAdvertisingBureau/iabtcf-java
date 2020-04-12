package com.iabtcf.decoder;

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

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream that returns EOF when either the end of the byte array is reached or the character
 * '.'.
 */
class SegmentInputStream extends InputStream {
    private final String src;
    private int rpos;
    private int start;
    private int end = -1;

    /**
     * src must be a base64 (e.g. subset of StandardCharsets.US_ASCII) encoded String.
     */
    public SegmentInputStream(String src, int start) {

        if (src == null) {
            throw new IllegalArgumentException("src");
        }

        if (start < 0) {
            throw new IllegalArgumentException(String.format("start is invalid %d", start));
        }

        this.src = src;
        start = rpos = Math.min(start, src.length());
    }

    @Override
    public int read() throws IOException {
        char c;
        if (rpos >= src.length() || (c = src.charAt(rpos)) == '.') {
            return -1;
        }

        rpos++;

        // make sure String is encoded as single bytes.
        return (c & 0xFF) != c ? -1 : c;
    }

    @Override
    public int available() throws IOException {
        if (rpos < src.length() && src.charAt(rpos) != '.') {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void reset() throws IOException {
        rpos = start;
    }

    /**
     * Returns the ending location of the segment. For example, the strings "hello" and "hello."
     * would return the same result.
     */
    protected int segmentEnd() {
        if (end == -1) {
            for (end = rpos; end < src.length() && src.charAt(end) != '.'; end++);
        }

        return end;
    }

    /**
     * Returns true if there are additional segments.
     */
    protected boolean hasNextSegment() {
        for (int idx = Math.max(rpos, end); idx < src.length(); idx++) {
            if (src.charAt(idx) == '.') {
                end = idx;
                return true;
            }
        }

        return false;
    }
}
