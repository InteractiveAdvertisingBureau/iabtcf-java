package com.iabtcf.utils;

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

import java.util.BitSet;
import java.util.NoSuchElementException;

/**
 * An implementation of the IntIterable based on BitSet.
 */
public class BitSetIntIterable extends IntIterable {
    private final BitSet bs;

    public static final BitSetIntIterable EMPTY = new BitSetIntIterable(new BitSet());

    public static BitSetIntIterable from(BitSet bs) {
        return new BitSetIntIterable((BitSet) bs.clone());
    }

    public static BitSetIntIterable from(IntIterable ii) {
        if (ii instanceof BitSetIntIterable) {
            return ((BitSetIntIterable) ii).clone();
        }

        BitSet bs = new BitSet();
        for (IntIterator i = ii.intIterator(); i.hasNext();) {
            bs.set(i.nextInt());
        }

        return new BitSetIntIterable(bs);
    }

    public static BitSetIntIterable from(int... values) {
        BitSet bs = new BitSet();
        for (int i = 0; i < values.length; i++) {
            bs.set(values[i]);
        }
        return new BitSetIntIterable(bs);
    }

    public static BitSetIntIterable.Builder newBuilder() {
        return new Builder();
    }

    public static BitSetIntIterable.Builder newBuilder(BitSetIntIterable prototype) {
        return new Builder(prototype);
    }

    public static BitSetIntIterable.Builder newBuilder(BitSet bs) {
        return new Builder(new BitSetIntIterable(bs));
    }

    private BitSetIntIterable(BitSet bs) {
        this.bs = bs;
    }

    public BitSet toBitSet() {
        return (BitSet) bs.clone();
    }

    @Override
    public BitSetIntIterable clone() {
        return new BitSetIntIterable((BitSet) bs.clone());
    }

    @Override
    public boolean contains(int value) {
        try {
            return bs.get(value);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public IntIterator intIterator() {
        return new IntIterator() {
            int currentIndex = start();

            public int start() {
                if (bs.isEmpty()) {
                    return -1;
                }

                return bs.nextSetBit(0);
            }

            @Override
            public boolean hasNext() {
                return currentIndex != -1;
            }

            @Override
            public Integer next() {
                return nextInt();
            }

            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                int next = currentIndex;
                currentIndex = bs.nextSetBit(currentIndex + 1);
                return next;
            }
        };
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bs == null) ? 0 : bs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BitSetIntIterable other = (BitSetIntIterable) obj;
        if (bs == null) {
            return other.bs == null;
        } else {
            return bs.equals(other.bs);
        }
    }

    @Override
    public String toString() {
        return bs.toString();
    }

    public static class Builder {
        private final BitSet bs;

        private Builder() {
            this(new BitSet());
        }

        private Builder(BitSet bs) {
            this.bs = bs;
        }

        private Builder(BitSetIntIterable prototype) {
            this(prototype.clone().bs);
        }

        public Builder add(int value) {
            bs.set(value);
            return this;
        }

        public Builder add(BitSetIntIterable value) {
            bs.or(value.bs);
            return this;
        }

        public Builder add(IntIterable value) {
            for (IntIterator ii = value.intIterator(); ii.hasNext();) {
                bs.set(ii.nextInt());
            }
            return this;
        }

        public Builder add(Builder value) {
            bs.or(value.bs);
            return this;
        }

        public Builder clear() {
            bs.clear();
            return this;
        }

        /**
         * Returns the maximum value in the set.
         */
        public int max() {
            if (bs.isEmpty()) {
                return 0;
            }

            return bs.length() - 1;
        }

        public BitSetIntIterable build() {
            return new BitSetIntIterable((BitSet) bs.clone());
        }
    }

    public static Builder newBuilder(IntIterable purposesConsent) {
        return new Builder(BitSetIntIterable.from(purposesConsent));
    }
}
