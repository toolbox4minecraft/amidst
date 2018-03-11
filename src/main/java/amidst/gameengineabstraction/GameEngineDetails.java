package amidst.gameengineabstraction;

import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.gameengineabstraction.world.versionfeatures.VersionFeaturesFactory;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class GameEngineDetails {

	private final GameEngineType type;
	private final VersionFeaturesFactory versionFeaturesFactory;
	
	public GameEngineDetails(GameEngineType type, VersionFeaturesFactory version_features_factory) {
		this.type = type;
		this.versionFeaturesFactory = version_features_factory;
	}

	public GameEngineType getType() {
		return type;
	}

	public IVersionFeatures getVersionFeatures(RecognisedVersion version) {
		return versionFeaturesFactory.create(version);
	}	
}
