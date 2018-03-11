package amidst.gameengineabstraction.world.versionfeatures;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public interface VersionFeaturesFactory {

	IVersionFeatures create(RecognisedVersion version);	
}
