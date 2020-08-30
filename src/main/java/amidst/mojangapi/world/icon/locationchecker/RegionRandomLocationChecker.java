package amidst.mojangapi.world.icon.locationchecker;

import java.util.function.Function;

import amidst.mojangapi.world.icon.producer.RegionalStructureProducer;
import kaptainwutax.seedutils.lcg.LCG;
import kaptainwutax.seedutils.lcg.rand.JRand;

public class RegionRandomLocationChecker implements LocationChecker {

	private RegionalStructureProducer<?> regionalProducer;
	private final Function<JRand, Boolean> randomFunction;
	
	public RegionRandomLocationChecker(Function<JRand, Boolean> randomFunction) {
		this.randomFunction = randomFunction;
	}
	
	public void setRegionalProducer(RegionalStructureProducer<?> regionalProducer) {
		this.regionalProducer = regionalProducer;
	}
	
	private static final LCG ADVANCE_4 = LCG.JAVA.combine(4);
	private static final LCG ADVANCE_2 = LCG.JAVA.combine(2);
	
	@Override
	public boolean isValidLocation(int chunkX, int chunkY) {
		JRand random = new JRand(regionalProducer.getRegionSeed(
									regionalProducer.getRegionCoord(chunkX),
									regionalProducer.getRegionCoord(chunkY)
								));
		
		if (regionalProducer.isTriangular) {
			random.advance(ADVANCE_4);
		} else {
			random.advance(ADVANCE_2);
		}
		
		return randomFunction.apply(random);
	}
	
}
