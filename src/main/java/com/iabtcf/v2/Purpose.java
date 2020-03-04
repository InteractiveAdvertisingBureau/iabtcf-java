package com.iabtcf.v2;

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

@SuppressWarnings("unused")
public enum Purpose {

	STORE_AND_ACCESS_INFO_ON_DEVICE(1),
	SELECT_BASIC_ADS(2),
	CREATE_PERSONALISED_ADS_PROFILE(3),
	SELECT_PERSONAL_ADS(4),
	CREATE_PERSONAL_CONTENT_PROFILE(5),
	SELECT_PERSONALISED_CONTENT(6),
	MEASURE_AD_PERFORMANCE(7),
	MEASURE_CONTENT_PERFORMANCE(8),
	APPLY_MARKET_RESEARCH_TO_GENERATE_AUDIENCE_INSIGHTS(9),
	DEVELOP_AND_IMPROVE_PRODUCTS(10),
	;

	private final int id;

	Purpose(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
