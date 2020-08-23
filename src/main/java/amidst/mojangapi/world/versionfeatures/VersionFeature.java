package amidst.mojangapi.world.versionfeatures;

import java.util.function.BiFunction;
import java.util.function.Function;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@FunctionalInterface
public interface VersionFeature<V> {
	public V getValue(RecognisedVersion version, VersionFeatures features);

	public static<V> VersionFeatureBuilder<V, ?> builder() {
		return new VersionFeatureBuilder<>();
	}

	public static<V> VersionFeatureListBuilder<V, ?> listBuilder() {
		return new VersionFeatureListBuilder<>();
	}

	public static<V> VersionFeature<V> constant(V value) {
		return (version, features) -> value;
	}

	public static<V> VersionFeature<V> fixed(Function<VersionFeatures, V> factory) {
		return (version, features) -> factory.apply(features);
	}

	public default<W> VersionFeature<W> andThen(Function<V, W> mapper) {
		return (version, features) -> mapper.apply(this.getValue(version, features));
	}

	public default<W> VersionFeature<W> andThenFixed(BiFunction<VersionFeatures, V, W> mapper) {
		return (version, features) -> mapper.apply(features, this.getValue(version, features));
	}
}
