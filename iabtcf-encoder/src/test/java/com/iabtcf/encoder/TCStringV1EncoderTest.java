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

import static com.iabtcf.encoder.utils.TestUtils.toDeci;
import static com.iabtcf.test.utils.IntIterableMatcher.matchInts;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;

import com.iabtcf.decoder.TCString;
import com.iabtcf.utils.BitSetIntIterable;

public class TCStringV1EncoderTest {

    @Test
    public void testEncodeDefault() {
        String tcf = TCStringEncoder.newBuilder().version(1).encode();
        assertEquals(1, TCString.decode(tcf).getVersion());
    }

    @Test
    public void testCanEncodeV1String() {
        BitSet purposeConsent = new BitSet();
        purposeConsent.set(1);

        BitSet vendorConsent = new BitSet();
        vendorConsent.set(1);
        vendorConsent.set(2);
        vendorConsent.set(8);

        Instant created = Instant.now();
        Instant updated = created.plus(1, ChronoUnit.HOURS);

        String str = TCStringEncoder.newBuilder()
                .version(1)
                .cmpId(2)
                .created(created)
                .lastUpdated(updated)
                .cmpVersion(3)
                .consentScreen(4)
                .consentLanguage("DE")
                .vendorListVersion(5)
                .purposesConsent(new BitSetIntIterable(purposeConsent))
                .vendorsConsent(new BitSetIntIterable(vendorConsent))
                .encode();

        TCString decodedTCString = TCString.decode(str);
        Assert.assertEquals(1, decodedTCString.getVersion());
        Assert.assertEquals(2, decodedTCString.getCmpId());
        Assert.assertEquals(toDeci(created), decodedTCString.getCreated());
        Assert.assertEquals(toDeci(updated), decodedTCString.getLastUpdated());
        Assert.assertEquals(3, decodedTCString.getCmpVersion());
        Assert.assertEquals(4, decodedTCString.getConsentScreen());
        Assert.assertEquals("DE", decodedTCString.getConsentLanguage());
        Assert.assertThat(decodedTCString.getVendorConsent(), matchInts(1, 2, 8));
        Assert.assertThat(decodedTCString.getPurposesConsent(), matchInts(1));
    }

}
