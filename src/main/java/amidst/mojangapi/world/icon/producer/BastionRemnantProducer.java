package amidst.mojangapi.world.icon.producer;

import java.util.List;
import java.util.function.Function;

import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.BiomeLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.RegionRandomLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.util.FastRand;
 
public class BastionRemnantProducer extends RegionalStructureProducer<Void> {
	private static final Resolution RESOLUTION = Resolution.NETHER_CHUNK;
	private static final int OFFSET_IN_WORLD = 88;
	private static final Dimension DIMENSION = Dimension.NETHER;
	private static final boolean DISPLAY_DIMENSION = false;
	
	private static final boolean IS_TRIANGULAR = false;
	
	public static BastionRemnantProducer create(
			long worldSeed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			long salt,
			byte spacing,
			byte separation,
			boolean buggyStructureCoordinateMath,
			Function<FastRand, Boolean> randomFunction) {
		
		RegionRandomLocationChecker regionRandomChecker = new RegionRandomLocationChecker(randomFunction);
		BastionRemnantProducer producer = new BastionRemnantProducer(
				worldSeed,
				biomeDataOracle,
				validBiomesAtMiddleOfChunk,
				salt,
				spacing,
				separation,
				buggyStructureCoordinateMath,
				regionRandomChecker
		);
		regionRandomChecker.setRegionalProducer(producer);
		return producer;
	}
	
	private BastionRemnantProducer(
			long worldSeed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			long salt,
			byte spacing,
			byte separation,
			boolean buggyStructureCoordinateMath,
			RegionRandomLocationChecker regionRandomChecker) {
		
		super(RESOLUTION,
			  OFFSET_IN_WORLD,
			  new AllValidLocationChecker(
					  new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk),
					  regionRandomChecker
			  ),
			  new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.BASTION_REMNANT),
			  DIMENSION,
			  DISPLAY_DIMENSION,
			  worldSeed,
			  salt,
			  spacing,
			  separation,
			  IS_TRIANGULAR,
			  buggyStructureCoordinateMath
			);
	}
	
}
