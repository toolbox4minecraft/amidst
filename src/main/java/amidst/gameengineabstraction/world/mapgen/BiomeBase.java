package amidst.gameengineabstraction.world.mapgen;

import amidst.mojangapi.world.biome.BiomeColor;

public abstract class BiomeBase {

	private final String name;
	private final int index;
	private final BiomeColor defaultColor;
		
	protected BiomeBase(String name, int index, BiomeColor defaultColor) {
		this.name = name;
		this.index = index;
		this.defaultColor = defaultColor;		
	}
	
	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public BiomeColor getDefaultColor() {
		return defaultColor;
	}

	@Override
	public String toString() {
		return "[Biome " + name + "]";
	}
}
