package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class VillageProducer extends StructureProducer {
	private final LocationChecker checker;

	public VillageProducer(long seed, BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle);
		this.checker = new VillageLocationChecker(seed, biomeDataOracle);
	}

	@Override
	protected boolean isValidLocation() {
		return checker.isValidLocation(chunkX, chunkY);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		return DefaultWorldIconTypes.VILLAGE;
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return false;
	}
}
