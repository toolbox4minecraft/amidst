package amidst.mojangapi.world.icon;

import java.util.List;
import java.util.Random;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class NetherFortressProducer extends StructureProducer {
	private final Random random = new Random();
	private final long seed;

	public NetherFortressProducer(RecognisedVersion recognisedVersion,
			long seed, BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle, recognisedVersion);
		this.seed = seed;
	}

	@Override
	protected boolean isValidLocation() {
		int i = chunkX >> 4;
		int j = chunkY >> 4;
		random.setSeed(i ^ j << 4 ^ seed);
		random.nextInt();
		if (random.nextInt(3) != 0) {
			return false;
		}
		if (chunkX != (i << 4) + 4 + random.nextInt(8)) {
			return false;
		}
		return chunkY == (j << 4) + 4 + random.nextInt(8);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		return DefaultWorldIconTypes.NETHER_FORTRESS;
	}

	@Override
	protected List<Biome> getValidBiomesForStructure() {
		return null; // not used
	}

	@Override
	protected List<Biome> getValidBiomesAtMiddleOfChunk() {
		return null; // not used
	}

	@Override
	protected int getStructureSize() {
		return -1; // not used
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return true;
	}
}
