package amidst.mojangapi.world.versionfeatures;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class VersionFeature<V> {
	public static <V> VersionFeatureBuilder<V> builder() {
		return new VersionFeatureBuilder<>();
	}

	public static <V> ListVersionFeatureBuilder<V> listBuilder() {
		return new ListVersionFeatureBuilder<>();
	}

	private final V defaultValue;
	private final List<Entry<V>> entriesNewestFirst;

	public VersionFeature(
			V defaultValue,
			List<Entry<V>> entriesNewestFirst) {
		this.defaultValue = defaultValue;
		this.entriesNewestFirst = entriesNewestFirst;
	}

	public V getValue(RecognisedVersion version) {
		Entry<V> entry = tryFindGreatestEntryLowerOrEqualTo(version);
		if (entry != null) {
			return entry.getValue();
		}
		return defaultValue;
	}

	private Entry<V> tryFindGreatestEntryLowerOrEqualTo(RecognisedVersion version) {
		for (Entry<V> entry : entriesNewestFirst) {
			if (RecognisedVersion.isOlderOrEqualTo(entry.getVersion(), version)) {
				return entry;
			}
		}
		return null;
	}

	@Immutable
	public static class Entry<V> {
		private final RecognisedVersion version;
		private final V value;

		public Entry(RecognisedVersion version, V value) {
			this.version = version;
			this.value = value;
		}

		public RecognisedVersion getVersion() {
			return version;
		}

		public V getValue() {
			return value;
		}
	}
}
