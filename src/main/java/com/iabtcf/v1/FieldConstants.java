package com.iabtcf.v1;

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

public class FieldConstants {
    public static final int VERSION_BIT_OFFSET = 0;
    public static final int CREATED_BIT_OFFSET = 6;
    public static final int UPDATED_BIT_OFFSET = 42;
    public static final int CMP_ID_OFFSET = 78;
    public static final int CMP_VERSION_OFFSET = 90;
    public static final int CONSENT_SCREEN_OFFSET = 102;
    public static final int CONSENT_LANGUAGE_OFFSET = 108;
    public static final int VENDOR_LIST_VERSION_OFFSET = 120;

    public static final int VENDOR_ID_SIZE = 16;
    public static final int PURPOSES_OFFSET = 132;
    public static final int PURPOSES_SIZE = 24;
    public static final int MAX_VENDOR_ID_OFFSET = 156;
    public static final int ENCODING_TYPE_OFFSET = 172;
    public static final int VENDOR_BITFIELD_OFFSET = 173;
    public static final int DEFAULT_CONSENT_OFFSET = 173;
    public static final int NUM_ENTRIES_OFFSET = 174;
    public static final int RANGE_ENTRY_OFFSET = 186;
    public static final int VENDOR_ENCODING_RANGE = 1;
}
