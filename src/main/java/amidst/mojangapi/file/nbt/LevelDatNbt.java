package amidst.mojangapi.file.nbt;

import java.io.IOException;
import java.nio.file.Path;

import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.parsing.FormatException;

@Immutable
public class LevelDatNbt {
	public static LevelDatNbt from(Path path) throws IOException, FormatException {
		try {
			CompoundTag dataTag = readDataTag(NBTUtils.readTagFromFile(path));
			long seed = readRandomSeed(dataTag);
			CoordinatesInWorld worldSpawn = readWorldSpawn(dataTag);
			WorldType worldType = readWorldType(dataTag);
			String generatorOptions = readGeneratorOptions(dataTag, worldType);
			boolean hasPlayer = hasPlayerTag(dataTag);
			return new LevelDatNbt(seed, worldSpawn, worldType, generatorOptions, hasPlayer);
		} catch (NullPointerException | ClassCastException e) {
			throw new FormatException("cannot read level.dat: " + path, e);
		}
	}

	private static CompoundTag readDataTag(CompoundTag root) throws IOException {
		return root.get(NBTTagKeys.TAG_KEY_DATA, CompoundTag.class);
	}

	private static long readRandomSeed(CompoundTag dataTag) {
		Tag<?> randomSeed = dataTag.get(NBTTagKeys.TAG_KEY_RANDOM_SEED);
		if (randomSeed != null) {
			return NBTUtils.getLongValue(randomSeed);
		}
		// Minecraft 1.16 format
		CompoundTag worldGenSettings = dataTag.get(NBTTagKeys.TAG_KEY_WORLD_GEN_SETTINGS, CompoundTag.class);
		return NBTUtils.getLongValue(worldGenSettings.get(NBTTagKeys.TAG_KEY_SEED));
	}

	private static CoordinatesInWorld readWorldSpawn(CompoundTag dataTag) {
		return CoordinatesInWorld.from(readSpawnX(dataTag), readSpawnZ(dataTag));
	}

	private static long readSpawnX(CompoundTag dataTag) {
		return NBTUtils.getLongValue(dataTag.get(NBTTagKeys.TAG_KEY_SPAWN_X));
	}

	private static long readSpawnZ(CompoundTag dataTag) {
		return NBTUtils.getLongValue(dataTag.get(NBTTagKeys.TAG_KEY_SPAWN_Z));
	}

	private static WorldType readWorldType(CompoundTag dataTag) {
		WorldType heuristicWorldType = tryGuessWorldType(dataTag);
		if (heuristicWorldType != null) {
			return heuristicWorldType;
		} else if (hasGeneratorName(dataTag)) {
			return WorldType.from(readGeneratorName(dataTag));
		} else {
			return WorldType.DEFAULT;
		}
	}

	private static boolean hasGeneratorName(CompoundTag dataTag) {
		return dataTag.containsKey(NBTTagKeys.TAG_KEY_GENERATOR_NAME);
	}

	private static String readGeneratorOptions(CompoundTag dataTag, WorldType worldType) {
		if (worldType == WorldType.CUSTOMIZED) {
			return readGeneratorOptions(dataTag);
		} else {
			return "";
		}
	}

	private static String readGeneratorName(CompoundTag dataTag) {
		return dataTag.get(NBTTagKeys.TAG_KEY_GENERATOR_NAME, StringTag.class).getValue();
	}

	// Try to guess world type from Minecraft 1.16 generator settings
	private static WorldType tryGuessWorldType(CompoundTag dataTag) {
		// Be careful when handling null values, we don't want to trigger unwanted errors
		Tag<?> generator = NBTUtils.getNestedTag(dataTag,
			"WorldGenSettings", "dimensions", "minecraft:overworld", "generator");

		Tag<?> generatorType = NBTUtils.getNestedTag(generator, "type");
		if (new StringTag("minecraft:flat").equals(generatorType)) {
			return WorldType.FLAT;
		}

		if (new StringTag("minecraft:noise").equals(generatorType)) {
			Tag<?> settings = NBTUtils.getNestedTag(generator, "settings");
			if (new StringTag("minecraft:amplified").equals(settings)) {
				return WorldType.AMPLIFIED;
			}
			Tag<?> largeBiomes = NBTUtils.getNestedTag(generator, "biome_source", "large_biomes");
			if (new ByteTag(true).equals(largeBiomes)) {
				return WorldType.LARGE_BIOMES;
			} else {
				return WorldType.DEFAULT;
			}
		}

		return null;
	}

	private static String readGeneratorOptions(CompoundTag dataTag) {
		return dataTag.get(NBTTagKeys.TAG_KEY_GENERATOR_OPTIONS, StringTag.class).getValue();
	}

	private static boolean hasPlayerTag(CompoundTag dataTag) {
		return dataTag.containsKey(NBTTagKeys.TAG_KEY_PLAYER);
	}

	private final long seed;
	private final CoordinatesInWorld worldSpawn;
	private final WorldType worldType;
	private final String generatorOptions;
	private final boolean hasPlayer;

	public LevelDatNbt(
			long seed,
			CoordinatesInWorld worldSpawn,
			WorldType worldType,
			String generatorOptions,
			boolean hasPlayer) {
		this.seed = seed;
		this.worldSpawn = worldSpawn;
		this.worldType = worldType;
		this.generatorOptions = generatorOptions;
		this.hasPlayer = hasPlayer;
	}

	public long getSeed() {
		return seed;
	}

	public CoordinatesInWorld getWorldSpawn() {
		return worldSpawn;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public boolean hasPlayer() {
		return hasPlayer;
	}
}
