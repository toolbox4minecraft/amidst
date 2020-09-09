package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.producer.RegionalStructureProducer;

@ThreadSafe
public class SuppressAroundLocationChecker implements LocationChecker {
	
	private final RegionalStructureProducer<?> regionalProducer;
	private final int distance;
	private final boolean checkLocations;
	
	public SuppressAroundLocationChecker(RegionalStructureProducer<?> regionalProducer, int distance, boolean checkLocations) {
		this.regionalProducer = regionalProducer;
		this.distance = distance;
		this.checkLocations = checkLocations;
	}
	
	/**
	 * Adaptation of <a
	 * href=https://github.com/KaptainWutax/FeatureUtils/blob/master/src/main/java/kaptainwutax/featureutils/structure/PillagerOutpost.java#L96>this</a>
	 */
	@Override
	public boolean isValidLocation(int chunkX, int chunkZ) {
		if (distance < 0) {
			return true;
		}
		
		CoordinatesInWorld thisStructPos = new CoordinatesInWorld(chunkX, chunkZ);
		
		int nwChunkX = chunkX - distance;
		int nwChunkZ = chunkZ - distance;
		int seChunkX = chunkX + distance;
		int seChunkZ = chunkZ + distance;
		
		CoordinatesInWorld otherStructPos = getLocation(nwChunkX, nwChunkZ);
		
		if (otherStructPos != null && otherStructPos.getDistanceChebyshev(thisStructPos) <= distance) {
			return false;
		}
		
		int nwRegionX = regionalProducer.getRegionCoord(nwChunkX);
		int nwRegionZ = regionalProducer.getRegionCoord(nwChunkZ);
		int seRegionX = regionalProducer.getRegionCoord(seChunkX);
		int seRegionZ = regionalProducer.getRegionCoord(seChunkZ);
		
		//The area is contained within one region.
		if (nwRegionX == seRegionX && nwRegionZ == seRegionZ) {
			return true;
		}
		
		//The area intersects 4 regions.
		if (nwRegionX != seRegionX && nwRegionZ != seRegionZ) {
			
			otherStructPos = getLocation(seChunkX, seChunkZ);
			if (otherStructPos != null && otherStructPos.getDistanceChebyshev(thisStructPos) <= distance) {
				return false;
			}
			
			otherStructPos = getLocation(nwChunkX, seChunkZ);
			if (otherStructPos != null && otherStructPos.getDistanceChebyshev(thisStructPos) <= distance) {
				return false;
			}
			
			otherStructPos = getLocation(seChunkX, nwChunkZ);
			return !(otherStructPos != null && otherStructPos.getDistanceChebyshev(thisStructPos) <= distance);
		}
		
		//The area intersects 2 regions.
		otherStructPos = getLocation(seChunkX, seChunkZ);
		return !(otherStructPos != null && otherStructPos.getDistanceChebyshev(thisStructPos) <= distance);
	}
	
	private CoordinatesInWorld getLocation(int chunkX, int chunkZ) {
		if (checkLocations) {
			return regionalProducer.getCheckedLocation(chunkX, chunkZ);
		} else {
			return regionalProducer.getPossibleLocation(chunkX, chunkZ);
		}
	}
	
}
