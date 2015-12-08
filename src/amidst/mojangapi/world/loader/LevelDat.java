package amidst.mojangapi.world.loader;

import org.jnbt.CompoundTag;

import amidst.mojangapi.world.WorldType;

public class LevelDat {
	private final CompoundTag dataTag;
	private final long seed;
	private final WorldType worldType;
	private final String generatorOptions;

	public LevelDat(CompoundTag root) {
		this.dataTag = readDataTag(root);
		this.seed = readRandomSeed();
		if (hasGeneratorName()) {
			this.worldType = WorldType.from(readGeneratorName());
			if (worldType == WorldType.CUSTOMIZED) {
				this.generatorOptions = readGeneratorOptions();
			} else {
				this.generatorOptions = "";
			}
		} else {
			this.worldType = WorldType.DEFAULT;
			this.generatorOptions = "";
		}
	}

	private CompoundTag readDataTag(CompoundTag root) {
		return (CompoundTag) root.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private Long readRandomSeed() {
		return (Long) dataTag.getValue().get(NBTTagKeys.TAG_KEY_RANDOM_SEED)
				.getValue();
	}

	private boolean hasGeneratorName() {
		return dataTag.getValue().get(NBTTagKeys.TAG_KEY_GENERATOR_NAME) != null;
	}

	private String readGeneratorName() {
		return (String) dataTag.getValue()
				.get(NBTTagKeys.TAG_KEY_GENERATOR_NAME).getValue();
	}

	private String readGeneratorOptions() {
		return (String) dataTag.getValue()
				.get(NBTTagKeys.TAG_KEY_GENERATOR_OPTIONS).getValue();
	}

	public long getSeed() {
		return seed;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}
}
