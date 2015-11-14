package amidst.minecraft.world;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class World {
	public static class Player {
		private World world;

		private String playerName;
		private int x;
		private int z;

		public Player(String playerName, int x, int z) {
			this.playerName = playerName;
			this.x = x;
			this.z = z;
		}

		private void setWorld(World world) {
			this.world = world;
		}

		public String getPlayerName() {
			return playerName;
		}

		public int getX() {
			return x;
		}

		public int getZ() {
			return z;
		}
	}

	private File worldFile;

	private long seed;
	private WorldType generatorType;
	private String generatorOptions;
	private boolean isMultiPlayerMap;
	private List<Player> players;

	public World(File worldFile, long seed, WorldType generatorType,
			String generatorOptions, boolean isMultiPlayerMap,
			List<Player> players) {
		this.worldFile = worldFile;
		this.seed = seed;
		this.generatorType = generatorType;
		this.generatorOptions = generatorOptions;
		this.isMultiPlayerMap = isMultiPlayerMap;
		this.players = Collections.unmodifiableList(players);
		initPlayers();
	}

	private void initPlayers() {
		for (Player player : players) {
			player.setWorld(this);
		}
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

	public List<Player> getPlayers() {
		return players;
	}
}
