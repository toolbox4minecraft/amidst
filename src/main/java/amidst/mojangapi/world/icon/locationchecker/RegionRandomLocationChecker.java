package amidst.mojangapi.world.icon.locationchecker;

import java.util.function.Function;

import amidst.mojangapi.world.icon.producer.RegionalStructureProducer;
import amidst.util.FastRand;

public class RegionRandomLocationChecker implements LocationChecker {

	private RegionalStructureProducer<?> regionalProducer;
	private final Function<FastRand, Boolean> randomFunction;

	public RegionRandomLocationChecker(Function<FastRand, Boolean> randomFunction) {
		this.randomFunction = randomFunction;
	}

	public void setRegionalProducer(RegionalStructureProducer<?> regionalProducer) {
		this.regionalProducer = regionalProducer;
	}

	@Override
	public boolean isValidLocation(int chunkX, int chunkY) {
		FastRand random = new FastRand(regionalProducer.getRegionSeed(
									regionalProducer.getRegionCoord(chunkX),
									regionalProducer.getRegionCoord(chunkY)
								));

		random.advance();
		random.advance();
		if (regionalProducer.isTriangular) {
			random.advance();
			random.advance();
		}

		return randomFunction.apply(random);
	}

}
