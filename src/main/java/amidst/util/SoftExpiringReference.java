package amidst.util;

import java.lang.ref.SoftReference;

public class SoftExpiringReference<T> {
	private long startTime = System.currentTimeMillis();
	private final long maxLifeTimeMillis;
	private final SoftReference<T> valueRef;

	public SoftExpiringReference(T value, long maxLifeTimeMillis) {
		this.maxLifeTimeMillis = maxLifeTimeMillis;
		this.valueRef = new SoftReference<>(value);
	}

	public T getValue() {
		return valueRef.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SoftExpiringReference<T> other = (SoftExpiringReference<T>) obj;
		if (this.getValue() != other.getValue()
				&& (this.getValue() == null || !this.getValue().equals(other.getValue()))) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (this.getValue() != null ? this.getValue().hashCode() : 0);
		return hash;
	}

	public long getDelayMillis() {
		return (startTime + maxLifeTimeMillis) - System.currentTimeMillis();
	}

	public void renew() {
		startTime = System.currentTimeMillis();
	}

	public void expire() {
		startTime = Long.MIN_VALUE;
	}
}