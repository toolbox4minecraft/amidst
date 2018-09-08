package amidst.minetest.world.mapgen;

import amidst.fragment.IBiomeDataOracle;

public interface MapgenUpdatedListener {
	void onMapgenUpdated(IBiomeDataOracle biomeDataOracle);
}
