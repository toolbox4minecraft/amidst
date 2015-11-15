package amidst.minecraft.world;

public class SeedWorld implements World {
	private long seed;
	private WorldType generatorType;

	public SeedWorld(long seed, WorldType generatorType) {
		this.seed = seed;
		this.generatorType = generatorType;
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
	public WorldType getGeneratorType() {
		return generatorType;
	}
}
