package com.iabtcf.model;

import java.time.Instant;
import java.util.SortedSet;

import com.iabtcf.model.ConsentLanguage;
import com.iabtcf.model.PurposeRestriction;
import com.iabtcf.model.PurposeRestrictionType;

public interface TCModel {

    ConsentLanguage static getConsentLanguages();

    /**
     * both of these are populated by decoding takes that value, otherwise creates timestamp of 'now'
     */
    Instant getCreated();
    Instant getLastUpdated();

    /**
     * Each of these SortedSet<int> represents a bitfield in the TC String.  Keeping the set sorted is essential for
     * encoding and using the SortedSet sublass of set creates a last() function which will return the highest id
     * (this corresponds to the maxVendorID)
     */
    // Global TC Vectors
    SortedSet<int> getSpecialFeatureOptIns();
    SortedSet<int> getPurposeConsents();
    SortedSet<int> getPurposeLegitimateInterests();
    // Publisher TC
    SortedSet<int> getPublisherConsents();
    SortedSet<int> getPublisherLegitimateInterests();
    // Vendors
    SortedSet<int> getVendorConsents();
    SortedSet<int> getVendorLegitimateInterests();
    // OOB
    SortedSet<int> getVendorsDisclosed();
    SortedSet<int> getVendorsAllowed();

    /**
     *  Publisher Restrictions is a special case because you have a combination of RestrictionType and PurposeId with
     *  a list of vendors under that restriction.
     */
    Map<int, SortedSet<int>> getPublisherRestrictions();

    void setGVL(gvl: GVL);
    GVL getGVL();

    void setCmpId(int id);
    int getCmpId();

    void setCmpVersion(int id);
    int getCmpVersion();

    void setConsentScreen(int id);
    int getConsentScreen();

    void setConsentLanguage(ConsentLanguage lang);
    ConsentLanguage getConsentLanguage();

    void setPublisherCountryCode(String country);
    String getPublisherCountryCode();

    void setVendorListVersion(int id);
    int getVendorListVersion();

    void setPolicyVersion(int id);
    int getPolicyVersion();

    // refers to encoding version
    void setVersion(int id);
    int getVersion();

    void setUseNonStandardStacks(boolean value);
    boolean getUseNonStandardStacks();

    // only need a getter because this is interpreted from decoding a string.  A java environment won't need to explicitly set this
    boolean getSupportOOB();

    void setPurposeOneTreatment(boolean value);
    boolean getPurposeOneTreatment();

    // sets lastUpdated time to now
    void update();

}
