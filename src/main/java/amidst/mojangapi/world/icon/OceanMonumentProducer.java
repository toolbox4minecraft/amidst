package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class OceanMonumentProducer extends StructureProducer {
	private final LocationChecker checker;

	public OceanMonumentProducer(long seed, BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle);
		this.checker = new OceanMonumentLocationChecker(seed, biomeDataOracle);
	}

	@Override
	protected boolean isValidLocation() {
		return checker.isValidLocation(chunkX, chunkY);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		return DefaultWorldIconTypes.OCEAN_MONUMENT;
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return false;
	}
}
