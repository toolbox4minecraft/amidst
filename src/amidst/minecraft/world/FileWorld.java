package amidst.minecraft.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;

import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.icon.DefaultWorldIconTypes;

public class FileWorld extends World {
	public static class Player {
		private FileWorld world;

		private BufferedImage skin = DefaultWorldIconTypes.PLAYER.getImage();
		private String playerName;
		private CoordinatesInWorld coordinates;

		private boolean moved = false;

		public Player(String playerName, CoordinatesInWorld coordinates) {
			this.playerName = playerName;
			this.coordinates = coordinates;
		}

		private void setWorld(FileWorld world) {
			this.world = world;
		}

		public String getPlayerName() {
			return playerName;
		}

		public void moveTo(CoordinatesInWorld coordinates) {
			this.coordinates = coordinates;
			moved = true;
		}

		public CoordinatesInWorld getCoordinates() {
			return coordinates;
		}

		public void saveLocation() {
			if (moved) {
				world.mover.movePlayer(this);
				moved = false;
			}
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

	@Deprecated
	public List<Player> getMovablePlayers() {
		return players;
	}

	public void savePlayerLocations() {
		for (Player player : players) {
			player.saveLocation();
		}
	}
}
