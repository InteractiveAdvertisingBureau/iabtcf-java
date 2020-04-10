# iabtcf-java

This library is currently in alpha release. 

Encode consent information with the IAB EU's GDPR Transparency and Consent Framework v2.0.

The IAB specification for the consent string format is available on the [IAB Github](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/tree/master/TCFv2).

**This library supports the version v2.0 of the specification. It can decode consent strings with version bit 2.**

### IAB Europe Transparency and Consent Framework v2
Version 2 of the TCF Specifications were released 21 August 2019 with industry adoption commencing first half of 2020.

Framework Technical specifications available at: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/tree/master/TCFv2 

### Usage

#### Maven

The official iabtcf java library is distributed through maven central. Please [search maven central](https://search.maven.org/search?q=a:iabtcf-core) for the current release version.

#### Decoding

The latest version of the library support decoding both v1 and v2 strings using the same interface. Decoding requires the iabtcf-decoder module dependency,

```
<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-encoder</artifactId>
  <version>VERSION</version>
</dependency>
```

Example of decoding a consent string,

```
import com.iabtcf.decoder.TCString;

TCString tcString = TCString.decode("COwxsONOwxsONKpAAAENAdCAAMAAAAAAAAAAAAAAAAAA");
tcString.getVersion() => 2
tcString.getVendorConsent().contains(10) => false
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

Example of parsing the GVL,

```
import com.iabtcf.extras.jackson.Loader;
import com.iabtcf.extras.gvl.Gvl;

String gvlContent = "...";
Loader loader = new Loader();
Gvl gvl = loader.loadGlobalVendorList(gvlContent); 
```

Example of parsing the CMP List,

```
import com.iabtcf.extras.jackson.Loader;
import com.iabtcf.extras.cmp.CmpList;

String cmpListContent = "...";
Loader loader = new Loader();
CmpList cmpList = loader.loadCmpList(cmpListContent); 
```

### About the Transparency & Consent Framework <a name="aboutTCframework"></a>

IAB Europe Transparency & Consent Framework (TCF) has a simple objective to help all parties in the digital advertising chain ensure that they comply with the EU’s General Data Protection Regulation and ePrivacy Directive when processing personal data or accessing and/or storing information on a user’s device, such as cookies, advertising identifiers, device identifiers and other tracking technologies. IAB Tech Lab stewards the development of these technical specifications.

Resources including policy FAQ, Global Vendor List, and CMP List can be found at [iabeurope.eu/tcf](http://iabeurope.eu/tcf).
