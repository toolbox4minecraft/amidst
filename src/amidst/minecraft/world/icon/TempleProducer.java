package amidst.minecraft.world.icon;

import java.util.Arrays;
import java.util.List;

import amidst.minecraft.Biome;
import amidst.minecraft.VersionInfo;
import amidst.minecraft.world.World;

public class TempleProducer extends StructureProducer {
	public TempleProducer(World world) {
		super(world);
	}

	@Override
	protected boolean isValidLocation() {
		return isSuccessful() && isValidBiomeAtMiddleOfChunk();
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		Biome chunkBiome = getBiomeAtMiddleOfChunk();
		if (chunkBiome == Biome.swampland) {
			return DefaultWorldIconTypes.WITCH;
		} else if (chunkBiome.getName().contains("Jungle")) {
			return DefaultWorldIconTypes.JUNGLE;
		} else if (chunkBiome.getName().contains("Desert")) {
			return DefaultWorldIconTypes.DESERT;
		} else {
			return null;
		}
	}

	@Override
	protected List<Biome> getValidBiomesForStructure() {
		return null; // not used
	}

	@Override
	protected List<Biome> getValidBiomesAtMiddleOfChunk() {
		// @formatter:off
		VersionInfo versionInfo = world.getMinecraftInterface().getVersion();
		if (versionInfo.isAtLeast(VersionInfo.V1_4_2)) {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills,
					Biome.jungle,
					Biome.jungleHills,
					Biome.swampland
			);
		} else if (versionInfo.isAtLeast(VersionInfo.V12w22a)) {
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
	protected int getStructureSize() {
		return -1; // not used
	}
}
