package amidst.mojangapi.world.versionfeatures;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class VersionFeatureImpl<V> implements VersionFeature<V> {
	private final List<Entry<V>> entriesOldestFirst;

	public VersionFeatureImpl(List<Entry<V>> entriesOldestFirst) {
		if (!entriesOldestFirst.isEmpty()) {
			Entry<?> first = entriesOldestFirst.get(0);
			if (first.version == null && first.valueSupplier != null) {
				this.entriesOldestFirst = entriesOldestFirst;
				return;
			}
		}
		throw new IllegalArgumentException("invalid entry list");
	}

	@Override
	public V getValue(RecognisedVersion version, VersionFeatures features) {
		Iterator<Entry<V>> entries = getEntriesToApply(version);

		V value = entries.next().createValue(version, features);
		while (entries.hasNext()) {
			value = entries.next().updateValue(version, features, value);
		}

		return value;
	}

	private Iterator<Entry<V>> getEntriesToApply(RecognisedVersion version) {
		int until = entriesOldestFirst.size();

		Entry<?> entry;
		do {
			entry = entriesOldestFirst.get(--until);
		} while (!entry.isApplicableTo(version));

		int from = until;
		do {
			entry = entriesOldestFirst.get(from--);
		} while (entry.needsPreviousValue());

		return entriesOldestFirst.subList(from+1, until+1).iterator();
	}

	@Immutable
	public static class Entry<V> {
		private final RecognisedVersion version;
		private final VersionFeature<V> valueSupplier;
		private final Function<V, VersionFeature<V>> valueUpdater;

		private Entry(RecognisedVersion version, VersionFeature<V> supplier, Function<V, VersionFeature<V>> updater) {
			this.version = version;
			this.valueSupplier = supplier;
			this.valueUpdater = updater;
		}

		public static<V> Entry<V> defaultValue(VersionFeature<V> valueSupplier) {
			return new Entry<>(null, Objects.requireNonNull(valueSupplier), null);
		}

		public static<V> Entry<V> since(RecognisedVersion version, VersionFeature<V> valueSupplier) {
			return new Entry<>(Objects.requireNonNull(version), Objects.requireNonNull(valueSupplier), null);
		}

		public static<V> Entry<V> sinceUpdate(RecognisedVersion version, Function<V, VersionFeature<V>> valueUpdater) {
			return new Entry<>(Objects.requireNonNull(version), null, Objects.requireNonNull(valueUpdater));
		}

		public RecognisedVersion getVersion() {
			return this.version;
		}

		public boolean isApplicableTo(RecognisedVersion version) {
			return this.version == null || RecognisedVersion.isOlderOrEqualTo(this.version, version);
		}

		public boolean needsPreviousValue() {
			return this.valueSupplier == null;
		}

		public V createValue(RecognisedVersion version, VersionFeatures features) {
			V value = this.valueSupplier.getValue(version, features);
			return Objects.requireNonNull(value, "feature value cannot be null");
		}

		public V updateValue(RecognisedVersion version, VersionFeatures features, V oldValue) {
			V value = this.valueUpdater.apply(oldValue).getValue(version, features);
			return Objects.requireNonNull(value, "feature value cannot be null");
		}
	}
}
