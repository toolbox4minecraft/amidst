package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class VillageProducer extends StructureProducer {
	private final LocationChecker checker;
	private final WorldIconTypeProvider provider;

	public VillageProducer(long seed, BiomeDataOracle biomeDataOracle) {
		this.checker = new VillageLocationChecker(seed, biomeDataOracle);
		this.provider = new ImmutableWorldIconTypeProvider(
				DefaultWorldIconTypes.VILLAGE);
	}

	@Override
	protected boolean isValidLocation(int x, int y) {
		return checker.isValidLocation(x, y);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType(int x, int y) {
		return provider.get(x, y);
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return false;
	}
}
