package com.iabtcf.decoder;

/*-
 * #%L
 * IAB TCF Java Decoder Library
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

import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;

public class PPCStringTest {
    @Test
    public void decodePublisherConsetV1() {
        PPCString decode = PPCString.decode("BOxgOqAOxgOqAAAABBENC2-AAAAtHAA");

        assertEquals(decode.getVersion(), 1);
        assertEquals(Instant.parse("2020-04-07T20:36:16.000Z"), decode.getCreated());
        assertEquals(Instant.parse("2020-04-07T20:36:16.000Z"), decode.getLastUpdated());
        assertEquals(0, decode.getCmpId());
        assertEquals(1, decode.getCmpVersion());
        assertEquals(1, decode.getConsentScreen());
        assertEquals("EN", decode.getConsentLanguage());
        assertEquals(182, decode.getVendorListVersion());
        assertEquals(3968, decode.getPublisherPurposesVersion());
        assertThat(decode.getStandardPurposesAllowed(), matchInts(19, 21, 22, 24));
        assertTrue(decode.getCustomPurposesBitField().isEmpty());
    }
}
