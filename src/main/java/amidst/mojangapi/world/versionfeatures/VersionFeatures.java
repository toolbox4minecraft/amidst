package amidst.mojangapi.world.versionfeatures;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@Immutable
public class VersionFeatures {
	private final Map<FeatureKey<?>, Object> features;

	private VersionFeatures(Map<FeatureKey<?>, Object> features) {
		this.features = features;
	}

	@SuppressWarnings("unchecked")
	public<T> T get(FeatureKey<T> key) {
		if (!features.containsKey(key)) {
			throw new IllegalArgumentException("unknown feature " + key);
		}
		return (T) features.get(key);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final Map<FeatureKey<?>, VersionFeature<?>> featureFactories = new IdentityHashMap<>();

		private Builder() {
		}

		public<T> Builder with(FeatureKey<T> key, VersionFeature<T> feature) {
			Objects.requireNonNull(key);
			Objects.requireNonNull(feature);
			if (featureFactories.containsKey(key)) {
				throw new IllegalArgumentException("the feature " + key + " was already set");
			}
			featureFactories.put(key, feature);
			return this;
		}

		public VersionFeatures create(RecognisedVersion version) {
			Map<FeatureKey<?>, Object> features = new IdentityHashMap<>();
			for (Map.Entry<FeatureKey<?>, VersionFeature<?>> entry: featureFactories.entrySet()) {
				features.put(entry.getKey(), entry.getValue().getValue(version));
			}
			return new VersionFeatures(features);
		}
	}
}
