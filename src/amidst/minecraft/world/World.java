package amidst.minecraft.world;

import java.io.File;
import java.util.List;

import amidst.map.MapObjectPlayer;

public class World {
	private File worldFile;

	private long seed;
	private WorldType generatorType;
	private String generatorOptions;
	private boolean isMultiPlayerMap;
	private List<MapObjectPlayer> players;

	public World(File worldFile, long seed, WorldType generatorType,
			String generatorOptions, boolean isMultiPlayerMap,
			List<MapObjectPlayer> players) {
		this.worldFile = worldFile;
		this.seed = seed;
		this.generatorType = generatorType;
		this.generatorOptions = generatorOptions;
		this.isMultiPlayerMap = isMultiPlayerMap;
		this.players = players;
	}

	public long getSeed() {
		return seed;
	}

	public WorldType getGeneratorType() {
		return generatorType;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public boolean isMultiPlayerMap() {
		return isMultiPlayerMap;
	}

	public List<MapObjectPlayer> getPlayers() {
		return players;
	}
}
