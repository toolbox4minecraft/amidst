package amidst.gameengineabstraction.world.versionfeatures;

import amidst.gameengineabstraction.world.WorldTypes;

public interface IVersionFeatures {

	boolean hasLayer(int layerId);
	
	WorldTypes getWorldTypes();
}
