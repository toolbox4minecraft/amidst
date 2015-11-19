package amidst.map.finder;

import java.util.Arrays;
import java.util.List;

import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.preferences.BooleanPrefModel;
import amidst.version.VersionInfo;

public class TempleFinder extends StructureFinder {
	@Override
	protected MapObject getMapObject(BooleanPrefModel isVisiblePreference,
			boolean isSuccessful, int middleOfChunkX, int middleOfChunkY,
			int x, int y, Fragment fragment) {
		if (isSuccessful) {
			Biome biome = getBiome(middleOfChunkX, middleOfChunkY);
			if (validBiomes.contains(biome)) {
				return createMapObject(isVisiblePreference, biome, x << 4,
						y << 4, fragment);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private Biome getBiome(int middleOfChunkX, int middleOfChunkY) {
		// This is a potential feature biome

		// Since the structure-size that would be passed to
		// MinecraftUtil.isValidBiome()
		// is 0, we can use MinecraftUtil.getBiomeAt() here instead, which
		// tells us what kind of
		// structure it is.
		return MinecraftUtil.getBiomeAt(middleOfChunkX, middleOfChunkY);
	}

	private MapObject createMapObject(BooleanPrefModel isVisiblePreference,
			Biome chunkBiome, int x, int y, Fragment fragment) {
		if (chunkBiome == Biome.swampland) {
			return MapObject.fromFragmentCoordinatesAndFragment(
					isVisiblePreference, MapMarkers.WITCH, x, y, fragment);
		} else if (chunkBiome.name.contains("Jungle")) {
			return MapObject.fromFragmentCoordinatesAndFragment(
					isVisiblePreference, MapMarkers.JUNGLE, x, y, fragment);
		} else if (chunkBiome.name.contains("Desert")) {
			return MapObject.fromFragmentCoordinatesAndFragment(
					isVisiblePreference, MapMarkers.DESERT, x, y, fragment);
		} else {
			Log.e("No known structure for this biome type. This might be an error.");
			return null;
		}
	}

	@Override
	protected List<Biome> getValidBiomes() {
		// @formatter:off
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V1_4_2)) {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills,
					Biome.jungle,
					Biome.jungleHills,
					Biome.swampland
			);
		} else if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w22a)) {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills,
					Biome.jungle
			);
		} else {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills
			);
		}
		// @formatter:on
	}

	@Override
	protected int updateValue(int value) {
		value *= maxDistanceBetweenScatteredFeatures;
		value += random.nextInt(distanceBetweenScatteredFeaturesRange);
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
		return 14357617L;
	}

	@Override
	protected byte getMaxDistanceBetweenScatteredFeatures() {
		return 32;
	}

	@Override
	protected byte getMinDistanceBetweenScatteredFeatures() {
		return 8;
	}

	@Override
	protected int getSize() {
		return Fragment.SIZE >> 4;
	}
}
