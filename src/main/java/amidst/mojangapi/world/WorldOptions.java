package amidst.mojangapi.world;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.SaveGame;

@Immutable
public class WorldOptions {
	private final WorldSeed worldSeed;
	private final WorldType worldType;
	private final String genOptions;
	
	public WorldOptions(WorldSeed seed, WorldType type, String generatorOptions) {
		worldSeed = seed;
		worldType = type;
		genOptions = generatorOptions;
	}
	
	public WorldOptions(WorldSeed seed, WorldType type) {
		this(seed, type, "");
	}
	
	public WorldOptions withGeneratorOptions(String generatorOptions) {
		return new WorldOptions(worldSeed, worldType, generatorOptions);
	}
	
	public static WorldOptions fromSaveGame(SaveGame saveGame) {
		return new WorldOptions(WorldSeed.fromSaveGame(saveGame.getSeed()), saveGame.getWorldType(), saveGame.getGeneratorOptions());
	}
	
	public WorldSeed getWorldSeed() {
		return worldSeed;
	}
	
	public WorldType getWorldType() {
		return worldType;
	}
	
	public String getGeneratorOptions() {
		return genOptions;
	}
}
