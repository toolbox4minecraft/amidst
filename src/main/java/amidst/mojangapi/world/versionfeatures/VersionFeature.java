package amidst.mojangapi.world.versionfeatures;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class VersionFeature<V> {
	public static <V> VersionFeatureBuilder<V, ?> builder() {
		return new VersionFeatureBuilder<>();
	}

	public static <V> VersionFeatureListBuilder<V, ?> listBuilder() {
		return new VersionFeatureListBuilder<>();
	}

	private final List<Entry<V>> entriesOldestFirst;

	public VersionFeature(List<Entry<V>> entriesOldestFirst) {
		if (!entriesOldestFirst.isEmpty()) {
			Entry<V> first = entriesOldestFirst.get(0);
			if (first.version == null && first.valueSupplier != null) {
				this.entriesOldestFirst = entriesOldestFirst;
				return;
			}
		}
		throw new IllegalArgumentException("invalid entry list");
	}

	public V getValue(RecognisedVersion version, VersionFeatures features) {
		Iterator<Entry<V>> entries = getEntriesToApply(version);

		V value = entries.next().createValue(features);
		while (entries.hasNext()) {
			value = entries.next().updateValue(features, value);
		}

		return value;
	}

	private Iterator<Entry<V>> getEntriesToApply(RecognisedVersion version) {
		int until = entriesOldestFirst.size();

		Entry<V> entry;
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
		private final Function<VersionFeatures, V> valueSupplier;
		private final BiFunction<VersionFeatures, V, V> valueUpdater;

		private Entry(RecognisedVersion version, Function<VersionFeatures, V> supplier, BiFunction<VersionFeatures, V, V> updater) {
			this.version = version;
			this.valueSupplier = supplier;
			this.valueUpdater = updater;
		}

		public static<V> Entry<V> defaultValue(Function<VersionFeatures, V> valueSupplier) {
			return new Entry<>(null, Objects.requireNonNull(valueSupplier), null);
		}

		public static<V> Entry<V> since(RecognisedVersion version, Function<VersionFeatures, V> valueSupplier) {
			return new Entry<>(Objects.requireNonNull(version), Objects.requireNonNull(valueSupplier), null);
		}

		public static<V> Entry<V> sinceUpdate(RecognisedVersion version, BiFunction<VersionFeatures, V, V> valueUpdater) {
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

		public V createValue(VersionFeatures features) {
			V value = this.valueSupplier.apply(features);
			return Objects.requireNonNull(value, "feature value cannot be null");
		}

		public V updateValue(VersionFeatures features, V oldValue) {
			V value = this.valueUpdater.apply(features, oldValue);
			return Objects.requireNonNull(value, "feature value cannot be null");
		}
	}
}
