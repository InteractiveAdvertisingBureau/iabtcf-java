package com.iabtcf;

/**
 * @author evanwht1
 */
public enum Feature {

	MATCH_AND_COMBINE_OFFLINE_DATA_SOURCES(1),
	LINK_DIFFERENT_DEVICES(2),
	RECEIVE_AND_USE_AUTOMATICALLY_SENT_DEVICE_CHARACTERISTICS_FOR_IDENTIFICATION(3),
	;

	private final int id;

	Feature(final int id) {
		this.id = id;
	}
}
