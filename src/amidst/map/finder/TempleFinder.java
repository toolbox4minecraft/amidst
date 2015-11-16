package amidst.map.finder;

import java.util.Arrays;
import java.util.List;

import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.layer.TempleLayer;
import amidst.map.object.MapObject;
import amidst.map.object.SimpleMapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.version.VersionInfo;

public class TempleFinder extends StructureFinder<TempleLayer> {
	@Override
	protected MapObject getMapObject(boolean isSuccessful, int middleOfChunkX,
			int middleOfChunkY, int x, int y) {
		if (isSuccessful) {
			Biome biome = getBiome(middleOfChunkX, middleOfChunkY);
			if (validBiomes.contains(biome)) {
				return createMapObject(biome, x << 4, y << 4);
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

	private MapObject createMapObject(Biome chunkBiome, int x, int y) {
		if (chunkBiome == Biome.swampland) {
			return new SimpleMapObject(MapMarkers.WITCH, x, y);
		} else if (chunkBiome.name.contains("Jungle")) {
			return new SimpleMapObject(MapMarkers.JUNGLE, x, y);
		} else if (chunkBiome.name.contains("Desert")) {
			return new SimpleMapObject(MapMarkers.DESERT, x, y);
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
