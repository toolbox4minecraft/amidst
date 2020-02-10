package amidst.mojangapi.world.versionfeatures;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@NotThreadSafe
public class VersionFeatureBuilder<V> {
	private V defaultValue = null;
	private RecognisedVersion previous = null;
	private final List<VersionFeature.Entry<V>> entriesNewestFirst = new LinkedList<>();

	public VersionFeatureBuilder<V> init(V defaultValue) {
		if (this.defaultValue == null) {
			this.defaultValue = defaultValue;
			return this;
		} else {
			throw new IllegalStateException("only one default value is allowed");
		}
	}

	public VersionFeatureBuilder<V> since(RecognisedVersion version, V value) {
		Objects.requireNonNull(version, "version cannot be null");
		if (this.defaultValue == null) {
			throw new IllegalStateException("you need to specify a default value first");
		} else if (previous != null && version == previous) {
			throw new IllegalStateException("you can only specify one value per version");
		} else if (previous != null && RecognisedVersion.isOlder(version, previous)) {
			throw new IllegalStateException("you have to specify versions in ascending order");
		} else {
			previous = version;
			entriesNewestFirst.add(0, new VersionFeature.Entry<>(version, value));
			return this;
		}
	}

	public VersionFeature<V> construct() {
		return new VersionFeature<>(
				defaultValue,
				Collections.unmodifiableList(entriesNewestFirst));
	}
}
