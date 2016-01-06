package amidst.mojangapi.world.icon;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class TempleProducer extends StructureProducer {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 14357617L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 8;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = false;

	private final StructureAlgorithm algorithm;

	public TempleProducer(RecognisedVersion recognisedVersion, long seed,
			BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle, recognisedVersion);
		this.algorithm = new StructureAlgorithm(seed, MAGIC_NUMBER_FOR_SEED_1,
				MAGIC_NUMBER_FOR_SEED_2, MAGIC_NUMBER_FOR_SEED_3,
				MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
				MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
				USE_TWO_VALUES_FOR_UPDATE);
	}

	@Override
	protected boolean isValidLocation() {
		return algorithm.isValidLocation(chunkX, chunkY)
				&& biomeDataOracle.isValidBiomeAtMiddleOfChunk(chunkX, chunkY,
						validBiomesAtMiddleOfChunk);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		try {
			Biome chunkBiome = biomeDataOracle.getBiomeAtMiddleOfChunk(chunkX,
					chunkY);
			if (chunkBiome == Biome.swampland) {
				return DefaultWorldIconTypes.WITCH;
			} else if (chunkBiome.getName().contains("Jungle")) {
				return DefaultWorldIconTypes.JUNGLE;
			} else if (chunkBiome.getName().contains("Desert")) {
				return DefaultWorldIconTypes.DESERT;
			} else {
				Log.e("No known structure for this biome type: "
						+ chunkBiome.getName());
				return null;
			}
		} catch (UnknownBiomeIndexException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (MinecraftInterfaceException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
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
		if (recognisedVersion.isAtLeast(RecognisedVersion.V1_4_2)) {
			return Arrays.asList(
					Biome.desert,
					Biome.desertHills,
					Biome.jungle,
					Biome.jungleHills,
					Biome.swampland
			);
		} else if (recognisedVersion.isAtLeast(RecognisedVersion.V12w22a)) {
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
	protected int getStructureSize() {
		return -1; // not used
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return false;
	}
}
