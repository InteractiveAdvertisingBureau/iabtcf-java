
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

Clone the project into your local repository. Run the below command from the project directory to install the project locally.
```
mvn clean install
```

#### Using
```java
import com.iabtcf.TCModel;
import com.iabtcf.TCString;
public class IabTCFCore {
    // Sample TC STring to be decoded
    private String base64TcString = "";

    @Test
    public void readTcString() {
        TCString tcString = TCString.getInstance();
        // take an encoded TC string and decode into a TCModel
        final TCModel tcModel = tcString.decode(base64TcString);
    }
}
```
**Note:-** The current library only support decoding of TCString(consent String) to [populate](#using) the TCModel.


### SortedVectors

The [`TCModel`]() leverages a [`SortedSet`](https://docs.oracle.com/javase/7/docs/api/java/util/SortedSet.html) style [`Vector`]() data structure to set consents, optins, allowed, disclosed, and legitimate interest establishment.  Properties that leverage this data structure are:
 - **Vendors**
   - [`vendorConsents`](#vendorconsents)
   - [`vendorLegitimateInterest`](#vendorlegitimateinterest)
   - [`vendorsAllowed`](#vendorsallowed)
   - [`vendorsDisclosed`](#vendorsdisclosed)
 - **Global Purposes**
   - [`purposeConsents`](#purposeconsents)
   - [`purposeLegitimateInterest`](#legitimateinterest)
 - **Special Feature Opt-Ins**
   - [`specialFeatureOptIns`](#specialfeatureoptins)
 - **Publisher**
   - [`publisherConsents`](#publisherconsents)
   - [`publisherCustomConsents`](#publishercustomconsents)
   - [`publisherLegitimateInterest`](#publisherlegitimateinterest)
   - [`publisherCustomLegitimateInterest`](#publishercustomlegitimateInterest)
   - [`publisherRestrictions`](#publisherrestrictions)
     - This Vector is a special [`PurposeRestrictionVector`]() of [`PurposeRestrictions`]()

**Example with `vendorConsents`**

The `vendorConsents` property on the `TCModel` is a [`SortedVector`]().  This example illustrates the methods of a [`SortedVector`](). With the exception of the `publisherRestrictions`, which implements a different type of [`PurposeRestrictionVector`](), all of the above Vectors will have this interface and functionality.

```java
// Give Vendor ID 24 consent
SortedVector vendorIds = new SortedVector();
TCModel tcModel = new TCModel();
// Sorted vector is a class with reverse sorted SortedSet elemenets  
vendorIds.getSet().add(24);
TCModelEnum.valueOf("vendorConsents").setValue(tcModel,vendorIds); // Using the TCModelEnum class to assign it.
// OR
// tcModel.setVendorConsents(vendorIds);
System.out.println(tcModel.getVendorConsents().getSet().contains(24)); // true
System.out.println(tcModel.getVendorConsents().getSet().first()); // 24 
//first() returns the Maximum element since set is reversely sorted.
System.out.println(tcModel.getVendorConsents().getSet().size()); // 1

// remove vendor 24
tcModel.getVendorConsents().getSet().remove(24)
System.out.println(tcModel.getVendorConsents().getSet().contains(24)); // false
System.out.println(tcModel.getVendorConsents().getSet().first()); // 0
System.out.println(tcModel.getVendorConsents().getSet().size()); // 0

// give a group of vendors consent
tcModel.getVendorConsents().getSet().add(14);
tcModel.getVendorConsents().getSet().add(24);
tcModel.getVendorConsents().getSet().add(100);
tcModel.getVendorConsents().getSet().add(102);
System.out.println(tcModel.getVendorConsents().getSet().contains(24)); // true
System.out.println(tcModel.getVendorConsents().getSet().contains(14)); // true
System.out.println(tcModel.getVendorConsents().getSet().contains(200)); // false
System.out.println(tcModel.getVendorConsents().getSet().first()); // 102
System.out.println(tcModel.getVendorConsents().getSet().size()); // 5

// loop through all ids 1 to maxId (102 loops)
for(int i=1;i<=tcModel.getVendorConsents().getSet().first();i++) {
     // check each id for consent

};

// empty everything
tcModel.getVendorConsents().getSet().clear();

System.out.println(tcModel.getVendorConsents().getSet().contains(24)); // false
System.out.println(tcModel.getVendorConsents().getSet().first()); // 0
System.out.println(tcModel.getVendorConsents().getSet().size()); // 0
```

### Setting Publisher Restrictions

A [Publisher Restriction](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#what-are-publisher-restrictions) is a restriction placed on a Vendor by a publisher limiting the purposes for which that Vendor is allowed to process personal data.  The `TCModel.publisherRestrictions` is an instance of the [`PurposeRestrictionVector`](), which is a vector containing [`PurposeRestrictions`]()'s.

**Example of setting publisher restrictions**

```java
import com.iabtcf.model.PurposeRestriction;
import com.iabtcf.model.RestrictionType;
import com.iabtcf.TCModel;

// first you must create a PurposeRestriction
PurposeRestriction purposeRestriction = new PurposeRestriction(purposeId, RestrictionType.NOT_ALLOWED);

// vendorID and restriction
tcModel.getPublisherRestrictions().add(2000, purposeRestriction);

```