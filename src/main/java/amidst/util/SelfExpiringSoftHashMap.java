/*
 * Copyright (c) 2019 Pierantonio Cangianiello
 * 
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package amidst.util;

import java.lang.ref.SoftReference;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import amidst.documentation.ThreadSafe;

/**
 * A HashMap which entries expires after the specified life time. The
 * life-time can be defined on a per-value basis, or using a default
 * one, that is passed to the constructor.<br>
 * <br>
 * This class was completely refactored to use SoftReferences for use
 * with amidst.
 * 
 * @author Pierantonio Cangianiello
 * @param <K> the Key type
 * @param <V> the Value type
 */
@ThreadSafe
public class SelfExpiringSoftHashMap<K, V> implements Map<K, V> {

	private final Map<K, SoftExpiringValue> internalMap;

	/**
	 * The default max life time in milliseconds.
	 */
	private final long maxLifeTimeMillis;

	public SelfExpiringSoftHashMap() {
		internalMap = new ConcurrentHashMap<>();
		this.maxLifeTimeMillis = Long.MAX_VALUE;
	}

	public SelfExpiringSoftHashMap(long defaultMaxLifeTimeMillis) {
		internalMap = new ConcurrentHashMap<>();
		this.maxLifeTimeMillis = defaultMaxLifeTimeMillis;
	}

	public SelfExpiringSoftHashMap(long defaultMaxLifeTimeMillis, int initialCapacity) {
		internalMap = new ConcurrentHashMap<>(initialCapacity);
		this.maxLifeTimeMillis = defaultMaxLifeTimeMillis;
	}

	public SelfExpiringSoftHashMap(long defaultMaxLifeTimeMillis, int initialCapacity, float loadFactor) {
		internalMap = new ConcurrentHashMap<>(initialCapacity, loadFactor);
		this.maxLifeTimeMillis = defaultMaxLifeTimeMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return internalMap.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(Object key) {
		return internalMap.containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		SoftExpiringValue valueRef = internalMap.get(key);
		renewValue(valueRef);
		return valueRef != null ? valueRef.getValue() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V put(K key, V value) {
		return this.put(key, value, maxLifeTimeMillis);
	}

	/**
	 * Associates the given key to the given value in this map, with the
	 * specified life times in milliseconds.
	 *
	 * @param key
	 * @param value
	 * @param lifeTimeMillis
	 * @return a previously associated object for the given key (if
	 *         exists).
	 */
	public V put(K key, V value, long lifeTimeMillis) {
		SoftExpiringValue newValue = new SoftExpiringValue(value, lifeTimeMillis);
		SoftExpiringValue oldValue = internalMap.put(key, newValue);
		if (oldValue != null) {
			expireValue(oldValue);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V remove(Object key) {
		SoftExpiringValue removedValue = internalMap.remove(key);
		expireValue(removedValue);
		return removedValue != null ? removedValue.getValue() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		putAll(m, maxLifeTimeMillis);
	}

	public void putAll(Map<? extends K, ? extends V> m, long lifeTimeMillis) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			SoftExpiringValue newValue = new SoftExpiringValue(entry.getValue(), lifeTimeMillis);
			SoftExpiringValue oldValue = internalMap.put(entry.getKey(), newValue);
			if (oldValue != null) {
				expireValue(oldValue);
			}
		}
	}

	/**
	 * Renews the specified value, setting the life time to the initial
	 * value.
	 *
	 * @param value
	 * @return true if the value is found and hasn't been dereferenced,
	 *         false otherwise
	 */
	public boolean renewValue(SoftExpiringValue valueRef) {
		if (valueRef != null && valueRef.getValue() != null) {
			valueRef.renew();
			return true;
		}
		return false;
	}

	private void expireValue(SoftExpiringValue valueRef) {
		if (valueRef != null) {
			valueRef.expire();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		internalMap.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<K> keySet() {
		return internalMap.keySet();
	}

	/**
	 * <b>WARNING:</b> This implementation does not reflect the changes
	 * made on the return value in the original Map.<br>
	 * <br>
	 * {@inheritDoc}
	 */
	@Override
	public Collection<V> values() {
		return Collections.synchronizedSet(internalMap.values().stream().map(sv -> sv.getValue()).filter(v -> v != null).collect(Collectors.toSet()));
	}

	/**
	 * <b>WARNING:</b> This implementation does not reflect the changes
	 * made on the return value in the original Map.<br>
	 * <br>
	 * {@inheritDoc}
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return Collections.synchronizedSet(internalMap.entrySet().stream().map(e -> {
			V realValue = e.getValue().getValue();
			if (realValue != null) {
				return new SimpleEntry<>(e.getKey(), realValue);
			}
			return null;
		}).filter(e -> e != null).collect(Collectors.toSet()));
	}

	public synchronized void clean() {
		Iterator<SoftExpiringValue> valueRefIterator = internalMap.values().iterator();
		for (SoftExpiringValue valueRef : (Iterable<SoftExpiringValue>) () -> valueRefIterator) {
			V realValue = valueRef.getValue();
			if (realValue == null || valueRef.getDelayMillis() < 0) {
				valueRefIterator.remove();
			}
		}
	}

	private class SoftExpiringValue {
		private long startTime = System.currentTimeMillis();
		private final long maxLifeTimeMillis;
		private final SoftReference<V> valueRef;

		public SoftExpiringValue(V value, long maxLifeTimeMillis) {
			this.maxLifeTimeMillis = maxLifeTimeMillis;
			this.valueRef = new SoftReference<>(value);
		}

		public V getValue() {
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
			final SoftExpiringValue other = (SoftExpiringValue) obj;
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
}
