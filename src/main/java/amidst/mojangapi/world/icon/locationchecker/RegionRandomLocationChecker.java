package amidst.mojangapi.world.icon.locationchecker;

import java.util.function.Function;
import java.util.function.Supplier;

import amidst.mojangapi.world.icon.producer.RegionalStructureProducer;
import kaptainwutax.seedutils.lcg.LCG;
import kaptainwutax.seedutils.lcg.rand.JRand;

public class RandomLocationChecker implements LocationChecker {
	// These have never changed and probably never will change, so we hard code them instead of giving them a version feature
	private static final long MAGIC_NUMBER_1 = 341873128712L;
	private static final long MAGIC_NUMBER_2 = 132897987541L;
	
	private final boolean isTriangular;
	private final boolean buggyStructureCoordinateMath;
	private final Function<JRand, Boolean> randomFunction;
	
	public RandomLocationChecker(boolean isTriangular, boolean buggyStructureCoordinateMath, Function<JRand, Boolean> randomFunction) {
		this.randomFunction = randomFunction;
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
	
	public int getRegionCoord(int coordinate) {
		return getModifiedCoord(coordinate) / spacing;
	}

	private int getModifiedCoord(int coordinate) {
		if (coordinate < 0) {
			if (buggyStructureCoordinateMath) {
				// Bug MC-131462.
				return coordinate - spacing - 1;
			} else {
				return coordinate - spacing + 1;
			}
		} else {
			return coordinate;
		}
	}

	private long getRegionSeed(int value1, int value2) {
		// @formatter:off
		return value1 * MAGIC_NUMBER_1
		     + value2 * MAGIC_NUMBER_2
		              + worldSeed
		              + salt;
		// @formatter:on
	}
	
}
