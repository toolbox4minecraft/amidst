package amidst.minecraft.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amidst.map.MapObjectPlayer;

public class FileWorld implements World {
	public static class Player {
		private Runnable positionChangedListener;

		private FileWorld world;

		private String playerName;
		private int x;
		private int z;

		private boolean moved = false;

		public Player(String playerName, int x, int z) {
			this.playerName = playerName;
			this.x = x;
			this.z = z;
		}

		private void setWorld(FileWorld world) {
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

		public void moveTo(int x, int z) {
			this.x = x;
			this.z = z;
			positionChangedListener.run();
			moved = true;
		}

		public void saveLocation() {
			if (moved) {
				world.mover.movePlayer(this);
				moved = false;
			}
		}

		public void setPositionChangedListener(Runnable listener) {
			this.positionChangedListener = listener;
		}

		@Override
		public String toString() {
			return "Player \"" + playerName + "\" at (" + x + ", " + z + ")";
		}
	}

	private PlayerMover mover;

	private long seed;
	private WorldType generatorType;
	private String generatorOptions;
	private boolean isMultiPlayerMap;
	private List<Player> players;
	private List<MapObjectPlayer> mapObjectPlayers;

	FileWorld(File worldFile, long seed, WorldType generatorType,
			String generatorOptions, boolean isMultiPlayerMap,
			List<Player> players) {
		this.seed = seed;
		this.generatorType = generatorType;
		this.generatorOptions = generatorOptions;
		this.isMultiPlayerMap = isMultiPlayerMap;
		this.players = Collections.unmodifiableList(players);
		this.mover = new PlayerMover(worldFile, isMultiPlayerMap);
		initPlayers();
		initMapObjectPlayers();
	}

	private void initPlayers() {
		for (Player player : players) {
			player.setWorld(this);
		}
	}

	private void initMapObjectPlayers() {
		List<MapObjectPlayer> result = new ArrayList<MapObjectPlayer>();
		for (Player player : players) {
			result.add(new MapObjectPlayer(player));
		}
		mapObjectPlayers = Collections.unmodifiableList(result);
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
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

	public List<MapObjectPlayer> getMapObjectPlayers() {
		return mapObjectPlayers;
	}
}
