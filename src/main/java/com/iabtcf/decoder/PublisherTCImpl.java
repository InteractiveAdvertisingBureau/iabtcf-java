package com.iabtcf.decoder;

import com.iabtcf.PublisherTC;

import java.util.Objects;
import java.util.Set;

/**
 * @author evanwht1
 */
class PublisherTCImpl implements PublisherTC {

	private final Set<Integer> publisherPurposesConsent;
	private final Set<Integer> publisherPurposesLITransparency;
	private final Set<Integer> customPurposesConsent;
	private final Set<Integer> customPurposesLITransparency;

	PublisherTCImpl(final Set<Integer> publisherPurposesConsent,
	                final Set<Integer> publisherPurposesLITransparency,
	                final Set<Integer> customPurposesConsent,
	                final Set<Integer> customPurposesLITransparency) {
		this.publisherPurposesConsent = publisherPurposesConsent;
		this.publisherPurposesLITransparency = publisherPurposesLITransparency;
		this.customPurposesConsent = customPurposesConsent;
		this.customPurposesLITransparency = customPurposesLITransparency;
	}

	@Override
	public Set<Integer> getPurposesConsent() {
		return publisherPurposesConsent;
	}

	@Override
	public Set<Integer> getPurposesLITransparency() {
		return publisherPurposesLITransparency;
	}

	@Override
	public Set<Integer> getCustomPurposesConsent() {
		return customPurposesConsent;
	}

	@Override
	public Set<Integer> getCustomPurposesLITransparency() {
		return customPurposesLITransparency;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final PublisherTCImpl that = (PublisherTCImpl) o;
		return Objects.equals(publisherPurposesConsent, that.publisherPurposesConsent) &&
		       Objects.equals(publisherPurposesLITransparency, that.publisherPurposesLITransparency) &&
		       Objects.equals(customPurposesConsent, that.customPurposesConsent) &&
		       Objects.equals(customPurposesLITransparency, that.customPurposesLITransparency);
	}

	@Override
	public int hashCode() {
		return Objects.hash(publisherPurposesConsent, publisherPurposesLITransparency, customPurposesConsent, customPurposesLITransparency);
	}

	@Override
	public String toString() {
		return "PublisherTCImpl{" +
		       "publisherPurposesConsent: " + publisherPurposesConsent +
		       ", publisherPurposesLITransparency: " + publisherPurposesLITransparency +
		       ", customPurposesConsent: " + customPurposesConsent +
		       ", customPurposesLITransparency: " + customPurposesLITransparency +
		       '}';
	}
}
