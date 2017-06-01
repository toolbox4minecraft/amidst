package amidst.mojangapi.file.nbt;

import java.io.File;
import java.io.IOException;

import org.jnbt.CompoundTag;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.parsing.FormatException;

@Immutable
public class LevelDatNbt {
	public static LevelDatNbt from(File file) throws IOException, FormatException {
		try {
			CompoundTag dataTag = readDataTag(NBTUtils.readTagFromFile(file));
			long seed = readRandomSeed(dataTag);
			CoordinatesInWorld worldSpawn = readWorldSpawn(dataTag);
			WorldType worldType = readWorldType(dataTag);
			String generatorOptions = readGeneratorOptions(dataTag, worldType);
			boolean hasPlayer = hasPlayerTag(dataTag);
			return new LevelDatNbt(seed, worldSpawn, worldType, generatorOptions, hasPlayer);
		} catch (NullPointerException e) {
			throw new FormatException("cannot read level.dat: " + file);
		}
	}

	private static CompoundTag readDataTag(CompoundTag root) {
		return (CompoundTag) root.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private static long readRandomSeed(CompoundTag dataTag) {
		return NBTUtils.getLongValue(dataTag.getValue().get(NBTTagKeys.TAG_KEY_RANDOM_SEED));
	}

	private static CoordinatesInWorld readWorldSpawn(CompoundTag dataTag) {
		return CoordinatesInWorld.from(readSpawnX(dataTag), readSpawnZ(dataTag));
	}

	private static long readSpawnX(CompoundTag dataTag) {
		return NBTUtils.getLongValue(dataTag.getValue().get(NBTTagKeys.TAG_KEY_SPAWN_X));
	}

	private static long readSpawnZ(CompoundTag dataTag) {
		return NBTUtils.getLongValue(dataTag.getValue().get(NBTTagKeys.TAG_KEY_SPAWN_Z));
	}

	private static WorldType readWorldType(CompoundTag dataTag) {
		if (hasGeneratorName(dataTag)) {
			return WorldType.from(readGeneratorName(dataTag));
		} else {
			return WorldType.DEFAULT;
		}
	}

	private static boolean hasGeneratorName(CompoundTag dataTag) {
		return dataTag.getValue().get(NBTTagKeys.TAG_KEY_GENERATOR_NAME) != null;
	}

	private static String readGeneratorOptions(CompoundTag dataTag, WorldType worldType) {
		if (worldType == WorldType.CUSTOMIZED) {
			return readGeneratorOptions(dataTag);
		} else {
			return "";
		}
	}

	private static String readGeneratorName(CompoundTag dataTag) {
		return (String) dataTag.getValue().get(NBTTagKeys.TAG_KEY_GENERATOR_NAME).getValue();
	}

	private static String readGeneratorOptions(CompoundTag dataTag) {
		return (String) dataTag.getValue().get(NBTTagKeys.TAG_KEY_GENERATOR_OPTIONS).getValue();
	}

	private static boolean hasPlayerTag(CompoundTag dataTag) {
		return dataTag.getValue().containsKey(NBTTagKeys.TAG_KEY_PLAYER);
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
