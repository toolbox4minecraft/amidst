package amidst.map.layers;

import java.util.Arrays;
import java.util.List;

import amidst.map.Fragment;
import amidst.map.MapObject;
import amidst.map.MapObjectVillage;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class VillageFinder extends StructureFinder<VillageLayer> {
	private static final int STRUCTURE_SIZE = 0;

	@Override
	protected MapObject getMapObject(boolean isSuccessful, int middleOfChunkX,
			int middleOfChunkY, int x, int y) {
		if (isSuccessful) {
			if (isValid(middleOfChunkX, middleOfChunkY)) {
				return createMapObject(x << 4, y << 4);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean isValid(int middleOfChunkX, int middleOfChunkY) {
		return MinecraftUtil.isValidBiome(middleOfChunkX, middleOfChunkY,
				STRUCTURE_SIZE, validBiomes);
	}

	private MapObjectVillage createMapObject(int x, int y) {
		return new MapObjectVillage(x, y);
	}

	@Override
	protected List<Biome> getValidBiomes() {
		// @formatter:off
		return Arrays.asList(
				Biome.plains,
				Biome.desert,
				Biome.savanna
		);
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
		return 10387312L;
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
