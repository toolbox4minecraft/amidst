package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.versionfeatures.VersionFeature.Entry;

@NotThreadSafe
public class VersionFeatureBuilder<V, B extends VersionFeatureBuilder<V, B>> {
	private RecognisedVersion previousVersion;
	private List<Entry<V>> entriesOldestFirst;

	public VersionFeatureBuilder() {
		this.previousVersion = null;
		this.entriesOldestFirst = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	protected final B self() {
		return (B) this;
	}

	public B init(Function<VersionFeatures, V> defaultValueSupplier) {
		if (!this.entriesOldestFirst.isEmpty()) {
			throw new IllegalStateException("only one default value is allowed");
		}
		this.entriesOldestFirst.add(Entry.defaultValue(defaultValueSupplier));
		return self();
	}

	public B init(V defaultValue) {
		return init(features -> defaultValue);
	}

	private B addEntry(Entry<V> entry) {
		if (this.entriesOldestFirst.isEmpty()) {
			throw new IllegalStateException("you must specify a default value first");
		} else if (previousVersion != null && entry.getVersion() == previousVersion) {
			throw new IllegalStateException("you can only specify one value per version");
		} else if (previousVersion != null && RecognisedVersion.isOlderOrEqualTo(entry.getVersion(), previousVersion)) {
			throw new IllegalStateException("you have to specify versions in ascending order");
		}
		entriesOldestFirst.add(entry);
		previousVersion = entry.getVersion();
		return self();
	}

	public B since(RecognisedVersion version, Function<VersionFeatures, V> valueSupplier) {
		return addEntry(Entry.since(version, valueSupplier));
	}

	public B since(RecognisedVersion version, V value) {
		return since(version, feature -> value);
	}

	public B sinceUpdate(RecognisedVersion version, BiFunction<VersionFeatures, V, V> valueUpdater) {
		return addEntry(Entry.sinceUpdate(version, valueUpdater));
	}

	public B sinceUpdate(RecognisedVersion version, Function<V, V> valueUpdater) {
		return sinceUpdate(version, (features, oldValue) -> valueUpdater.apply(oldValue));
	}

	public VersionFeature<V> construct() {
		if (this.entriesOldestFirst.isEmpty()) {
			throw new IllegalStateException("you must specify a default value first");
		}
		return new VersionFeature<>(entriesOldestFirst.subList(0, entriesOldestFirst.size()));
	}
}