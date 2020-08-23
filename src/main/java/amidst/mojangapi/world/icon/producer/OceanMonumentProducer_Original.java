package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.BiomeLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class OceanMonumentProducer_Original extends RegionalStructureProducer<Void> {
	private static final Resolution RESOLUTION = Resolution.CHUNK;
	private static final int OFFSET_IN_WORLD = 8;
	private static final Dimension DIMENSION = Dimension.OVERWORLD;
	private static final boolean DISPLAY_DIMENSION = false;
	
	private static final long SALT = 10387313L;
	private static final byte SPACING = 32;
	private static final byte SEPARATION = 5;
	private static final boolean IS_TRIANGULAR = true;
	
	private static final int STRUCTURE_SIZE = 29;

	public OceanMonumentProducer_Original(
			long worldSeed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			List<Biome> validBiomesForStructure,
			boolean buggyStructureCoordinateMath) {
		
		super(RESOLUTION,
			  OFFSET_IN_WORLD,
			  new AllValidLocationChecker(
					  new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk),
					  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure)
			  ),
			  new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
			  DIMENSION,
			  DISPLAY_DIMENSION,
			  worldSeed,
			  SALT,
			  SPACING,
			  SEPARATION,
			  IS_TRIANGULAR,
			  buggyStructureCoordinateMath
			);
	}
}
