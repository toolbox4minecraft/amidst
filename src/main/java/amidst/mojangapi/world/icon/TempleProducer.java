package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class TempleProducer extends StructureProducer {
	private final LocationChecker checker;
	private final WorldIconTypeProvider provider;

	public TempleProducer(RecognisedVersion recognisedVersion, long seed,
			BiomeDataOracle biomeDataOracle) {
		this.checker = new TempleLocationChecker(seed, biomeDataOracle,
				recognisedVersion);
		this.provider = new TempleWorldIconTypeProvider(biomeDataOracle);
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
