package amidst.map.finder;

import java.util.Arrays;
import java.util.List;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.preferences.BooleanPrefModel;

public class OceanMonumentFinder extends StructureFinder {
	// @formatter:off
	// Not sure if the extended biomes count
	private static final List<Biome> VALID_SURROUNDING_BIOMES = Arrays.asList(
			Biome.ocean,
			Biome.deepOcean,
			Biome.frozenOcean,
			Biome.river,
			Biome.frozenRiver,
			Biome.oceanM,
			Biome.deepOceanM,
			Biome.frozenOceanM,
			Biome.riverM,
			Biome.frozenRiverM
	);
	// @formatter:on

	private static final int STRUCTURE_SIZE = 29;

	@Override
	protected MapObject getMapObject(BooleanPrefModel isVisiblePreference,
			boolean isSuccessful, int middleOfChunkX, int middleOfChunkY,
			int x, int y) {
		if (isSuccessful) {
			if (isValid(middleOfChunkX, middleOfChunkY)) {
				return createMapObject(isVisiblePreference, x << 4, y << 4);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean isValid(int middleOfChunkX, int middleOfChunkY) {
		// Note that getBiomeAt() is full-resolution biome data, while
		// isValidBiome() is calculated using
		// quarter-resolution biome data. This is identical to how Minecraft
		// calculates it.
		Biome biome = MinecraftUtil.getBiomeAt(middleOfChunkX, middleOfChunkY);
		boolean isValid = MinecraftUtil.isValidBiome(middleOfChunkX,
				middleOfChunkY, STRUCTURE_SIZE, VALID_SURROUNDING_BIOMES);
		return validBiomes.contains(biome) && isValid;
	}

	private MapObject createMapObject(BooleanPrefModel isVisiblePreference,
			int x, int y) {
		return MapObject.fromFragmentCoordinates(isVisiblePreference,
				MapMarkers.OCEAN_MONUMENT, x, y);
	}

	@Override
	protected List<Biome> getValidBiomes() {
		// @formatter:off
		// Not sure if the extended biomes count
		return Arrays.asList(
				Biome.deepOcean
		//		Biome.deepOceanM
		);
		// @formatter:on
	}

	@Override
	protected int updateValue(int value) {
		value *= maxDistanceBetweenScatteredFeatures;
		value += (random.nextInt(distanceBetweenScatteredFeaturesRange) + random
				.nextInt(distanceBetweenScatteredFeaturesRange)) / 2;
		return value;
	}

	@Override
	protected long getMagicNumberForSeed1() {
		return 341873128712L;
	}

	@Override
	protected long getMagicNumberForSeed2() {
		return 132897987541L;
	}

	@Override
	protected long getMagicNumberForSeed3() {
		return 10387313L;
	}

	@Override
	protected byte getMaxDistanceBetweenScatteredFeatures() {
		return 32;
	}

	@Override
	protected byte getMinDistanceBetweenScatteredFeatures() {
		return 5;
	}

	@Override
	protected int getSize() {
		return Fragment.SIZE >> 4;
	}
}
