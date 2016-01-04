package amidst.mojangapi.file.nbt;

import org.jnbt.CompoundTag;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.world.WorldType;

@Immutable
public class LevelDatNbt {
	private final long seed;
	private final WorldType worldType;
	private final String generatorOptions;
	private final boolean hasPlayer;

	public LevelDatNbt(CompoundTag root) throws MojangApiParsingException {
		try {
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
			this.hasPlayer = hasPlayerTag(dataTag);
		} catch (NullPointerException e) {
			throw new MojangApiParsingException("cannot read leve.dat", e);
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

	private boolean hasPlayerTag(CompoundTag dataTag) {
		return dataTag.getValue().containsKey(NBTTagKeys.TAG_KEY_PLAYER);
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

	public boolean hasPlayer() {
		return hasPlayer;
	}
}
