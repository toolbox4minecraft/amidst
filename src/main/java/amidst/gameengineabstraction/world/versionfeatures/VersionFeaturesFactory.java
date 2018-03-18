package amidst.gameengineabstraction.world.versionfeatures;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

public interface VersionFeaturesFactory {

	/**
	 * Use this method when you don't know the worldtype yet
	 */
	IVersionFeatures create(RecognisedVersion version);
	
	/**
	 * Use this method if you know the worldtype, as features change in Minetest
	 * depending on the worldType
	 */
	IVersionFeatures create(WorldType worldtype, RecognisedVersion version);		
}
