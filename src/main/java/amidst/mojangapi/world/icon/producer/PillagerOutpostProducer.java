package amidst.mojangapi.world.icon.producer;

import java.util.List;
import java.util.Random;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.SuppressAroundLocationChecker;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class PillagerOutpostProducer extends RegionalStructureProducer<Void> {
	private static final long SALT = 165745296L;
	private static final byte SPACING = 32;
	private static final byte SEPARATION = 8;
	private static final boolean IS_TRIANGULAR = false;
	private static final int STRUCTURE_SIZE = 0;


	public PillagerOutpostProducer(
			Resolution resolution,
			int offsetInWorld,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesForStructure,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			long seed,
			RegionalStructureProducer<Void> villageProducer,
			int avoidVillageRadius,
			boolean checkVillageLocations) {
		
		super(resolution,
			  offsetInWorld,
			  new AllValidLocationChecker(
					  new PillagerOutpostAlgorithm(seed),
					  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure),
					  new SuppressAroundLocationChecker<>(villageProducer, avoidVillageRadius, checkVillageLocations)
			  ),
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

	private static class PillagerOutpostAlgorithm implements LocationChecker {
		private final long seed;

		public PillagerOutpostAlgorithm(long seed) {
			this.seed = seed;
		}

		@Override
		public boolean isValidLocation(int x, int y) {
			Random random = new Random((x >> 4) ^ ((y >> 4) << 4) ^ seed);
			random.nextInt();
			return random.nextInt(5) == 0;
		}
	}
}
