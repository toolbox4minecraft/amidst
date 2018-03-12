package amidst.gameengineabstraction;

import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.gameengineabstraction.world.versionfeatures.VersionFeaturesFactory;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.settings.biomeprofile.BiomeProfile;

public class GameEngineDetails {

	private final GameEngineType type;
	private final VersionFeaturesFactory versionFeaturesFactory;
	private final Class<? extends BiomeProfile> biomeProfileImpl;
	
	public GameEngineDetails(GameEngineType type, VersionFeaturesFactory version_features_factory, Class<? extends BiomeProfile> biome_profile_impl) {
		this.type = type;
		this.versionFeaturesFactory = version_features_factory;
		this.biomeProfileImpl = biome_profile_impl;
	}

	public GameEngineType getType() {
		return type;
	}

	public IVersionFeatures getVersionFeatures(RecognisedVersion version) {
		return versionFeaturesFactory.create(version);
	}
	
	public Class<? extends BiomeProfile> getBiomeProfileImplementation() {
		return biomeProfileImpl;
	}
}
