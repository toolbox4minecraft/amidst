package amidst.mojangapi.world.testworld.storage.json;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@Immutable
public class WorldMetadataJson {
	public static WorldMetadataJson from(World world) {
		return new WorldMetadataJson(
				world.getRecognisedVersion(),
				world.getWorldOptions().getWorldSeed().getLong(),
				world.getWorldOptions().getWorldType());
	}

	private volatile RecognisedVersion recognisedVersion;
	private volatile long seed;
	private volatile WorldType worldType;

	@GsonConstructor
	public WorldMetadataJson() {
	}

	public WorldMetadataJson(RecognisedVersion recognisedVersion, long seed, WorldType worldType) {
		this.recognisedVersion = recognisedVersion;
		this.seed = seed;
		this.worldType = worldType;
	}

	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	public long getSeed() {
		return seed;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public WorldOptions intoWorldOptions() {
		return new WorldOptions(WorldSeed.fromUserInput(seed + ""), worldType);
	}
}
