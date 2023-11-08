package com.iabtcf.extras.jackson.gvl;

import java.util.Optional;

/*-
 * #%L
 * IAB TCF Java GVL Jackson
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

/*
 * This interface allow for vendors to declare multiple URLs for their privacy policies and explanation of their
 * legitimate interests at stake
 * @since 3.0
 */
public class VendorUrl implements com.iabtcf.extras.gvl.VendorUrl {

    private String langId;
    private String privacy;
    private String legIntClaim;

    /**
     * langId
     *
     * @return lang id
     */
    @Override
    public String getLangId() {
        return langId;
    }

    /**
     * privacy
     *
     * @return privacy
     */
    @Override
    public String getPrivacy() {
        return privacy;
    }

    /**
     * legIntClaim
     *
     * @return legIntClaim
     */
    @Override
    public Optional<String> getLegIntClaim() {
        return Optional.ofNullable(legIntClaim);
    }
}
