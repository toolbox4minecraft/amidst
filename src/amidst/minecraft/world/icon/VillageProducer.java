package amidst.minecraft.world.icon;

import java.util.Arrays;
import java.util.List;

import amidst.minecraft.Biome;
import amidst.minecraft.world.World;

public class VillageProducer extends StructureProducer {
	public VillageProducer(World world) {
		super(world);
	}

	@Override
	protected boolean isValidLocation() {
		return isSuccessful() && isValidBiomeForStructure();
	}

	@Override
	protected DefaultWorldIconTypes getMapMarker() {
		return DefaultWorldIconTypes.VILLAGE;
	}

	@Override
	protected List<Biome> getValidBiomesForStructure() {
		// @formatter:off
		return Arrays.asList(
				Biome.plains,
				Biome.desert,
				Biome.savanna
		);
		// @formatter:on
	}

	@Override
	protected List<Biome> getValidBiomesAtMiddleOfChunk() {
		return null; // not used
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
	protected int getStructureSize() {
		return 0;
	}
}
