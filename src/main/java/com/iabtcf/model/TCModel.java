package com.iabtcf.model;

import java.time.Instant;

public interface TCModel {

    /**
     * @return the version of consent string format
     */
    int version();

    /**
     * @return the {@link Instant} at which the consent string was created
     */
    Instant consentRecordCreated();

    /**
     * @return the {@link Instant} at which consent string was last updated
     */
    Instant consentRecordLastUpdated();
}
