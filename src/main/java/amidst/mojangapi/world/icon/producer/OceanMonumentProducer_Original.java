package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.BiomeLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class OceanMonumentProducer_Original extends RegionalStructureProducer<Void> {
	private static final long SALT = 10387313L;
	private static final byte SPACING = 32;
	private static final byte SEPARATION = 5;
	private static final boolean IS_TRIANGULAR = true;
	private static final int STRUCTURE_SIZE = 29;

	public OceanMonumentProducer_Original(
			Resolution resolution,
			int offsetInWorld,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			long worldSeed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			List<Biome> validBiomesForStructure) {
		
		super(
			  resolution,
			  offsetInWorld,
			  new AllValidLocationChecker(
					  new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk),
					  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure)
			  ),
			  provider,
			  dimension,
			  displayDimension,
			  worldSeed,
			  SALT,
			  SPACING,
			  SEPARATION,
			  IS_TRIANGULAR
			);
	}
}
