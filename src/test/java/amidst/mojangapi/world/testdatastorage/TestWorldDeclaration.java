package amidst.mojangapi.world.testdatastorage;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@Immutable
public enum TestWorldDeclaration {
	WORLD1(RecognisedVersion.V1_8_9, "amidst-test-seed", WorldType.DEFAULT);

	private final RecognisedVersion recognisedVersion;
	private final WorldSeed worldSeed;
	private final WorldType worldType;

	private TestWorldDeclaration(RecognisedVersion recognisedVersion,
			String seed, WorldType worldType) {
		this.recognisedVersion = recognisedVersion;
		this.worldSeed = WorldSeed.fromUserInput(seed);
		this.worldType = worldType;
	}

	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	public WorldSeed getWorldSeed() {
		return worldSeed;
	}

	public WorldType getWorldType() {
		return worldType;
	}
}
