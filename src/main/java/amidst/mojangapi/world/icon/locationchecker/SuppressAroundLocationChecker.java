package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.producer.RegionalStructureProducer;

@ThreadSafe
public class SuppressAroundLocationChecker<T> implements LocationChecker {
	
	private final RegionalStructureProducer<T> regionalProducer;
	private final int distance;
	
	public SuppressAroundLocationChecker(RegionalStructureProducer<T> regionalProducer, int distance) {
		this.regionalProducer = regionalProducer;
		this.distance = distance;
	}
	
	/**
	 * Adaptation of <a
	 * href=https://github.com/KaptainWutax/FeatureUtils/blob/a751c57c7efdd3cc97114efb06de10033f50cfb0/src/main/java/kaptainwutax/featureutils/structure/PillagerOutpost.java#L90>this</a>
	 */
	@Override
	public boolean isValidLocation(int chunkX, int chunkZ) {
		if (distance < 0) {
			return true;
		}
		
		CoordinatesInWorld outpostPos = new CoordinatesInWorld(chunkX, chunkZ);
		
		int nwChunkX = chunkX - distance;
		int nwChunkZ = chunkZ - distance;
		int seChunkX = chunkX + distance;
		int seChunkZ = chunkZ + distance;
		
		CoordinatesInWorld structPos = this.regionalProducer.getCheckedLocation(nwChunkX, nwChunkZ);
		
		if (structPos != null && structPos.getDistanceChebyshev(outpostPos) <= distance) {
			return true;
		}
		
		int nwRegionX = regionalProducer.getRegionCoord(nwChunkX);
		int nwRegionZ = regionalProducer.getRegionCoord(nwChunkZ);
		int seRegionX = regionalProducer.getRegionCoord(seChunkX);
		int seRegionZ = regionalProducer.getRegionCoord(seChunkZ);
		
		//The area is contained within one region.
		if (nwRegionX == seRegionX && nwRegionZ == seRegionZ) {
			return false;
		}
		
		//The area intersects 4 regions.
		if (nwRegionX != seRegionX && nwRegionZ != seRegionZ) {
			structPos = this.regionalProducer.getCheckedLocation(seChunkX, seChunkZ);
			
			if (structPos != null && structPos.getDistanceChebyshev(outpostPos) <= distance) { // TODO: why was this null check not here?
				return true;
			}
			
			structPos = this.regionalProducer.getCheckedLocation(nwChunkX, seChunkZ);
			
			if (structPos != null && structPos.getDistanceChebyshev(outpostPos) <= distance) {
				return true;
			}
			
			structPos = this.regionalProducer.getCheckedLocation(seChunkX, nwChunkZ);
			return structPos != null && structPos.getDistanceChebyshev(outpostPos) <= distance;
		}
		
		//The area intersects 2 regions.
		structPos = this.regionalProducer.getCheckedLocation(seChunkX, seChunkZ);
		return structPos != null && structPos.getDistanceChebyshev(outpostPos) <= distance;
	}
	
}
