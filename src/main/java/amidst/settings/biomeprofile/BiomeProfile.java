package amidst.settings.biomeprofile;

import java.io.File;
import java.util.Collection;

import amidst.documentation.Immutable;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;

@Immutable
public interface BiomeProfile {

	/**
	 * All BiomeProfile classes MUST implement a static function getDefaultProfile()
	 * which returns an instance of that class.
	 * It will be invoked via reflection.
	 */
	//public static BiomeProfile getDefaultProfile();

	String getName();

	String getShortcut();

	boolean validate();

	BiomeColor[] createBiomeColorArray();
	
	boolean save(File file);
	
	/** 
	 * The Minecraft BiomeProfile will just pass this through to the fixed Biome singleton,
	 * but the Minetest BiomeProfile determines the biomes itself.
	 */
	Collection<IBiome> allBiomes();
	
	/** 
	 * The Minecraft BiomeProfile will just pass this through to the fixed Biome singleton,
	 * but the Minetest BiomeProfile determines the biomes itself.
	 */
	IBiome getByIndex(int index) throws UnknownBiomeIndexException;

	/**
	 * Minecraft biome data isn't affected by the BiomeProfiles, however engines which
	 * are (Minetest) should return true here.
	 */
	boolean invalidatesBiomeData();
}
