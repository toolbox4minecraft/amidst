package amidst.map.finder;

import java.util.Arrays;
import java.util.List;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class VillageFinder extends StructureFinder {
	private static final int STRUCTURE_SIZE = 0;

	@Override
	protected MapObject getMapObject() {
		if (isSuccessful) {
			if (isValid()) {
				return createMapObject(
						xRelativeToFragmentAsChunkResolution << 4,
						yRelativeToFragmentAsChunkResolution << 4);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean isValid() {
		return MinecraftUtil.isValidBiome(middleOfChunkX, middleOfChunkY,
				STRUCTURE_SIZE, validBiomes);
	}

	private MapObject createMapObject(int x, int y) {
		return MapObject.fromFragmentCoordinatesAndFragment(
				isVisiblePreference, MapMarkers.VILLAGE, x, y, fragment);
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
