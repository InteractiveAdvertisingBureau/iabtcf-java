# iabtcf-java

Encode/decode consent information with the IAB EU's GDPR Transparency and Consent Framework

The IAB specification for the consent string format is available on the [IAB Github](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/tree/master/TCFv2).

### IAB Europe Transparency and Consent Framework v2
Version 2 of the TCF Specifications were released 21 August 2019 with industry adoption commencing first half of 2020.

Framework Technical specifications available at: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/tree/master/TCFv2

### Usage

#### Maven

The official iabtcf java library is distributed through maven central. Please [search maven central](https://search.maven.org/search?q=g:com.iabtcf) for the current release version.

#### Decoding

The latest version of the library support decoding both [v1](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md#vendor-consent-string-format-) and [v2](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#tc-string-format) strings using the same interface. Decoding requires the iabtcf-decoder module dependency:

```
<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-decoder</artifactId>
  <version>VERSION</version>
</dependency>
```

Example of decoding a consent string,

```
import com.iabtcf.decoder.TCString;

TCString tcString = TCString.decode("COwxsONOwxsONKpAAAENAdCAAMAAAAAAAAAAAAAAAAAA");

try {
    tcString.getVersion(); ( => 2)
    tcString.getVendorConsent().contains(10); ( => false)
} catch (TCStringDecodeException e) {
    throw e;
}
```

##### Lazy Decoding and Exceptions

The iabtcf-decoder library has the ability to perform lazy decoding. When this mode is activated, a
given field is only decoded during respective TCString#get function application. This may be of benefit if only a
subset of fields need to be accessed since less work is done. To enable lazy decoding a user should invoke,

```
TCString tcString = TCString.decode(str, DecoderOption.LAZY);
```

It's important to be aware that since the `str` is not evaluated at the time TCString#decode is
called, any potential parsing errors will not be thrown until function application. Under non-lazy
decoding, every field is examined during TCString#decode and the user can expect some sense of
string validity if the method returns without throwing an exception. However, the library does not
claim that no other exception may be thrown. It's advisable that all TCString#get methods be wrapped in a
try-catch block. See javadoc for further details.

##### Decoding Publisher Purposes Consent String Format (v1)

The iabtcf-decoder library supports decoding iabtcf v1 [publisher purposes consent strings](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md#publisher-purposes-consent-string-format-).

```
import com.iabtcf.decoder.PPCString;

PPCString ppcString = PPCString.decode("BOxgOqAOxgOqAAAABBENC2-AAAAtHAA");

try {
    ppcString.getVersion() => 1
    ppcString.getStandardPurposesAllowed().contains(7) => false
} catch (TCStringDecodeException e) {
    throw e;
}
```

The `TCString#decode` method can throw a number of runtime exceptions when encountering errors during parsing. See javadoc for more details.

#### Encoding

The latest version of the library supports encoding both v1 and v2 strings using the same interface. Encoding requires the iabtcf-decoder and iabtcf-encoder module dependencies,


```
<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-encoder</artifactId>
  <version>VERSION</version>
</dependency>

<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-decoder</artifactId>
  <version>VERSION</version>
</dependency>
```

Example of encoding a consent string,

```
import com.iabtcf.encoder.TCStringEncoder.Builder;
import com.iabtcf.utils.BitSetIntIterable;

TCStringEncoder.Builder tcStrBuilder = TCStringEncoder.newBuilder()
    .version(2)
    .created(Instant.now())
    .lastUpdated(Instant.now())
    .cmpId(1)
    .cmpVersion(12)
    .consentScreen(1)
    .consentLanguage("FR")
    .vendorListVersion(2)
    .tcfPolicyVersion(1)
    .isServiceSpecific(true)
    .useNonStandardStacks(false)
    .addSpecialFeatureOptIns(BitSetIntIterable.from(1, 2))
    .addPurposesConsent(BitSetIntIterable.from(4, 8))
    .addPurposesLITransparency(BitSetIntIterable.from(11, 20))
    .purposeOneTreatment(true)
    .publisherCC("DE")
    .addVendorConsent(BitSetIntIterable.from(1, 4))
    .addVendorLegitimateInterest(BitSetIntIterable.from(2, 6));

TCString tcStr = tcStrBuilder.toTCString();
String tcStrEncoded = tcStrBuilder.encode();

assertEquals(tcStr, TCString.decode(tcStrEncoded));
```

The encoder attempts to catch some encoding issues such as field values that may result in overflow. It is the users 
responsibility to ensure that the encoded strings are compliant according to the iabtcf specification. 


#### GVL & CMP List

The `iabtcf-extras` and `iabtcf-extras-jackson` libraries provides an interface and ability to parse the GVL and CMP 
List respectively. The `iabtcf-extras-jackson` library uses Jackson 2.10.3 to parse the GVL and CMP List JSON.

```
<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-extras</artifactId>
  <version>VERSION</version>
</dependency>

<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-extras-jackson</artifactId>
  <version>VERSION</version>
</dependency>
```


Example of parsing the GVL,

```
import com.iabtcf.extras.jackson.Loader;
import com.iabtcf.extras.gvl.Gvl;

String gvlContent = "...";
Loader loader = new Loader();
Gvl gvl = loader.globalVendorList(gvlContent); 
```

Example of parsing the CMP List,

```
import com.iabtcf.extras.jackson.Loader;
import com.iabtcf.extras.cmp.CmpList;

String cmpListContent = "...";
Loader loader = new Loader();
CmpList cmpList = loader.cmpList(cmpListContent); 
```

### About the Transparency & Consent Framework <a name="aboutTCframework"></a>

IAB Europe Transparency & Consent Framework (TCF) has a simple objective to help all parties in the digital advertising chain ensure that they comply with the EU’s General Data Protection Regulation and ePrivacy Directive when processing personal data or accessing and/or storing information on a user’s device, such as cookies, advertising identifiers, device identifiers and other tracking technologies. IAB Tech Lab stewards the development of these technical specifications.

Resources including policy FAQ, Global Vendor List, and CMP List can be found at [iabeurope.eu/tcf](http://iabeurope.eu/tcf).
