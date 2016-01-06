package amidst.mojangapi.world.icon;

import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class NetherFortressProducer extends StructureProducer {
	private final LocationChecker checker;

	public NetherFortressProducer(RecognisedVersion recognisedVersion,
			long seed, BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle, recognisedVersion);
		this.checker = new NetherFortressAlgorithm(seed);
	}

	@Override
	protected boolean isValidLocation() {
		return checker.isValidLocation(chunkX, chunkY);
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
