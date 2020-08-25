package amidst.mojangapi.world.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;

import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.versionfeatures.DefaultVersionFeatures;
import amidst.mojangapi.world.versionfeatures.FeatureKey;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;

public class VersionFeaturesTest {

	@Test
	public void shouldHaveAllRequiredFeatures() throws IllegalAccessException {
		List<FeatureKey<?>> requiredFeatures = getRequiredFeatures();
		VersionFeatures.Builder versionFeaturesBuilder = createVersionFeaturesBuilder();

		for (RecognisedVersion recognisedVersion: RecognisedVersion.values()) {
			VersionFeatures versionFeatures = versionFeaturesBuilder.create(recognisedVersion);
			for (FeatureKey<?> requiredFeature: requiredFeatures) {
				// will throw if the feature doesn't exist or is null.
				versionFeatures.get(requiredFeature);
			}
		}
	}

	public VersionFeatures.Builder createVersionFeaturesBuilder() {
		WorldOptions worldOptions = new WorldOptions(WorldSeed.fromSaveGame(0), WorldType.DEFAULT);
		MinecraftInterface.World minecraftWorld = new MockMinecraftWorld();
		return DefaultVersionFeatures.builder(worldOptions, minecraftWorld);
	}

	public List<FeatureKey<?>> getRequiredFeatures() throws IllegalAccessException {
		List<FeatureKey<?>> features = new ArrayList<>();
		for (Field field: FeatureKey.class.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
				if (field.getType().isAssignableFrom(FeatureKey.class)) {
					features.add((FeatureKey<?>) field.get(null));
				}
			}
		}
		return features;
	}

	private static class MockMinecraftWorld implements MinecraftInterface.World {
		@Override
		public<T> T getBiomeData(Dimension dimension, int x, int y, int width, int height, boolean useQuarterResolution,
				Function<int[], T> biomeDataMapper) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Dimension> supportedDimensions() {
			return EnumSet.allOf(Dimension.class);
		}
	}
}
