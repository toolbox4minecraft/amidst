package amidst.mojangapi.mocking;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;
import amidst.mojangapi.world.testworld.storage.json.WorldMetadataJson;

@ThreadSafe
public class FakeMinecraftInterface implements MinecraftInterface {
	private final WorldMetadataJson worldMetadataJson;
	private final BiomeDataJson quarterBiomeData;
	private final BiomeDataJson fullBiomeData;
	private volatile boolean isWorldCreated = false;

	public FakeMinecraftInterface(
			WorldMetadataJson worldMetadataJson,
			BiomeDataJson quarterBiomeData,
			BiomeDataJson fullBiomeData) {
		this.worldMetadataJson = worldMetadataJson;
		this.quarterBiomeData = quarterBiomeData;
		this.fullBiomeData = fullBiomeData;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		if (isWorldCreated) {
			return getBiomeData(useQuarterResolution).get(x, y, width, height);
		} else {
			throw new MinecraftInterfaceException("the world needs to be created first");
		}
	}

	private BiomeDataJson getBiomeData(boolean useQuarterResolution) {
		if (useQuarterResolution) {
			return quarterBiomeData;
		} else {
			return fullBiomeData;
		}
	}

	@Override
	public void createWorld(long seed, WorldType worldType, String generatorOptions) throws MinecraftInterfaceException {
		if (worldMetadataJson.getSeed() == seed && worldMetadataJson.getWorldType().equals(worldType)
				&& generatorOptions.isEmpty()) {
			isWorldCreated = true;
		} else {
			isWorldCreated = false;
			throw new MinecraftInterfaceException("the world has to match");
		}
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return worldMetadataJson.getRecognisedVersion();
	}
}
