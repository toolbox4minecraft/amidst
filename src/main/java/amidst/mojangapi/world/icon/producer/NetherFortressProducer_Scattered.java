package amidst.mojangapi.world.icon.producer;

import java.util.function.Function;

import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.RegionRandomLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.util.FastRand;

public class NetherFortressProducer_Scattered extends RegionalStructureProducer<Void> {
	private static final Resolution RESOLUTION = Resolution.NETHER_CHUNK;
	private static final int OFFSET_IN_WORLD = 88;
	private static final Dimension DIMENSION = Dimension.NETHER;
	private static final boolean DISPLAY_DIMENSION = false;
	
	private static final boolean IS_TRIANGULAR = false;
	
	public static NetherFortressProducer_Scattered create(
			long worldSeed,
			long salt,
			byte spacing,
			byte separation,
			boolean buggyStructureCoordinateMath,
			Function<FastRand, Boolean> randomFunction) {
		
		RegionRandomLocationChecker regionRandomChecker = new RegionRandomLocationChecker(randomFunction);
		NetherFortressProducer_Scattered producer = new NetherFortressProducer_Scattered(
				worldSeed,
				salt,
				spacing,
				separation,
				buggyStructureCoordinateMath,
				regionRandomChecker
		);
		regionRandomChecker.setRegionalProducer(producer);
		return producer;
	}
	
	private NetherFortressProducer_Scattered(
			long worldSeed,
			long salt,
			byte spacing,
			byte separation,
			boolean buggyStructureCoordinateMath,
			RegionRandomLocationChecker regionRandomChecker) {
		
		super(RESOLUTION,
			  OFFSET_IN_WORLD,
			  regionRandomChecker,
			  new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.NETHER_FORTRESS),
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
