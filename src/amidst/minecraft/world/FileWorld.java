package amidst.minecraft.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;

import amidst.map.MapMarkers;
import amidst.minecraft.MinecraftUtil;

public class FileWorld extends World {
	public static class Player {
		private FileWorld world;

		private BufferedImage skin = MapMarkers.PLAYER.getImage();
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

		@Deprecated
		public void moveTo(CoordinatesInWorld coordinates) {
			x = (int) coordinates.getX();
			z = (int) coordinates.getY();
			moved = true;
		}

		@Deprecated
		public CoordinatesInWorld getCoordinates() {
			return CoordinatesInWorld.from(x, z);
		}

		public void saveLocation() {
			if (moved) {
				world.mover.movePlayer(this);
				moved = false;
			}
		}

		@Override
		public String toString() {
			return "Player \"" + playerName + "\" at (" + x + ", " + z + ")";
		}

		public void setSkin(BufferedImage skin) {
			this.skin = skin;
		}

		public BufferedImage getSkin() {
			return skin;
		}
	}

	private PlayerMover mover;

	private long seed;
	private WorldType worldType;
	private String generatorOptions;
	private boolean isMultiPlayerMap;
	private List<Player> players;

	FileWorld(File worldFile, long seed, WorldType worldType,
			String generatorOptions, boolean isMultiPlayerMap,
			List<Player> players) {
		this.seed = seed;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.isMultiPlayerMap = isMultiPlayerMap;
		this.players = Collections.unmodifiableList(players);
		this.mover = new PlayerMover(worldFile, isMultiPlayerMap);
		initPlayers();
		initMinecraftInterface();
	}

	private void initMinecraftInterface() {
		MinecraftUtil.createWorld(seed, worldType.getName(), generatorOptions);
	}

	private void initPlayers() {
		for (Player player : players) {
			player.setWorld(this);
		}
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
	public String getSeedText() {
		return null;
	}

	@Override
	public WorldType getWorldType() {
		return worldType;
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

	public void savePlayerLocations() {
		for (Player player : players) {
			player.saveLocation();
		}
	}
}
