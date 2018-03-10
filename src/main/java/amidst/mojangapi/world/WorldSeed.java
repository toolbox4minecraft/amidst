package amidst.mojangapi.world;

import java.util.Random;
import java.util.AbstractMap.SimpleEntry;

import amidst.documentation.Immutable;
import amidst.gameengineabstraction.GameEngineType;
import amidst.minetest.world.mapgen.Numeric;

@Immutable
public class WorldSeed {
	public static enum WorldSeedType {
		// @formatter:off
		TEXT           ("Text Seed"),
		NUMERIC     ("Numeric Seed"),
		SAVE_GAME ("Save Game Seed"),
		RANDOM       ("Random Seed");
		// @formatter:on

		private final String labelPrefix;

		private WorldSeedType(String labelPrefix) {
			this.labelPrefix = labelPrefix;
		}

		private String getLabel(long seed, String text, GameEngineType gameEngine) {
			
			String seedValue = Long.toString(seed);
			if (gameEngine != GameEngineType.MINECRAFT) {
				seedValue = Long.toUnsignedString(seed);				
			}
					
			if (this == TEXT) {
				return labelPrefix + ": '" + text + "' (" + seedValue + ")";
			} else {
				return labelPrefix + ": " + seedValue;
			}
		}
	}

	public static WorldSeed random(GameEngineType game_engine_type) {
		return new WorldSeed(new Random().nextLong(), null, WorldSeedType.RANDOM, game_engine_type);
	}

	public static WorldSeed fromUserInput(String input, GameEngineType engine_type) {
		if (input.isEmpty()) {
			return random(engine_type);
		}
		if (engine_type == GameEngineType.MINETESTv7) {
			SimpleEntry<Long, Boolean> covertedSeed = Numeric.stringToSeed(input);
			return new WorldSeed(
					covertedSeed.getKey(), 
					input, 
					covertedSeed.getValue() ? WorldSeedType.TEXT : WorldSeedType.NUMERIC, 
					engine_type);			
		} else {		
			try {
				long seed = Long.parseLong(input);
				return new WorldSeed(seed, null, WorldSeedType.NUMERIC, engine_type);
			} catch (NumberFormatException err) {
				int seed = input.hashCode();
				return new WorldSeed(seed, input, WorldSeedType.TEXT, engine_type);
			}
		}
	}

	public static WorldSeed fromSaveGame(long seed, GameEngineType game_engine_type) {
		return new WorldSeed(seed, null, WorldSeedType.SAVE_GAME, game_engine_type);
	}

	private final long seed;
	private final String text;
	private final WorldSeedType type;
	private final String label;
	private final GameEngineType gameEngineType;

	private WorldSeed(long seed, String text, WorldSeedType type, GameEngineType game_engine_type) {
		this.seed = seed;
		this.text = text;
		this.type = type;
		this.gameEngineType = game_engine_type;
		this.label = type.getLabel(seed, text, game_engine_type);
	}

	public long getLong() {
		return seed;
	}
	
	public String getText() {
		return text;
	}

	public WorldSeedType getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}
}
