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
	private final List<VersionFeatureEntry<V>> exactMatches;
	private final List<VersionFeatureEntry<V>> entriesNewestFirst;

	public VersionFeature(
			V defaultValue,
			List<VersionFeatureEntry<V>> exactMatches,
			List<VersionFeatureEntry<V>> entriesNewestFirst) {
		this.defaultValue = defaultValue;
		this.exactMatches = exactMatches;
		this.entriesNewestFirst = entriesNewestFirst;
	}

	public V getValue(RecognisedVersion version) {
		VersionFeatureEntry<V> entry;
		entry = tryFindExactMatch(version);
		if (entry != null) {
			return entry.getValue();
		}
		entry = tryFindGreatestEntryLowerOrEqualTo(version);
		if (entry != null) {
			return entry.getValue();
		}
		return defaultValue;
	}

	private VersionFeatureEntry<V> tryFindExactMatch(RecognisedVersion version) {
		for (VersionFeatureEntry<V> entry : exactMatches) {
			if (entry.getVersion() == version) {
				return entry;
			}
		}
		return null;
	}

	private VersionFeatureEntry<V> tryFindGreatestEntryLowerOrEqualTo(RecognisedVersion version) {
		for (VersionFeatureEntry<V> entry : entriesNewestFirst) {
			if (RecognisedVersion.isOlderOrEqualTo(entry.getVersion(), version)) {
				return entry;
			}
		}
		return null;
	}
}
