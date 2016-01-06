package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class OceanMonumentProducer extends StructureProducer {
	private final LocationChecker checker;
	private final WorldIconTypeProvider provider;

	public OceanMonumentProducer(long seed, BiomeDataOracle biomeDataOracle) {
		this.checker = new OceanMonumentLocationChecker(seed, biomeDataOracle);
		this.provider = new ImmutableWorldIconTypeProvider(
				DefaultWorldIconTypes.OCEAN_MONUMENT);
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
