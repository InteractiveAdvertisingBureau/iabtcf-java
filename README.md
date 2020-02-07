

# @iabtcf/core-Java

Ensures consistent of decoding of [IAB's Transparency and Consent Framework (TCF)](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework) [TC Strings](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#about-the-transparency--consent-string-tc-string) and the stateful persistence of the Transparency and Consent information while providing tools for the handling and manipulation of the [TCF](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework) [Global Vendor List](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#the-global-vendor-list) data all free and open sourced ([License](LICENSE)).

  - [Installation](#installation)
  - [Including in your project](#using)
  - [TCModel](#tcmodel) - Creates a stateful model to store a Transparency and Consent user interaction with all the fields specified in the [TC string](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#about-the-transparency--consent-string-tc-string) encoding schema.
     - [Information that is stored in a TC string (specification)](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#what-information-is-stored-in-a-tc-string)
- [GVL](#gvl) - The [Global Vendor List](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#the-global-vendor-list) contains all of the information about vendors and legal language to display to users in a CMP user interface, this component helps manage it.
- [TCModelEnum](#tcmodelenum) - The library class for implementing the dynamic getting and setting fields of the [TCModel](#tcmodel) during runtime.
- [TCString](#tcstring) - Decodes a [TC string](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#about-the-transparency--consent-string-tc-string) into a [`TCModel`](#tcmodel)
      + [Decode an IAB TC String](#using)
  
#### Installation
- Prerequisites
    + Java 1.8 
    + Maven 2.0+

Add the **com.iabtcf** dependency in your project's pom.xml. 
```xmldo
<dependency>
  <groupId>com.iabtcf</groupId>
  <artifactId>iabtcf-core</artifactId>
  <version>1.0.4-alpha.0</version>
</dependency>
```


Run the below command from the project's directory to download the dependency into your local project.
```
mvn clean install
```

#### Using
```java
import com.iabtcf.TCModel;
import com.iabtcf.TCString;
public class IabTCFCore {
    // Sample TC STring to be decoded
    private String base64TcString = ""; //version 1 or version 2 encoded string

    @Test
    public void readTcString() {
        TCString tcString = TCString.getInstance();
        // take an encoded TC string and decode into a TCModel
        final TCModel tcModel = tcString.decode(base64TcString);
    }
}
```
**Note:-** 

 1. The current library only support decoding of TCString(consent String) to [populate](#using) the TCModel.
 2. The current library supports both GDPR version 1 and version 2 decoding.
