package com.iabtcf;

import java.util.Set;

/**
 * @author evanwht1
 */
public interface PublisherTC {

	Set<Integer> getPurposesConsent();

	Set<Integer> getPurposesLITransparency();

	Set<Integer> getCustomPurposesConsent();

	Set<Integer> getCustomPurposesLITransparency();
}
