package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class NetherFortressProducer extends StructureProducer {
	private final LocationChecker checker;
	private final WorldIconTypeProvider provider;

	public NetherFortressProducer(long seed) {
		this.checker = new NetherFortressAlgorithm(seed);
		this.provider = new ImmutableWorldIconTypeProvider(
				DefaultWorldIconTypes.NETHER_FORTRESS);
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
		return true;
	}
}
