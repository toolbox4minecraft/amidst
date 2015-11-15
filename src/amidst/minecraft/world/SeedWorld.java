package amidst.minecraft.world;

public class SeedWorld implements World {
	private long seed;
	private WorldType worldType;

	public SeedWorld(long seed, WorldType worldType) {
		this.seed = seed;
		this.worldType = worldType;
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
