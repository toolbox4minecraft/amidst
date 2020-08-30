package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.SuppressAroundLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.util.FastRand;

@ThreadSafe
public class PillagerOutpostProducer extends RegionalStructureProducer<Void> {
	private static final Resolution RESOLUTION = Resolution.CHUNK;
	private static final int OFFSET_IN_WORLD = 4;
	private static final Dimension DIMENSION = Dimension.OVERWORLD;
	private static final boolean DISPLAY_DIMENSION = false;

	private static final long SALT = 165745296L;
	private static final byte SPACING = 32;
	private static final byte SEPARATION = 8;
	private static final boolean IS_TRIANGULAR = false;

	private static final int STRUCTURE_SIZE = 0;


	public PillagerOutpostProducer(
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesForStructure,
			long worldSeed,
			RegionalStructureProducer<Void> villageProducer,
			int avoidVillageRadius,
			boolean checkVillageLocations,
			boolean buggyStructureCoordinateMath) {

		super(RESOLUTION,
			  OFFSET_IN_WORLD,
			  new AllValidLocationChecker(
					  new PillagerOutpostAlgorithm(worldSeed),
					  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure),
					  new SuppressAroundLocationChecker(villageProducer, avoidVillageRadius, checkVillageLocations)
			  ),
			  new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.PILLAGER_OUTPOST),
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

	private static class PillagerOutpostAlgorithm implements LocationChecker {
		private final long seed;

		public PillagerOutpostAlgorithm(long seed) {
			this.seed = seed;
		}

		@Override
		public boolean isValidLocation(int x, int y) {
			FastRand random = new FastRand((x >> 4) ^ ((y >> 4) << 4) ^ seed);
			random.advance();
			return random.nextInt(5) == 0;
		}
	}
}
