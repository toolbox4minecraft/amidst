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

	public FakeMinecraftInterface(
			WorldMetadataJson worldMetadataJson,
			BiomeDataJson quarterBiomeData,
			BiomeDataJson fullBiomeData) {
		this.worldMetadataJson = worldMetadataJson;
		this.quarterBiomeData = quarterBiomeData;
		this.fullBiomeData = fullBiomeData;
	}

	@Override
	public MinecraftInterface.World createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		if (worldMetadataJson.getSeed() == seed && worldMetadataJson.getWorldType().equals(worldType)
				&& generatorOptions.isEmpty()) {
			return new World();
		} else {
			throw new MinecraftInterfaceException("the world has to match");
		}
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return worldMetadataJson.getRecognisedVersion();
	}

	private class World implements MinecraftInterface.World {
		private World() {
		}

		@Override
		public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
				throws MinecraftInterfaceException {
			BiomeDataJson biomes = useQuarterResolution ? quarterBiomeData : fullBiomeData;
			return biomes.get(x, y, width, height);
		}
	}
}
