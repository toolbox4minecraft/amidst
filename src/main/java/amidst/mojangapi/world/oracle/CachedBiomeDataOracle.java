package amidst.mojangapi.world.oracle;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.BiomeData;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public class CachedBiomeDataOracle extends BiomeDataOracle {
	private BiomeDataOracle oracle;
	private Region.Box cacheRegion;
	private Region.Box cacheRegionQuarter;
	private boolean isCacheValid;
	private boolean isCacheQuarterValid;
	private BiomeData cacheQuarter;
	private BiomeData cache;

	public CachedBiomeDataOracle(BiomeDataOracle oracle, Region.Box region) {
		this.oracle = oracle;
		this.cacheRegion = region;
		this.cacheRegionQuarter = region.getAs(Resolution.QUARTER);
		this.isCacheValid = false;
		this.isCacheQuarterValid = false;
		this.cache = null;
		this.cacheQuarter = null;
	}
	
	@Override
	public synchronized short getBiomeAt(int x, int y) throws MinecraftInterfaceException {
		if(isCacheValid)
			return doGetBiomeData(Region.box(x, y, 1, 1), false).get(0, 0);
		return oracle.getBiomeAt(x, y);
	}

	@Override
	protected synchronized BiomeData doGetBiomeData(Region.Box region, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		Region.Box cacheReg = useQuarterResolution ? cacheRegionQuarter : cacheRegion;
		if(!cacheReg.contains(region))
			return oracle.doGetBiomeData(region, useQuarterResolution);

		Coordinates offset = region.getCorner().substract(cacheReg.getCorner());
		return getCacheData(useQuarterResolution).view(offset.getX(), offset.getY(), region.getWidth(), region.getHeight());
	}

	public synchronized BiomeData getCacheData(boolean useQuarterResolution) 
			throws MinecraftInterfaceException {
		Resolution r = Resolution.from(useQuarterResolution);

		if(useQuarterResolution) {
			if(isCacheQuarterValid)
				return cacheQuarter;

			if(cacheQuarter == null)
				cacheQuarter = createDataArray(r);
			

			cacheQuarter.copyFrom(oracle.doGetBiomeData(cacheRegionQuarter, true));
			return cacheQuarter;

		} else {
			if(isCacheValid)
				return cache;

			if(cache == null)
				cache = createDataArray(r);

			cache.copyFrom(oracle.doGetBiomeData(cacheRegion, true));
			return cache;
		}
	}

	private BiomeData createDataArray(Resolution resolution) {
		Region r = cacheRegion.getAs(resolution);
		return new BiomeData(null, r.getWidth(), r.getHeight());
	}


	public synchronized Region.Box getCacheRegion() {
		return cacheRegion;
	}

	public synchronized void moveCacheTo(Region.Box r) {
		if(cacheRegion.equals(r))
			return;

		Region old = cacheRegion;
		cacheRegion = r;
		cacheRegionQuarter = r.getAs(Resolution.QUARTER);

		if(old.getWidth() != r.getWidth() || old.getHeight() != r.getHeight()) {
			cache = null;
			cacheQuarter = null;
			isCacheValid = false;
		} else {
			isCacheValid = old.getCorner().equals(r.getCorner());
		}

		isCacheQuarterValid = isCacheValid;
	}
}

