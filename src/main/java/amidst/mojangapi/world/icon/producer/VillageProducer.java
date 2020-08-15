package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.VillageAlgorithm;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class VillageProducer extends RegionalStructureProducer<Void> {
	private static final long SALT = 10387312L;
	private static final byte SPACING = 32;
	private static final byte SEPARATION = 8;
	private static final boolean IS_TRIANGULAR = false;
	private static final int STRUCTURE_SIZE = 0;

	public VillageProducer(
			Resolution resolution,
			int offsetInWorld,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesForStructure,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			long seed,
			boolean doComplexVillageCheck) {
		
		super(resolution,
			  offsetInWorld,
			  getLocationCheckers(seed, biomeDataOracle, validBiomesForStructure, doComplexVillageCheck),
			  provider,
			  dimension,
			  displayDimension,
			  seed,
			  SALT,
			  SPACING,
			  SEPARATION,
			  IS_TRIANGULAR
			 );
	}

	private static LocationChecker getLocationCheckers(
			long seed, BiomeDataOracle biomeDataOracle, List<Biome> validBiomesForStructure, boolean doComplexVillageCheck) {
		LocationChecker biome = new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure);

		if(doComplexVillageCheck) {
			return new AllValidLocationChecker(biome, new VillageAlgorithm(biomeDataOracle, validBiomesForStructure));
		} else {
			return biome;
		}
	}
}
