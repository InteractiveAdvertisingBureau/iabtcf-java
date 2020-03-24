package com.iabtcf;

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

import java.util.function.Function;

/**
 * This enum defines all V1 and V2 consent string fields with their offsets and lengths. Since some
 * fields have dynamic values, the offset and length methods are a function of ByteBitVector
 * allowing a dynamic field access to the consent string.
 *
 * The enum takes care to cache the lengths and offsets of fields when appropriate. Whenever
 * possible, static field lengths and offsets are cached by the enum using the MemoizingFunction.
 * Due to the dynamic nature of some fields, computing the offsets and lengths can only be done at
 * runtime when a consent string is parsed. For such fields, their values are cached by the
 * ByteBitVector used to parse the consent string.
 *
 * All fields following a dynamic field are treated as a dynamic field.
 */
public enum FieldDefs {
    CORE_VERSION(6, 0),
    CORE_CREATED(36),
    CORE_LAST_UPDATED(36),
    CORE_CMP_ID(12),
    CORE_CMP_VERSION(12),
    CORE_CONSENT_SCREEN(6),
    CORE_CONSENT_LANGUAGE(12),
    CORE_VENDOR_LIST_VERSION(12),
    CORE_TCF_POLICY_VERSION(6),
    CORE_IS_SERVICE_SPECIFIC(1),
    CORE_USE_NON_STANDARD_STOCKS(1),
    CORE_SPECIAL_FEATURE_OPT_INS(12),
    CORE_PURPOSES_CONSENT(24),
    CORE_PURPOSES_LI_TRANSPARENCY(24),
    CORE_PURPOSE_ONE_TREATMENT(1),
    CORE_PUBLISHER_CC(12),
    CORE_VENDOR_MAX_VENDOR_ID(16),
    CORE_VENDOR_IS_RANGE_ENCODING(1),
    CORE_VENDOR_BITRANGE_FIELD(
            BitRangeFieldUtils.lengthSupplier(CORE_VENDOR_IS_RANGE_ENCODING, CORE_VENDOR_MAX_VENDOR_ID)),
    CORE_VENDOR_LI_MAX_VENDOR_ID(16),
    CORE_VENDOR_LI_IS_RANGE_ENCODING(1),
    CORE_VENDOR_LI_BITRANGE_FIELD(
            BitRangeFieldUtils.lengthSupplier(CORE_VENDOR_LI_IS_RANGE_ENCODING, CORE_VENDOR_LI_MAX_VENDOR_ID)),
    CORE_NUM_PUB_RESTRICTION(12),
    CORE_PUB_RESTRICTION_ENTRY(
            PublisherRestrictionUtils.lengthSupplier(CORE_NUM_PUB_RESTRICTION)),

    OOB_SEGMENT_TYPE(3, 0),

    // disallowed vendor fields
    DV_MAX_VENDOR_ID(16, OOB_SEGMENT_TYPE),
    DV_IS_RANGE_ENCODING(1),
    DV_VENDOR_BITRANGE_FIELD(
            BitRangeFieldUtils.lengthSupplier(DV_IS_RANGE_ENCODING, DV_MAX_VENDOR_ID)),

    // allowed vendor fields
    AV_MAX_VENDOR_ID(16, OOB_SEGMENT_TYPE),
    AV_IS_RANGE_ENCODING(1),
    AV_VENDOR_BITRANGE_FIELD(
            BitRangeFieldUtils.lengthSupplier(AV_IS_RANGE_ENCODING, AV_MAX_VENDOR_ID)),

    // publisher purposes transparency and consent
    PPTC_SEGMENT_TYPE(3, 0),
    PPTC_PUB_PURPOSES_CONSENT(24),
    PPTC_PUB_PURPOSES_LI_TRANSPARENCY(24),
    PPTC_NUM_CUSTOM_PURPOSES(6),
    PPTC_CUSTOM_PURPOSES_CONSENT(new LengthSupplier() {
        @Override
        public Integer apply(ByteBitVector t) {
            return Integer.valueOf(t.readBits6(PPTC_NUM_CUSTOM_PURPOSES.getOffset(t)));
        }

        @Override
        public boolean isDynamic() {
            return true;
        }
    }),
    PPTC_CUSTOM_PURPOSES_LI_TRANSPARENCY(new LengthSupplier() {
        @Override
        public Integer apply(ByteBitVector t) {
            // same length as PPTC_CUSTOM_PURPOSES_CONSENT
            return PPTC_CUSTOM_PURPOSES_CONSENT.getLength(t);
        }

        @Override
        public boolean isDynamic() {
            return true;
        }
    }),

    // range entry, only field lengths are supported
    NUM_ENTRIES(12, OffsetSupplier.NOT_SUPPORTED),
    IS_A_RANGE(1, OffsetSupplier.NOT_SUPPORTED),
    START_OR_ONLY_VENDOR_ID(16, OffsetSupplier.NOT_SUPPORTED),
    END_VENDOR_ID(16, OffsetSupplier.NOT_SUPPORTED),

    // publish restriction fields, only field lengths are supported
    PURPOSE_ID(6, OffsetSupplier.NOT_SUPPORTED),
    RESTRICTION_TYPE(2, OffsetSupplier.NOT_SUPPORTED),

    CHAR(6, OffsetSupplier.NOT_SUPPORTED),

    // v1 fields
    V1_VERSION(6, 0),
    V1_CREATED(36),
    V1_LAST_UPDATED(36),
    V1_CMP_ID(12),
    V1_CMP_VERSION(12),
    V1_CONSENT_SCREEN(6),
    V1_CONSENT_LANGUAGE(12),
    V1_VENDOR_LIST_VERSION(12),
    V1_PURPOSES_ALLOW(24),
    V1_VENDOR_MAX_VENDOR_ID(16),
    V1_VENDOR_IS_RANGE_ENCODING(1),
    V1_VENDOR_BITRANGE_FIELD(BitRangeFieldUtils.lengthSupplierV1()),
    V1_VENDOR_DEFAULT_CONSENT(1, V1_VENDOR_IS_RANGE_ENCODING),
    V1_VENDOR_NUM_ENTRIES(12);

    private OffsetSupplier offset;
    private LengthSupplier length;
    private boolean isDynamic = false;
    private boolean isDynamicInit = false;

    FieldDefs(int length, FieldDefs field) {
        assert field != this;

        this.length = LengthSupplier.constant(length);
        this.offset = OffsetSupplier.from(field);
    }

    FieldDefs(int length, OffsetSupplier offset) {
        this.length = LengthSupplier.constant(length);
        this.offset = offset;
    }

    FieldDefs(final int length, int offset) {
        this.length = LengthSupplier.constant(length);
        this.offset = OffsetSupplier.constant(offset);
    }

    FieldDefs(LengthSupplier length) {
        this.length = length;
        this.offset = OffsetSupplier.fromPrevious(this);
    }

    FieldDefs(final int length) {
        this.length = LengthSupplier.constant(length);
        this.offset = OffsetSupplier.fromPrevious(this);
    }

    /**
     * Determines whether the field is dynamic, that is, it depends on the particular consent string
     * being processed.
     */
    protected boolean isDynamic() {
        if (!isDynamicInit) {
            isDynamic = offset.isDynamic() || length.isDynamic();
            isDynamicInit = true;
        }

        return isDynamic;
    }

    /**
     * Returns the length of the field.
     */
    public int getLength(ByteBitVector bbv) {
        if (isDynamic) {
            return bbv.cache.getLength(this, length);
        } else {
            return length.apply(bbv);
        }
    }

    /**
     * Returns the offset of the field.
     */
    public int getOffset(ByteBitVector bbv) {
        return bbv.cache.getOffset(this, offset);
    }

    /**
     * Returns the offset of the next field.
     */
    public int getEnd(ByteBitVector bbv) {
        return getLength(bbv) + getOffset(bbv);
    }

    /**
     * The offset of the nth field depends on the length and offset of the nth-1 field. This class
     * is used to cache static fields to avoid querying parent fields.
     *
     * Both the value and it's dynamic state are cached.
     */
    private static abstract class MemoizingFunction
            implements LengthSupplier, OffsetSupplier, Function<ByteBitVector, Integer> {
        private boolean dynamicInitialized = false;
        private boolean isDynamic = false;
        private Integer value;

        public abstract Integer doCompute(ByteBitVector t);

        @Override
        public abstract boolean isDynamic();

        @Override
        public Integer apply(ByteBitVector t) {
            if (value == null || isDynamicPvt()) {
                value = doCompute(t);

                return value;
            } else {
                return value;
            }
        }

        private boolean isDynamicPvt() {
            if (!dynamicInitialized) {
                dynamicInitialized = true;
                isDynamic = isDynamic();
            }

            return isDynamic;
        }
    }

    private interface OffsetSupplier extends Function<ByteBitVector, Integer> {

        /**
         * This is used when we don't want a field to support offsets.
         */
        public static final OffsetSupplier NOT_SUPPORTED = new OffsetSupplier() {

            @Override
            public Integer apply(ByteBitVector t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isDynamic() {
                return false;
            }
        };

        /**
         * A constant offset for static fields.
         */
        public static OffsetSupplier constant(int offset) {
            return new OffsetSupplier() {

                @Override
                public Integer apply(ByteBitVector t) {
                    return offset;
                }

                @Override
                public boolean isDynamic() {
                    return false;
                }
            };
        }

        /**
         * Supplies the offset that's based on the specified field. The offset value is stored to
         * avoid re-computing.
         */
        public static OffsetSupplier from(final FieldDefs thisEnum) {
            return new MemoizingFunction() {
                @Override
                public boolean isDynamic() {
                    return thisEnum.isDynamic();
                }

                @Override
                public Integer doCompute(ByteBitVector t) {
                    return thisEnum.getLength(t) + thisEnum.getOffset(t);
                }
            };
        }

        /**
         * Supplies the offset that's based on the the fields previous field. The offset value is
         * stored to avoid re-computing.
         */
        public static OffsetSupplier fromPrevious(final FieldDefs thisEnum) {
            return new MemoizingFunction() {

                @Override
                public boolean isDynamic() {
                    return FieldDefs.values()[thisEnum.ordinal() - 1].isDynamic();
                }

                @Override
                public Integer doCompute(ByteBitVector t) {
                    FieldDefs prevEnum = FieldDefs.values()[thisEnum.ordinal() - 1];
                    return prevEnum.getLength(t) + prevEnum.getOffset(t);
                }
            };
        }

        public boolean isDynamic();
    }

    private interface LengthSupplier extends Function<ByteBitVector, Integer> {

        /**
         * A constant length for static fields.
         */
        public static LengthSupplier constant(int length) {
            return new LengthSupplier() {

                @Override
                public Integer apply(ByteBitVector t) {
                    return length;
                }

                @Override
                public boolean isDynamic() {
                    return false;
                }
            };
        }

        public boolean isDynamic();
    }

    /**
     * Utility class to handle publisher restrictions
     */
    private static class PublisherRestrictionUtils {
        public static int calculateBitRangelength(ByteBitVector t, int numPubRestrictionsOffset) {
            int cptr = numPubRestrictionsOffset;
            int numPubRestrictions = t.readBits12(numPubRestrictionsOffset);
            cptr += CORE_NUM_PUB_RESTRICTION.getLength(t);

            for (int i = 0; i < numPubRestrictions; i++) {
                cptr += (PURPOSE_ID.getLength(t) + RESTRICTION_TYPE.getLength(t));
                cptr += BitRangeFieldUtils.calculateRangeLength(t, cptr);
            }

            return cptr - numPubRestrictionsOffset;
        }

        public static LengthSupplier lengthSupplier(FieldDefs numPubRestrictionsOffset) {
            return new LengthSupplier() {
                @Override
                public Integer apply(ByteBitVector t) {
                    return calculateBitRangelength(t, numPubRestrictionsOffset.getOffset(t));
                }

                @Override
                public boolean isDynamic() {
                    return true;
                }
            };
        }
    }

    /**
     * Utility class to handle bit / range fields.
     */
    private static class BitRangeFieldUtils {
        public static int calculateRangeLength(ByteBitVector t, int numEntriesOffset) {
            int cptr = numEntriesOffset;
            int numEntries = t.readBits12(cptr);
            cptr += NUM_ENTRIES.getLength(t);

            for (int i = 0; i < numEntries; i++) {
                cptr += IS_A_RANGE.getLength(t) + START_OR_ONLY_VENDOR_ID.getLength(t)
                        + (t.readBits1(cptr) ? END_VENDOR_ID.getLength(t)
                                : 0);
            }

            return cptr - numEntriesOffset;
        }

        public static int calculateBitLength(ByteBitVector t, int maxVendorIdOffset) {
            return t.readBits16(maxVendorIdOffset);
        }

        public static int calculateBitRangeLength(ByteBitVector t, int isRangeEncodingOffset, int maxVendorIdOffset) {
            boolean isRangeEncoding = t.readBits1(isRangeEncodingOffset);
            if (!isRangeEncoding) {
                return calculateBitLength(t, maxVendorIdOffset);
            } else {
                return calculateRangeLength(t, isRangeEncodingOffset + 1);
            }
        }

        public static LengthSupplier lengthSupplier(FieldDefs isRangeEncoding, FieldDefs maxVendorId) {
            return new LengthSupplier() {
                @Override
                public Integer apply(ByteBitVector t) {
                    return calculateBitRangeLength(t, isRangeEncoding.getOffset(t), maxVendorId.getOffset(t));
                }

                @Override
                public boolean isDynamic() {
                    return true;
                }
            };
        }

        public static LengthSupplier lengthSupplierV1() {
            return new LengthSupplier() {
                @Override
                public Integer apply(ByteBitVector t) {
                    int isRangeEncodingOffset = FieldDefs.V1_VENDOR_IS_RANGE_ENCODING.getOffset(t);
                    if (!t.readBits1(isRangeEncodingOffset)) {
                        return calculateBitLength(t, FieldDefs.V1_VENDOR_MAX_VENDOR_ID.getOffset(t));
                    } else {
                        return calculateRangeLength(t, FieldDefs.V1_VENDOR_NUM_ENTRIES.getOffset(t));
                    }
                }

                @Override
                public boolean isDynamic() {
                    return true;
                }
            };
        }
    }
}
