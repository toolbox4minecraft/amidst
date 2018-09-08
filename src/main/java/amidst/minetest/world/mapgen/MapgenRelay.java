package amidst.minetest.world.mapgen;

import java.util.ArrayList;

import amidst.fragment.IBiomeDataOracle;
import amidst.gui.main.WorldSwitchedListener;
import amidst.gui.main.WorldSwitcher;
import amidst.gui.main.viewer.ViewerFacade;

/**
 * Adapts the WorldSwitcher to provides notifications for when the mapgen has changed
 */
public class MapgenRelay implements WorldSwitchedListener {

	private IBiomeDataOracle biomeDataOracle;
	private final ArrayList<MapgenUpdatedListener> listeners = new ArrayList<MapgenUpdatedListener>();
		
	@Override
	public void onWorldSwitched(ViewerFacade viewerFacade) {
	
		biomeDataOracle = null;
		if (viewerFacade != null) biomeDataOracle = viewerFacade.getBiomeDataOracle();
		
		for (MapgenUpdatedListener listener: listeners) {
			listener.onMapgenUpdated(biomeDataOracle);
		}		
	}
	
	public void addMapgenUpdatedListener(MapgenUpdatedListener listener) {
		listeners.add(listener);
	}
	public void removeMapgenUpdatedListener(MapgenUpdatedListener listener) {
		listeners.remove(listener);
	}	
	
	public IBiomeDataOracle getBiomeDataOracle() { return biomeDataOracle; }
	
	public MapgenRelay(WorldSwitcher worldSwitcher) {
		
		ViewerFacade currentViewerFacade = worldSwitcher.addWorldSwitchedListener(this);
		
		// Force a switched event to bring the instance's state up to date.
		onWorldSwitched(currentViewerFacade);
	}
}
