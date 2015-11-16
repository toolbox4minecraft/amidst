package amidst.minecraft.world;

import amidst.minecraft.MinecraftUtil;

public class SeedWorld extends World {
	private long seed;
	private String seedText;
	private WorldType worldType;

	SeedWorld(long seed, String seedText, WorldType worldType) {
		this.seed = seed;
		this.seedText = seedText;
		this.worldType = worldType;
		initMinecraftInterface();
	}

	private void initMinecraftInterface() {
		MinecraftUtil.createWorld(seed, worldType.getName());
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
	public String getSeedText() {
		return seedText;
	}

	@Override
	public WorldType getWorldType() {
		return worldType;
	}
}
