package com.iabtcf.v2;

public class FieldConstants {

    public enum Type {
        TINY_INT {
            @Override
            public int length() {
                return 6;
            }
        },
        SHORT {
            @Override
            public int length() {
                return 12;
            }
        },
        MEDIUM {
            @Override
            public int length() {
                return 16;
            }
        },
        EPOCH_TIME {
            @Override
            public int length() {
                return 36;
            }
        },
        CHAR {
            @Override
            public int length() {
                return 6;
            }
        };

        public abstract int length();
    }

    public static class CoreStringConstants {
        public static final int VERSION_OFFSET = 0;
        public static final int CREATED_OFFSET = 6;
        public static final int LAST_UPDATED_OFFSET = 42;
        public static final int CMP_ID_OFFSET = 78;
        public static final int CMP_VERSION_OFFSET = 90;
        public static final int CONSENT_SCREEN_OFFSET = 102;
        public static final int CONSENT_LANGUAGE_OFFSET = 108;
        public static final int VENDOR_LIST_VERSION_OFFSET = 120;
        public static final int TCF_POLICY_VERSION_OFFSET = 132;
        public static final int IS_SERVICE_SPECIFIC_OFFSET = 138;
        public static final int USE_NON_STANDARD_STACKS_OFFSET = 139;

        public static final int SPECIAL_FEATURE_OPT_INS_OFFSET = 140;
        public static final int SPECIAL_FEATURE_OPT_INS_LENGTH = 12;

        public static final int PURPOSES_CONSENT_OFFSET = 152;
        public static final int PURPOSES_CONSENT_LENGTH = 24;

        public static final int PURPOSE_LI_TRANSPARENCY_OFFSET = 176;
        public static final int PURPOSE_LI_TRANSPARENCY_LENGTH = 24;

        public static final int PURPOSE_ONE_TREATMENT_OFFSET = 200;
        public static final int PUBLISHER_CC_OFFSET = 201;

        public static final int SEGMENT_TYPE_OFFSET = 0;
        public static final int SEGMENT_TYPE_LENGTH = 3;
        public static final int SEGMENT_TYPE_DEFAULT = 0;
        public static final int SEGMENT_TYPE_DISCLOSED_VENDOR = 1;
        public static final int SEGMENT_TYPE_ALLOWED_VENDOR = 2;
        public static final int SEGMENT_TYPE_PUBLISHER_TC = 3;
    }
}
