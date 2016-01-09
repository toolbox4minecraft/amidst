package amidst.mojangapi.world.versionfeatures;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class VersionFeatureEntry<V> {
	private final RecognisedVersion version;
	private final V value;

	public VersionFeatureEntry(RecognisedVersion version, V value) {
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
