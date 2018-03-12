package amidst.gameengineabstraction.world.biome;

import amidst.mojangapi.world.biome.BiomeColor;

public interface IBiome {

	String getName();
	
	int getIndex();
	
	BiomeColor getDefaultColor();
	
	/**
	 * Used by Biome widget. 
	 * In Minecraft this is Biomes above 128, other engines can ignore
	 * @return whether the biome is a rare biome that players are frequently hunting for.
	 * */	
	boolean isSpecialBiome();
}
