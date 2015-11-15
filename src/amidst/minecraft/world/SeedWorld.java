package amidst.minecraft.world;

import amidst.minecraft.MinecraftUtil;

public class SeedWorld implements World {
	private long seed;
	private WorldType worldType;

	SeedWorld(long seed, WorldType worldType) {
		this.seed = seed;
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
	public WorldType getWorldType() {
		return worldType;
	}
}
