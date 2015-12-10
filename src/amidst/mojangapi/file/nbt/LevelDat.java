package amidst.mojangapi.file.nbt;

import org.jnbt.CompoundTag;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldType;

@Immutable
public class LevelDat {
	private final long seed;
	private final WorldType worldType;
	private final String generatorOptions;

	public LevelDat(CompoundTag root) {
		CompoundTag dataTag = readDataTag(root);
		this.seed = readRandomSeed(dataTag);
		if (hasGeneratorName(dataTag)) {
			this.worldType = WorldType.from(readGeneratorName(dataTag));
			if (worldType == WorldType.CUSTOMIZED) {
				this.generatorOptions = readGeneratorOptions(dataTag);
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

	private long readRandomSeed(CompoundTag dataTag) {
		return (Long) dataTag.getValue().get(NBTTagKeys.TAG_KEY_RANDOM_SEED)
				.getValue();
	}

	private boolean hasGeneratorName(CompoundTag dataTag) {
		return dataTag.getValue().get(NBTTagKeys.TAG_KEY_GENERATOR_NAME) != null;
	}

	private String readGeneratorName(CompoundTag dataTag) {
		return (String) dataTag.getValue()
				.get(NBTTagKeys.TAG_KEY_GENERATOR_NAME).getValue();
	}

	private String readGeneratorOptions(CompoundTag dataTag) {
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
