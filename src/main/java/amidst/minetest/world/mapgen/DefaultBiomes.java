package amidst.minetest.world.mapgen;

import java.util.LinkedHashMap;
import java.util.Map;

import amidst.mojangapi.world.biome.BiomeColor;

public class DefaultBiomes {

	private static Map<String, MinetestBiome[]> biomeLists = null;

	public static final String BIOMEPROFILENAME_V6      = "V6 biomes";
	public static final String BIOMEPROFILENAME_DEFAULT = "Minetest default";
	
	/**
	 * The first item in the list will be considered the definitive biome list
	 */
	public static Map<String, MinetestBiome[]> getDefaultBiomeSets() {
		if (biomeLists == null) {
			biomeLists = new LinkedHashMap<String, MinetestBiome[]>();
			
			biomeLists.put(
				BIOMEPROFILENAME_DEFAULT,
				setIndexes(
					new MinetestBiome[] {
						new MinetestBiome("Icesheet",                BiomeColor.from(202, 216, 229), (short)  -8, Short.MAX_VALUE,  0, 73),	
						new MinetestBiome("Icesheet ocean",          BiomeColor.from( 89, 123, 178), (short)-112,       (short)-9,  0, 73),	
						new MinetestBiome("Tundra",                  BiomeColor.from(171, 124,  58), (short)   2, Short.MAX_VALUE,  0, 40),	
						new MinetestBiome("Tundra beach",            BiomeColor.from(214, 186, 109), (short)  -3,        (short)1,  0, 40),	
						new MinetestBiome("Tundra ocean",            BiomeColor.from( 86, 114, 161), (short)-112,       (short)-4,  0, 40),	
						new MinetestBiome("Taiga",                   BiomeColor.from(124, 190, 150), (short)   2, Short.MAX_VALUE, 25, 70),	
						new MinetestBiome("Taiga ocean",             BiomeColor.from( 82, 121, 170), (short)-112,       (short)-1, 25, 70),	
						new MinetestBiome("Snowy grassland",         BiomeColor.from(124, 184, 108), (short)   5, Short.MAX_VALUE, 20, 35),	
						new MinetestBiome("Snowy grassland ocean",   BiomeColor.from( 82, 120, 166), (short)-112,       (short)-4, 20, 35),	
						new MinetestBiome("Grassland",               BiomeColor.from( 97, 157,  53), (short)   6, Short.MAX_VALUE, 50, 35),	
						new MinetestBiome("Grassland dunes",         BiomeColor.from(174, 175,  81), (short)   4,        (short)5, 50, 35),	
						new MinetestBiome("Grassland ocean",         BiomeColor.from( 81, 122, 148), (short)-112,        (short)3, 50, 35),	
						new MinetestBiome("Coniferous forest",       BiomeColor.from( 11, 107,  95), (short)   6, Short.MAX_VALUE, 45, 70),	
						new MinetestBiome("Coniferous forest dunes", BiomeColor.from(104, 122, 110), (short)   4,        (short)5, 45, 70),	
						new MinetestBiome("Coniferous forest ocean", BiomeColor.from( 64, 112, 157), (short)-112,        (short)3, 45, 70),	
						new MinetestBiome("Deciduous forest",        BiomeColor.from( 12,  94,  11), (short)   1, Short.MAX_VALUE, 60, 68),
						new MinetestBiome("Deciduous forest shore",  BiomeColor.from(144, 148,   9), (short)  -1,        (short)0, 60, 68),
						new MinetestBiome("Deciduous forest ocean",  BiomeColor.from( 64, 109, 140), (short)-112,       (short)-2, 60, 68),
						new MinetestBiome("Desert",                  BiomeColor.from(216, 210, 156), (short)   5, Short.MAX_VALUE, 92, 16),	
						new MinetestBiome("Desert ocean",            BiomeColor.from(105, 132, 169), (short)-112,        (short)4, 92, 16),	
						new MinetestBiome("Sandstone desert",        BiomeColor.from(216, 174, 156), (short)   5, Short.MAX_VALUE, 60,  0),	
						new MinetestBiome("Sandstone desert ocean",  BiomeColor.from(105, 125, 169), (short)-112,        (short)4, 60,  0),	
						new MinetestBiome("Cold desert",             BiomeColor.from(144, 144, 160), (short)   5, Short.MAX_VALUE, 40,  0),	
						new MinetestBiome("Cold desert ocean",       BiomeColor.from( 86, 108, 151), (short)-112,        (short)4, 40,  0),	
						new MinetestBiome("Savanna",                 BiomeColor.from(139, 142,  64), (short)   1, Short.MAX_VALUE, 89, 42),	
						new MinetestBiome("Savanna shore",           BiomeColor.from(213, 199, 109), (short)  -1,        (short)0, 89, 42),	
						new MinetestBiome("Savanna ocean",           BiomeColor.from( 86, 142, 170), (short)-112,       (short)-2, 89, 42),	
						new MinetestBiome("Rainforest",              BiomeColor.from(  0,  63,   1), (short)   1, Short.MAX_VALUE, 86, 65),	
						new MinetestBiome("Rainforest swamp",        BiomeColor.from( 24,  63,  60), (short)  -1,        (short)0, 86, 65),	
						new MinetestBiome("Rainforest ocean",        BiomeColor.from( 72, 134, 163), (short)-112,       (short)-2, 86, 65),

						// Underground (Amidst don't care) 
						// new MinetestBiome("Underground",          BiomeColor.from( 20,  20,  20), -Short.MAX_VALUE, (short)-113, 50, 50), 
					}
				)
			);

			
			biomeLists.put(
					BIOMEPROFILENAME_V6,
					setIndexes(
						new MinetestBiome[] {
							// Keep in alphabetical order, as v6 references them by index, and biome sets are ordered
							// alphabetically when saved.
							new MinetestBiome("Beach",  BiomeColor.from(230, 230, 130), (short)   1,                      (short)5, 50, 35),	
							new MinetestBiome("Desert", BiomeColor.from(216, 210, 156), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 92, 16),	
							new MinetestBiome("Jungle", BiomeColor.from(  0,  63,   1), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 86, 65),	
							new MinetestBiome("Normal", BiomeColor.from( 97, 157,  53), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 50, 35),	
							new MinetestBiome("Taiga",  BiomeColor.from(124, 190, 150), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 25, 70),	
							new MinetestBiome("Tundra", BiomeColor.from(202, 216, 229), (short)   Short.MIN_VALUE, Short.MAX_VALUE,  0, 40),	
						}
					)
				);
			
			
			biomeLists.put(
				"30-biomes", // https://github.com/Gael-de-Sailly/30-biomes
				setIndexes(
					new MinetestBiome[] {
						// Main biomes
						new MinetestBiome("Glacier 1",               BiomeColor.from(232, 246, 255), (short)  -6, Short.MAX_VALUE, -11,  20), 
						new MinetestBiome("Glacier 2",               BiomeColor.from(217, 231, 244), (short)   1, Short.MAX_VALUE, -19,  40), 
						new MinetestBiome("Glacier 3",               BiomeColor.from(202, 216, 229), (short)   1, Short.MAX_VALUE, -27,  80), 
						new MinetestBiome("Taiga",                   BiomeColor.from(124, 190, 150), (short)   4, Short.MAX_VALUE,  11,  67), 
						new MinetestBiome("Tundra",                  BiomeColor.from(171, 124,  58), (short)   3, Short.MAX_VALUE,   4,  32), 
						new MinetestBiome("Coniferous forest",       BiomeColor.from( 11, 107,  95), (short)   5, Short.MAX_VALUE,  22,  71), 
						new MinetestBiome("Cold gravel desert",      BiomeColor.from(220, 220, 230), (short)  -6, Short.MAX_VALUE,  -2,   2), 
						new MinetestBiome("Gravel desert",           BiomeColor.from(220, 220, 230), (short)   2, Short.MAX_VALUE,  20,  -2), 
						new MinetestBiome("Dry tundra",              BiomeColor.from(220, 220, 230), (short)   2, Short.MAX_VALUE,   4,  12), 
						new MinetestBiome("Cold desert",             BiomeColor.from(144, 144, 160), (short)   3, Short.MAX_VALUE,  32,  -3), 
						new MinetestBiome("Swamp",                   BiomeColor.from(220, 220, 230), (short)   1, Short.MAX_VALUE,  30, 114), 
						new MinetestBiome("Icy swamp",               BiomeColor.from(220, 220, 230), (short)   1, Short.MAX_VALUE, -10, 107), 
						new MinetestBiome("Stone grasslands",        BiomeColor.from(124, 184, 108), (short)   4, Short.MAX_VALUE,  29,  22), 
						new MinetestBiome("Mixed forest",            BiomeColor.from(220, 220, 230), (short)   5, Short.MAX_VALUE,  24,  56), 
						new MinetestBiome("Cold deciduous forest",   BiomeColor.from(220, 220, 230), (short)   4, Short.MAX_VALUE,  31,  48), 
						new MinetestBiome("Deciduous forest",        BiomeColor.from( 12,  94,  11), (short)   5, Short.MAX_VALUE,  45,  65), 
						new MinetestBiome("Bushes",                  BiomeColor.from(220, 220, 230), (short)   7, Short.MAX_VALUE,  43,  30), 
						new MinetestBiome("Scrub",                   BiomeColor.from( 97, 157,  53), (short)   8, Short.MAX_VALUE,  57,  25), 
						new MinetestBiome("Hot pine forest",         BiomeColor.from(220, 220, 230), (short)   6, Short.MAX_VALUE,  65,  35), 
						new MinetestBiome("Desert",                  BiomeColor.from(216, 210, 156), (short)   7, Short.MAX_VALUE,  89,   9), 
						new MinetestBiome("Sandstone grasslands",    BiomeColor.from(220, 220, 230), (short)   3, Short.MAX_VALUE,  55,  15), 
						new MinetestBiome("Savanna",                 BiomeColor.from(139, 142,  64), (short)   6, Short.MAX_VALUE,  80,  24), 
						new MinetestBiome("Desert stone grasslands", BiomeColor.from(220, 220, 230), (short)   4, Short.MAX_VALUE,  90,  41), 
						new MinetestBiome("Red savanna",             BiomeColor.from(220, 220, 230), (short)   5, Short.MAX_VALUE,  90,  31), 
						new MinetestBiome("Semi-tropical forest",    BiomeColor.from(220, 220, 230), (short)   4, Short.MAX_VALUE,  72,  61), 
						new MinetestBiome("Rainforest",              BiomeColor.from(  0,  63,   1), (short)   1, Short.MAX_VALUE,  89,  76), 
						new MinetestBiome("Sandstone desert",        BiomeColor.from(216, 174, 156), (short)   6, Short.MAX_VALUE,  60,   0), 
						new MinetestBiome("Orchard",                 BiomeColor.from(220, 220, 230), (short)   6, Short.MAX_VALUE,  40,  40), 
						new MinetestBiome("Hot deciduous forest",    BiomeColor.from(220, 220, 230), (short)   5, Short.MAX_VALUE,  61,  58), 
						new MinetestBiome("Hot swamp",               BiomeColor.from(220, 220, 230), (short)   1, Short.MAX_VALUE,  83, 116), 
 
						// Beach biomes 
						new MinetestBiome("Gravel beach",            BiomeColor.from(220, 220, 230), (short)  -7,       (short) 6,  19,  19), 
						new MinetestBiome("Sand dunes",              BiomeColor.from(213, 199, 109), (short)  -5,       (short) 5,  21,  61), 
						new MinetestBiome("Mangrove",                BiomeColor.from( 24,  63,  60), (short)  -4,       (short) 0,  96, 106), 
						new MinetestBiome("Desert dunes",            BiomeColor.from(220, 220, 230), (short)  -5,       (short) 6,  70,   6), 
						new MinetestBiome("Hot sand dunes",          BiomeColor.from(220, 220, 230), (short)  -5,       (short) 5, 106,  49), 
						new MinetestBiome("Tundra dunes",            BiomeColor.from(214, 186, 109), (short)  -5,       (short) 2,   5,  21), 
						                                                                                                                
						new MinetestBiome("Glacier 2 shore",         BiomeColor.from(220, 220, 230), (short)  -4,       (short) 0, -19,  40), 
						new MinetestBiome("Glacier 3 shore",         BiomeColor.from(220, 220, 230), (short)  -4,       (short) 0, -27,  80), 
						new MinetestBiome("Swamp shore",             BiomeColor.from(220, 220, 230), (short)  -3,       (short) 0,  30, 114), 
						new MinetestBiome("Icy swamp shore",         BiomeColor.from(220, 220, 230), (short)  -4,       (short) 0, -10, 107), 
						new MinetestBiome("Hot swamp shore",         BiomeColor.from(220, 220, 230), (short)  -3,       (short) 0,  83, 116), 
						                                                                                                          
						// Sea biomes                                                                                             
						new MinetestBiome("Pack ice",                BiomeColor.from(220, 220, 255), (short)-112,       (short)-6, -24,  41), 
						new MinetestBiome("Cold sea",                BiomeColor.from( 30,  30,  90), (short)-112,       (short)-6,  20,  47), 
						new MinetestBiome("Tempered sea",            BiomeColor.from(  0,  20,  90), (short)-112,       (short)-6,  41,  48), 
						new MinetestBiome("Warm sea",                BiomeColor.from(  0,  90,  90), (short)-112,       (short)-6,  89,  51) 

						// Underground (Amidst don't care) 
						// new MinetestBiome("Underground",          BiomeColor.from( 20,  20,  20), -Short.MAX_VALUE, (short)-113, 50, 50), 
					}
				)
			);
			
			biomeLists.put(
					"VoxelGardon",
					setIndexes(
						new MinetestBiome[] {
							new MinetestBiome("Sea dirt",                BiomeColor.from( 81, 122, 148), (short)-32000,        (short)0,  30,  40),	
							new MinetestBiome("Sea sand",                BiomeColor.from( 86, 142, 170), (short)-32000,        (short)5,  90,  40),	
							new MinetestBiome("Sea desert sand",         BiomeColor.from(105, 132, 169), (short)-32000,        (short)5, 110, -60),	
							new MinetestBiome("Sea gravel",              BiomeColor.from( 86, 108, 151), (short)-32000,        (short)5,  30, -60),	

							new MinetestBiome("Conifer",                 BiomeColor.from( 11, 107,  95), (short)     1, Short.MAX_VALUE,   0,  70),	
							new MinetestBiome("Conifer-Tree transition", BiomeColor.from( 12, 100,  50), (short)     1, Short.MAX_VALUE,  25,  75),
							new MinetestBiome("Tree",                    BiomeColor.from( 12,  94,  11), (short)     1, Short.MAX_VALUE,  50,  80),	
							new MinetestBiome("Tree-Jungle transition",  BiomeColor.from(  6,  80,   4), (short)     1, Short.MAX_VALUE,  90,  75),
							new MinetestBiome("Jungle",                  BiomeColor.from(  0,  63,   1), (short)     1, Short.MAX_VALUE, 100,  70),	
							new MinetestBiome("Desert",                  BiomeColor.from(216, 210, 156), (short)     1, Short.MAX_VALUE, 120, -45),	
							new MinetestBiome("Glacier",                 BiomeColor.from(202, 216, 229), (short)     1, Short.MAX_VALUE, -20, -30),	
							new MinetestBiome("Gravel ice",              BiomeColor.from(172, 186, 200), (short)     1, Short.MAX_VALUE, -40, -40),															
							new MinetestBiome("Gravel desert",           BiomeColor.from(144, 144, 160), (short)     1, Short.MAX_VALUE, 160, -20),	
						}
					)
				);
			
		}
		return biomeLists;
	}
	
	static MinetestBiome[] setIndexes(MinetestBiome[] array) {
		// Set correct indexes in case any part of the rest of the application needs them
		for(int i = array.length - 1; i >= 0; i--) array[i].setIndex(i);
		return array;		
	}
}
