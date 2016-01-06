package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class NetherFortressProducer extends StructureProducer {
	private final LocationChecker checker;

	public NetherFortressProducer(long seed, BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle);
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
	protected boolean displayNetherCoordinates() {
		return true;
	}
}
