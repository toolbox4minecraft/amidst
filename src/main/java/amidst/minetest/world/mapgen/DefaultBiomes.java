package amidst.minetest.world.mapgen;

import java.time.LocalDate;
import java.time.Month;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.mojangapi.world.biome.BiomeColor;

public class DefaultBiomes {

	private static Map<String, MinetestBiome[]> biomeLists = null;

	public static final String BIOMEPROFILENAME_V6            = "v6";
	public static final String BIOMEPROFILENAME_MINETEST_GAME = "Minetest Game v5";
	
	public static final short FLOATLAND_SHADOW_LIMIT    = 1024;
	public static final short FLOATLAND_LEVEL           = 1280;
	
	/**
	 * List of biome profile files that were created by older versions of Admist
	 * and can now be deleted.
	 * @see getObsoleteBiomeProfilesDate()
	 */
	public static List<Entry<String, String>> getObsoleteBiomeProfiles() {
		
		List<Entry<String, String>> result = new ArrayList<Entry<String, String>>();
		
		result.add(new AbstractMap.SimpleEntry<String, String>("Minetest Game",    "c0f90f045b4111bab064f669958196fd73bad894")); // v1.3
		
		result.add(new AbstractMap.SimpleEntry<String, String>("Minetest default", "b9535c9299fd0ee92067c8c6b38a46c43d22be71")); // v1.1
		result.add(new AbstractMap.SimpleEntry<String, String>("V6 biomes",        "2c74eaf3c805c9f05797bfed906b70dd18772587")); // v1.1
		result.add(new AbstractMap.SimpleEntry<String, String>("VoxelGarden",      "8ab488cbbe5c3cbfc1aa3bae8c717104e98e00ca")); // v1.1

		return result;
	}
	
	/**
	 * Returns the date of the data provided by getObsoleteBiomeProfiles().
	 * If the returned date is younger than the last time obsolete biomes were removed, then
	 * no further checks are necessary. 
	 * @see getObsoleteBiomeProfiles()
	 */
	public static LocalDate getObsoleteBiomeProfilesDate() {
		return LocalDate.of(2018, Month.OCTOBER, 20);
	}
	
	/**
	 * The first item in the list will be considered the definitive biome list
	 */
	public static Map<String, MinetestBiome[]> getDefaultBiomeSets() {
		if (biomeLists == null) {
			biomeLists = new LinkedHashMap<String, MinetestBiome[]>();
			
			// Constructed from mapgen.lua in Master branch, where mapgen.lua last updated in ab1a79b13c on Oct 10, 2018
			// https://github.com/minetest/minetest_game/blob/ab1a79b13c5739d73083236ca29869b60ff3b380/mods/default/mapgen.lua
			biomeLists.put(
				BIOMEPROFILENAME_MINETEST_GAME,
				setIndexes(
					new MinetestBiome[] {
						new MinetestBiome("Icesheet",                BiomeColor.from(213, 221, 240), (short)  -8, FLOATLAND_SHADOW_LIMIT,  0, 73),
						new MinetestBiome("Icesheet ocean",          BiomeColor.from(216, 216, 183), (short)-112,           (short)   -9,  0, 73),
						new MinetestBiome("Tundra highland",         BiomeColor.from(228, 222, 220), (short)  47, FLOATLAND_SHADOW_LIMIT,  0, 40),
						new MinetestBiome("Tundra",                  BiomeColor.from( 82,  68,  63), (short)   2,           (short)   46,  0, 40),
						new MinetestBiome("Tundra beach",            BiomeColor.from(131, 131, 131), (short)  -3,            (short)   1,  0, 40),
						new MinetestBiome("Tundra ocean",            BiomeColor.from(214, 213, 179), (short)-112,           (short)   -4,  0, 40),
						new MinetestBiome("Taiga",                   BiomeColor.from(174, 191, 190), (short)   4, FLOATLAND_SHADOW_LIMIT, 25, 70),
						new MinetestBiome("Taiga ocean",             BiomeColor.from(208, 210, 177), (short)-112,            (short)   3, 25, 70),
						new MinetestBiome("Snowy grassland",         BiomeColor.from(187, 201, 187), (short)   4, FLOATLAND_SHADOW_LIMIT, 20, 35),
						new MinetestBiome("Snowy grassland ocean",   BiomeColor.from(210, 212, 177), (short)-112,            (short)   3, 20, 35),
						new MinetestBiome("Grassland",               BiomeColor.from( 76, 129,  36), (short)   6, FLOATLAND_SHADOW_LIMIT, 50, 35),
						new MinetestBiome("Grassland dunes",         BiomeColor.from(174, 175,  81), (short)   4,            (short)   5, 50, 35),
						new MinetestBiome("Grassland ocean",         BiomeColor.from(198, 204, 156), (short)-112,            (short)   3, 50, 35),
						new MinetestBiome("Coniferous forest",       BiomeColor.from( 25,  61,  33), (short)   6, FLOATLAND_SHADOW_LIMIT, 45, 70),
						new MinetestBiome("Coniferous forest dunes", BiomeColor.from(162, 180, 114), (short)   4,            (short)   5, 45, 70),
						new MinetestBiome("Coniferous forest ocean", BiomeColor.from(191, 195, 156), (short)-112,            (short)   3, 45, 70),
						new MinetestBiome("Deciduous forest",        BiomeColor.from( 12,  94,  11), (short)   1, FLOATLAND_SHADOW_LIMIT, 60, 68),
						new MinetestBiome("Deciduous forest shore",  BiomeColor.from( 95,  64,  39), (short)  -1,            (short)   0, 60, 68),
						new MinetestBiome("Deciduous forest ocean",  BiomeColor.from(190, 199, 153), (short)-112,           (short)   -2, 60, 68),
						new MinetestBiome("Desert",                  BiomeColor.from(206, 199, 152), (short)   4, FLOATLAND_SHADOW_LIMIT, 92, 16),
						new MinetestBiome("Desert ocean",            BiomeColor.from(215, 213, 171), (short)-112,            (short)   3, 92, 16),
						new MinetestBiome("Sandstone desert",        BiomeColor.from(194, 168, 127), (short)   4, FLOATLAND_SHADOW_LIMIT, 60,  0),
						new MinetestBiome("Sandstone desert ocean",  BiomeColor.from(212, 206, 165), (short)-112,            (short)   3, 60,  0),
						new MinetestBiome("Cold desert",             BiomeColor.from(193, 191, 179), (short)   4, FLOATLAND_SHADOW_LIMIT, 40,  0),
						new MinetestBiome("Cold desert ocean",       BiomeColor.from(211, 210, 175), (short)-112,            (short)   3, 40,  0),
						new MinetestBiome("Savanna",                 BiomeColor.from(139, 142,  64), (short)   1, FLOATLAND_SHADOW_LIMIT, 89, 42),
						new MinetestBiome("Savanna shore",           BiomeColor.from(113,  95,  49), (short)  -1,            (short)   0, 89, 42),
						new MinetestBiome("Savanna ocean",           BiomeColor.from(201, 200, 152), (short)-112,           (short)   -2, 89, 42),
						new MinetestBiome("Rainforest",              BiomeColor.from(  0,  60,   0), (short)   1, FLOATLAND_SHADOW_LIMIT, 86, 65),
						new MinetestBiome("Rainforest swamp",        BiomeColor.from(  0,  40,   0), (short)  -1,            (short)   0, 86, 65),
						new MinetestBiome("Rainforest ocean",        BiomeColor.from(188, 195, 151), (short)-112,           (short)   -2, 86, 65),

						// Underground (normally Amidst don't care, but here it's needed because ocean floor can drop lower than -112)						
						new MinetestBiome("Underground",             BiomeColor.from( 20,  20,  20), Short.MIN_VALUE, (short)-113, 50, 50),
						
						// Floatlands
						new MinetestBiome("Floatland grassland",         BiomeColor.from( 76, 129,  36), (short)(FLOATLAND_LEVEL + 2),              Short.MAX_VALUE, 50, 25),
						new MinetestBiome("Floatland coniferous forest", BiomeColor.from( 25,  61,  33), (short)(FLOATLAND_LEVEL + 2),              Short.MAX_VALUE, 50, 75),
						new MinetestBiome("Floatland ocean",             BiomeColor.from(198, 204, 156),       FLOATLAND_SHADOW_LIMIT, (short)(FLOATLAND_LEVEL + 1), 50, 50)
					}
				)
			);
			
			// Constructed from mapgen.lua in backport branch, where mapgen.lua last updated in c2d1a5e on 24 Apr 2017
			// (and the mapgen.lua file in stable-0.4 branch is identical)
			// https://github.com/minetest/minetest_game/blob/c2d1a5ed4bebd01a1a9340d55cb2d5edf6d59df2/mods/default/mapgen.lua
			biomeLists.put(
				"Minetest Game v.4",
				setIndexes(
					new MinetestBiome[] {
						new MinetestBiome("Icesheet",                BiomeColor.from(213, 221, 240), (short)  -8, FLOATLAND_SHADOW_LIMIT,  0, 73),
						new MinetestBiome("Icesheet ocean",          BiomeColor.from(216, 216, 183), (short)-112,              (short)-9,  0, 73),
						new MinetestBiome("Tundra",                  BiomeColor.from(205, 205, 216), (short)   2, FLOATLAND_SHADOW_LIMIT,  0, 40),
						new MinetestBiome("Tundra beach",            BiomeColor.from(131, 131, 131), (short)  -3,               (short)1,  0, 40),
						new MinetestBiome("Tundra ocean",            BiomeColor.from(214, 213, 179), (short)-112,              (short)-4,  0, 40),
						new MinetestBiome("Taiga",                   BiomeColor.from(174, 191, 190), (short)   2, FLOATLAND_SHADOW_LIMIT, 25, 70),
						new MinetestBiome("Taiga ocean",             BiomeColor.from(208, 210, 177), (short)-112,               (short)1, 25, 70),
						new MinetestBiome("Snowy grassland",         BiomeColor.from(187, 201, 187), (short)   5, FLOATLAND_SHADOW_LIMIT, 20, 35),
						new MinetestBiome("Snowy grassland ocean",   BiomeColor.from(210, 212, 177), (short)-112,               (short)4, 20, 35),
						new MinetestBiome("Grassland",               BiomeColor.from( 76, 129,  36), (short)   6, FLOATLAND_SHADOW_LIMIT, 50, 35),
						new MinetestBiome("Grassland dunes",         BiomeColor.from(174, 175,  81), (short)   5,               (short)5, 50, 35),
						new MinetestBiome("Grassland ocean",         BiomeColor.from(198, 204, 156), (short)-112,               (short)4, 50, 35),
						new MinetestBiome("Coniferous forest",       BiomeColor.from( 25,  61,  33), (short)   6, FLOATLAND_SHADOW_LIMIT, 45, 70),
						new MinetestBiome("Coniferous forest dunes", BiomeColor.from(162, 180, 114), (short)   5,               (short)5, 45, 70),
						new MinetestBiome("Coniferous forest ocean", BiomeColor.from(191, 195, 156), (short)-112,               (short)4, 45, 70),
						new MinetestBiome("Deciduous forest",        BiomeColor.from( 12,  94,  11), (short)   1, FLOATLAND_SHADOW_LIMIT, 60, 68),
						new MinetestBiome("Deciduous forest shore",  BiomeColor.from( 95,  64,  39), (short)  -1,               (short)0, 60, 68),
						new MinetestBiome("Deciduous forest ocean",  BiomeColor.from(190, 199, 153), (short)-112,              (short)-2, 60, 68),
						new MinetestBiome("Desert",                  BiomeColor.from(206, 199, 152), (short)   5, FLOATLAND_SHADOW_LIMIT, 92, 16),
						new MinetestBiome("Desert ocean",            BiomeColor.from(215, 213, 171), (short)-112,               (short)4, 92, 16),
						new MinetestBiome("Sandstone desert",        BiomeColor.from(194, 168, 127), (short)   5, FLOATLAND_SHADOW_LIMIT, 60,  0),
						new MinetestBiome("Sandstone desert ocean",  BiomeColor.from(212, 206, 165), (short)-112,               (short)4, 60,  0),
						new MinetestBiome("Cold desert",             BiomeColor.from(193, 191, 179), (short)   5, FLOATLAND_SHADOW_LIMIT, 40,  0),
						new MinetestBiome("Cold desert ocean",       BiomeColor.from(211, 210, 175), (short)-112,               (short)4, 40,  0),
						new MinetestBiome("Savanna",                 BiomeColor.from(139, 142,  64), (short)   1, FLOATLAND_SHADOW_LIMIT, 89, 42),
						new MinetestBiome("Savanna shore",           BiomeColor.from(113,  95,  49), (short)  -1,               (short)0, 89, 42),
						new MinetestBiome("Savanna ocean",           BiomeColor.from(201, 200, 152), (short)-112,              (short)-2, 89, 42),
						new MinetestBiome("Rainforest",              BiomeColor.from(  0,  60,   0), (short)   1, FLOATLAND_SHADOW_LIMIT, 86, 65),
						new MinetestBiome("Rainforest swamp",        BiomeColor.from(  0,  40,   0), (short)  -1,               (short)0, 86, 65),
						new MinetestBiome("Rainforest ocean",        BiomeColor.from(188, 195, 151), (short)-112,              (short)-2, 86, 65),

						// Underground (normally Amidst don't care, but here it's needed because ocean floor can drop lower than -112)						
						new MinetestBiome("Underground",             BiomeColor.from( 20,  20,  20), Short.MIN_VALUE, (short)-113, 50, 50),
						
						// Floatlands
						new MinetestBiome("floatland coniferous forest", BiomeColor.from( 25,  61,  33), (short)(FLOATLAND_LEVEL + 2),              Short.MAX_VALUE, 50, 70),
						new MinetestBiome("floatland grassland",         BiomeColor.from( 76, 129,  36), (short)(FLOATLAND_LEVEL + 2),              Short.MAX_VALUE, 50, 35),
						new MinetestBiome("floatland sandstone desert",  BiomeColor.from(194, 168, 127), (short)(FLOATLAND_LEVEL + 2),              Short.MAX_VALUE, 50,  0),
						new MinetestBiome("floatland ocean",             BiomeColor.from(198, 204, 156),       FLOATLAND_SHADOW_LIMIT, (short)(FLOATLAND_LEVEL + 1), 50, 50)						
					}
				)
			);

			
			biomeLists.put(
				BIOMEPROFILENAME_V6,
				setIndexes(
					new MinetestBiome[] {
						// Keep in alphabetical order, as v6 references them by index, and biome sets are ordered
						// alphabetically when saved.
						new MinetestBiome("Beach",  BiomeColor.from(214, 186, 109), (short)   1,                      (short)5, 50, 35),	
						new MinetestBiome("Desert", BiomeColor.from(206, 199, 152), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 92, 16),	
						new MinetestBiome("Jungle", BiomeColor.from(  0,  60,   0), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 86, 65),	
						new MinetestBiome("Normal", BiomeColor.from( 76, 129,  36), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 50, 35),	
						new MinetestBiome("Taiga",  BiomeColor.from(174, 191, 190), (short)   Short.MIN_VALUE, Short.MAX_VALUE, 25, 70),	
						new MinetestBiome("Tundra", BiomeColor.from(224, 225, 238), (short)   Short.MIN_VALUE, Short.MAX_VALUE,  0, 40),	
					}
				)
			);
			
			
			biomeLists.put(
				"30-biomes", // https://github.com/Gael-de-Sailly/30-biomes
				setIndexes(
					new MinetestBiome[] {
						// Main biomes
						new MinetestBiome("Glacier 1",               BiomeColor.from(213, 221, 240), (short)  -6, Short.MAX_VALUE, -11,  20), 
						new MinetestBiome("Glacier 2",               BiomeColor.from(225, 229, 247), (short)   1, Short.MAX_VALUE, -19,  40), 
						new MinetestBiome("Glacier 3",               BiomeColor.from(237, 238, 255), (short)   1, Short.MAX_VALUE, -27,  80), 
						new MinetestBiome("Taiga",                   BiomeColor.from(174, 191, 190), (short)   4, Short.MAX_VALUE,  11,  67), 
						new MinetestBiome("Tundra",                  BiomeColor.from(205, 205, 216), (short)   3, Short.MAX_VALUE,   4,  32), 
						new MinetestBiome("Coniferous forest",       BiomeColor.from( 25,  61,  33), (short)   5, Short.MAX_VALUE,  22,  71), 
						new MinetestBiome("Cold gravel desert",      BiomeColor.from(211, 211, 211), (short)  -6, Short.MAX_VALUE,  -2,   2), 
						new MinetestBiome("Gravel desert",           BiomeColor.from(131, 131, 131), (short)   2, Short.MAX_VALUE,  20,  -2), 
						new MinetestBiome("Dry tundra",              BiomeColor.from(216, 217, 241), (short)   2, Short.MAX_VALUE,   4,  12), 
						new MinetestBiome("Cold desert",             BiomeColor.from( 97,  94,  93), (short)   3, Short.MAX_VALUE,  32,  -3), 
						new MinetestBiome("Swamp",                   BiomeColor.from(153, 161,  31), (short)   1, Short.MAX_VALUE,  30, 114), 
						new MinetestBiome("Icy swamp",               BiomeColor.from(205, 206, 240), (short)   1, Short.MAX_VALUE, -10, 107), 
						new MinetestBiome("Stone grasslands",        BiomeColor.from( 89, 121,  63), (short)   4, Short.MAX_VALUE,  29,  22), 
						new MinetestBiome("Mixed forest",            BiomeColor.from( 75, 118,  82), (short)   5, Short.MAX_VALUE,  24,  56), 
						new MinetestBiome("Cold deciduous forest",   BiomeColor.from( 52, 111,  36), (short)   4, Short.MAX_VALUE,  31,  48), 
						new MinetestBiome("Deciduous forest",        BiomeColor.from( 28, 108,  29), (short)   5, Short.MAX_VALUE,  45,  65), 
						new MinetestBiome("Bushes",                  BiomeColor.from( 70, 129,  25), (short)   7, Short.MAX_VALUE,  43,  30), 
						new MinetestBiome("Scrub",                   BiomeColor.from(112, 139,  53), (short)   8, Short.MAX_VALUE,  57,  25), 
						new MinetestBiome("Hot pine forest",         BiomeColor.from( 15, 115,  13), (short)   6, Short.MAX_VALUE,  65,  35), 
						new MinetestBiome("Desert",                  BiomeColor.from(206, 199, 152), (short)   7, Short.MAX_VALUE,  89,   9), 
						new MinetestBiome("Sandstone grasslands",    BiomeColor.from(114, 149,  77), (short)   3, Short.MAX_VALUE,  55,  15), 
						new MinetestBiome("Savanna",                 BiomeColor.from(139, 142,  64), (short)   6, Short.MAX_VALUE,  80,  24), 
						new MinetestBiome("Desert stone grasslands", BiomeColor.from( 76, 129,  36), (short)   4, Short.MAX_VALUE,  90,  41), 
						new MinetestBiome("Red savanna",             BiomeColor.from(187, 148,  77), (short)   5, Short.MAX_VALUE,  90,  31), 
						new MinetestBiome("Semi-tropical forest",    BiomeColor.from(  0,  60,   0), (short)   4, Short.MAX_VALUE,  72,  61), 
						new MinetestBiome("Rainforest",              BiomeColor.from(  0,  46,   0), (short)   1, Short.MAX_VALUE,  89,  76), 
						new MinetestBiome("Sandstone desert",        BiomeColor.from(183, 186, 149), (short)   6, Short.MAX_VALUE,  60,   0), 
						new MinetestBiome("Orchard",                 BiomeColor.from( 12,  94,  11), (short)   6, Short.MAX_VALUE,  40,  40), 
						new MinetestBiome("Hot deciduous forest",    BiomeColor.from( 17,  82,  19), (short)   5, Short.MAX_VALUE,  61,  58), 
						new MinetestBiome("Hot swamp",               BiomeColor.from(123, 170,  33), (short)   1, Short.MAX_VALUE,  83, 116), 
 
						// Beach biomes 
						new MinetestBiome("Gravel beach",            BiomeColor.from(156, 154, 136), (short)  -7,       (short) 6,  19,  19), 
						new MinetestBiome("Sand dunes",              BiomeColor.from(203, 195, 119), (short)  -5,       (short) 5,  21,  61), 
						new MinetestBiome("Mangrove",                BiomeColor.from( 71,  79,  32), (short)  -4,       (short) 0,  96, 106), 
						new MinetestBiome("Desert dunes",            BiomeColor.from(225, 218, 172), (short)  -5,       (short) 6,  70,   6), 
						new MinetestBiome("Hot sand dunes",          BiomeColor.from(227, 217, 150), (short)  -5,       (short) 5, 106,  49), 
						new MinetestBiome("Tundra dunes",            BiomeColor.from(206, 204, 203), (short)  -5,       (short) 2,   5,  21), 
						                                                                                                                
						new MinetestBiome("Glacier 2 shore",         BiomeColor.from(210, 223, 247), (short)  -4,       (short) 0, -19,  40), 
						new MinetestBiome("Glacier 3 shore",         BiomeColor.from(219, 230, 253), (short)  -4,       (short) 0, -27,  80), 
						new MinetestBiome("Swamp shore",             BiomeColor.from( 87,  79,  36), (short)  -3,       (short) 0,  30, 114), 
						new MinetestBiome("Icy swamp shore",         BiomeColor.from(192, 188, 211), (short)  -4,       (short) 0, -10, 107), 
						new MinetestBiome("Hot swamp shore",         BiomeColor.from(126,  68,  52), (short)  -3,       (short) 0,  83, 116), 
						                                                                                                          
						// Sea biomes                                                                                             
						new MinetestBiome("Pack ice",                BiomeColor.from(220, 220, 255), (short)-112,       (short)-6, -24,  41), 
						new MinetestBiome("Cold sea",                BiomeColor.from(146, 158, 148), (short)-112,       (short)-6,  20,  47), 
						new MinetestBiome("Tempered sea",            BiomeColor.from(205, 212, 179), (short)-112,       (short)-6,  41,  48), 
						new MinetestBiome("Warm sea",                BiomeColor.from(206, 212, 160), (short)-112,       (short)-6,  89,  51), 

						// Underground (normally Amidst don't care, but here it's needed because ocean floor can drop lower than -112)						
						new MinetestBiome("Underground",             BiomeColor.from( 20,  20,  20), Short.MIN_VALUE, (short)-113, 50, 50) 
					}
				)
			);
			
			biomeLists.put(
				"Ethereal", // https://github.com/tenplus1/ethereal
				setIndexes(
					new MinetestBiome[] {
						new MinetestBiome("Glacier",         BiomeColor.from(233, 242, 255), (short)    -8, (short)31000,  0, 50),
						new MinetestBiome("Glacier ocean",   BiomeColor.from(222, 222, 255), (short)  -112, (short)   -9,  0, 50),
						new MinetestBiome("Mountain",        BiomeColor.from(243, 252, 255), (short)   140, (short)31000, 50, 50),
						new MinetestBiome("Desert",          BiomeColor.from(203, 163,  96), (short)     3, (short)   23, 35, 20),
						new MinetestBiome("Desert ocean",    BiomeColor.from(212, 200, 151), (short)  -192, (short)    3, 35, 20),
						new MinetestBiome("Clearing",        BiomeColor.from( 75, 120,  38), (short)     3, (short)   71, 45, 65),
						new MinetestBiome("Bamboo",          BiomeColor.from(133, 167,  80), (short)     3, (short)   71, 45, 75),
						new MinetestBiome("Bamboo ocean",    BiomeColor.from(199, 205, 155), (short)  -192, (short)    2, 45, 75),
						new MinetestBiome("Mesa",            BiomeColor.from(188, 149,  77), (short)     1, (short)   71, 25, 28),
						new MinetestBiome("Mesa ocean",      BiomeColor.from(208, 195, 145), (short)  -192, (short)    1, 25, 28),
						new MinetestBiome("Alpine",          BiomeColor.from(227, 221, 231), (short)    40, (short)  140, 10, 40),
						new MinetestBiome("Snowy",           BiomeColor.from(197, 221, 197), (short)     4, (short)   40, 10, 40),
						new MinetestBiome("Frost",           BiomeColor.from( 73, 144, 160), (short)     1, (short)   71, 10, 40),
						new MinetestBiome("Frost ocean",     BiomeColor.from(187, 201, 171), (short)  -192, (short)    1, 10, 40),
						new MinetestBiome("Grassy",          BiomeColor.from(130, 184, 111), (short)     3, (short)   91, 13, 40),
						new MinetestBiome("Grassy ocean",    BiomeColor.from(199, 209, 161), (short)-31000, (short)    3, 13, 40),
						new MinetestBiome("Caves",           BiomeColor.from(129,  79,  59), (short)     4, (short)   41, 15, 25),
						new MinetestBiome("Grayness",        BiomeColor.from( 94, 146, 116), (short)     2, (short)   41, 15, 30),
						new MinetestBiome("Grayness ocean",  BiomeColor.from(192, 192, 192), (short)  -192, (short)    1, 15, 30),
						new MinetestBiome("Grassytwo",       BiomeColor.from( 62, 146,  25), (short)     1, (short)   91, 15, 40),
						new MinetestBiome("Grassytwo ocean", BiomeColor.from(185, 201, 144), (short)  -192, (short)    1, 15, 40),
						new MinetestBiome("Prairie",         BiomeColor.from(118, 178,  53), (short)     3, (short)   26, 20, 40),
						new MinetestBiome("Prairie ocean",   BiomeColor.from(196, 208, 150), (short)  -192, (short)    1, 20, 40),
						new MinetestBiome("Jumble",          BiomeColor.from( 58,  93,  34), (short)     1, (short)   71, 25, 50),
						new MinetestBiome("Jumble ocean",    BiomeColor.from(156, 169, 121), (short)  -192, (short)    1, 25, 50),
						new MinetestBiome("Junglee",         BiomeColor.from( 27,  54,  19), (short)     1, (short)   71, 30, 60),
						new MinetestBiome("Junglee ocean",   BiomeColor.from(188, 195, 151), (short)  -192, (short)    1, 30, 60),
						new MinetestBiome("Grove",           BiomeColor.from( 42, 132,  18), (short)     3, (short)   23, 45, 35),
						new MinetestBiome("Grove ocean",     BiomeColor.from(181, 198, 143), (short)  -192, (short)    2, 45, 35),
						new MinetestBiome("Mushroom",        BiomeColor.from(164,  58,  76), (short)     3, (short)   50, 45, 55),
						new MinetestBiome("Mushroom ocean",  BiomeColor.from(206, 184, 154), (short)  -192, (short)    2, 45, 55),
						new MinetestBiome("Sandstone",       BiomeColor.from(195, 190, 140), (short)     3, (short)   23, 50, 20),
						new MinetestBiome("Sandstone ocean", BiomeColor.from(210, 208, 164), (short)  -192, (short)    2, 50, 20),
						new MinetestBiome("Quicksand",       BiomeColor.from(190, 186, 138), (short)     1, (short)    1, 50, 38),
						new MinetestBiome("Plains",          BiomeColor.from(127,  96,  53), (short)     3, (short)   25, 65, 25),
						new MinetestBiome("Plains ocean",    BiomeColor.from(191, 184, 146), (short)  -192, (short)    2, 55, 25),
						new MinetestBiome("Savannah",        BiomeColor.from(139, 142,  64), (short)     3, (short)   50, 55, 25),
						new MinetestBiome("Savannah ocean",  BiomeColor.from(201, 200, 152), (short)  -192, (short)    1, 55, 25),
						new MinetestBiome("Fiery",           BiomeColor.from(251, 108,   9), (short)     5, (short)   20, 75, 10),
						new MinetestBiome("Fiery ocean",     BiomeColor.from(223, 194, 141), (short)  -192, (short)    4, 75, 10),
						new MinetestBiome("Sandclay",        BiomeColor.from(190, 188, 162), (short)     1, (short)   11, 65,  2),
						new MinetestBiome("Swamp",           BiomeColor.from(126, 161,  20), (short)     1, (short)    7, 80, 90),
						new MinetestBiome("Swamp ocean",     BiomeColor.from(182, 194, 115), (short)  -192, (short)    1, 80, 90),
						
						// Underground (Amidst don't care) 
						// new MinetestBiome("underground",     BiomeColor.from(20, 20, 20), (short)-31000, (short) -192, 50, 50),						
					}
				)
			);
			
			biomeLists.put(
				"Voxelgarden",
				setIndexes(
					new MinetestBiome[] {
						new MinetestBiome("Sea dirt",                BiomeColor.from(106,  95,  61), (short)-32000,        (short)0,  30,  40),	
						new MinetestBiome("Sea sand",                BiomeColor.from(211, 199, 148), (short)-32000,        (short)5,  90,  40),	
						new MinetestBiome("Sea desert sand",         BiomeColor.from(203, 175, 119), (short)-32000,        (short)5, 110, -60),	
						new MinetestBiome("Sea gravel",              BiomeColor.from( 93,  93,  93), (short)-32000,        (short)5,  30, -60),	

						new MinetestBiome("Conifer",                 BiomeColor.from( 25,  61,  33), (short)     1, Short.MAX_VALUE,   0,  70),	
						new MinetestBiome("Conifer-Tree transition", BiomeColor.from( 20,  75,  23), (short)     1, Short.MAX_VALUE,  25,  75),
						new MinetestBiome("Tree",                    BiomeColor.from( 15, 115,  13), (short)     1, Short.MAX_VALUE,  50,  80),	
						new MinetestBiome("Tree-Jungle transition",  BiomeColor.from(  7,  76,   7), (short)     1, Short.MAX_VALUE,  90,  75),
						new MinetestBiome("Jungle",                  BiomeColor.from(  0,  46,   0), (short)     1, Short.MAX_VALUE, 100,  70),	
						new MinetestBiome("Desert",                  BiomeColor.from(205, 180, 125), (short)     1, Short.MAX_VALUE, 120, -45),	
						new MinetestBiome("Glacier",                 BiomeColor.from(237, 238, 255), (short)     1, Short.MAX_VALUE, -20, -30),	
						new MinetestBiome("Gravel ice",              BiomeColor.from(213, 221, 240), (short)     1, Short.MAX_VALUE, -40, -40),															
						new MinetestBiome("Gravel desert",           BiomeColor.from( 93,  93,  93), (short)     1, Short.MAX_VALUE, 160, -20),	
					}
				)
			);

			biomeLists.put(
				"Australia v0.4", // https://github.com/vlapsley/australia
				setIndexes(
					new MinetestBiome[] {
						new MinetestBiome("Arnhem land",            BiomeColor.from( 90, 140,  60), (short)      4,(short)    35, 60, 80),
						new MinetestBiome("Australian alps",        BiomeColor.from(224, 225, 238), (short)    150,(short) 31000, 10, 50),
						new MinetestBiome("Central australia",      BiomeColor.from(183,  62,  34), (short)     36,(short) 31000, 80, 25),
						new MinetestBiome("Eastern coasts",         BiomeColor.from( 97, 120,  43), (short)      4,(short)    35, 35, 60),
						new MinetestBiome("Far north queensland",   BiomeColor.from( 47, 132,  75), (short)      4,(short)    35, 75, 90),
						new MinetestBiome("Flinders lofty",         BiomeColor.from(183, 189,  93), (short)     36,(short) 31000, 50, 50),
						new MinetestBiome("Goldfields esperence",   BiomeColor.from(204, 187, 106), (short)      4,(short)    35, 60, 20),
						new MinetestBiome("Great australian bight", BiomeColor.from(193, 188, 125), (short)    -64,(short)     3, 40, 50),
						new MinetestBiome("Great barrier reef",     BiomeColor.from(214, 207, 158), (short)    -64,(short)     3, 75, 75),
						new MinetestBiome("Great dividing range",   BiomeColor.from( 63, 120,  43), (short)     36,(short) 31000, 25, 65),
						new MinetestBiome("Gulf of carpentaria",    BiomeColor.from(200, 168,  70), (short)      4,(short)    35, 75, 55),
						new MinetestBiome("Indian ocean",           BiomeColor.from(204, 193, 116), (short)    -64,(short)     3, 60, 50),
						new MinetestBiome("Jarrah karri forests",   BiomeColor.from(137, 160,  30), (short)      4,(short)    35, 20, 50),
						new MinetestBiome("Kimberley",              BiomeColor.from(204,  80,  13), (short)      4,(short)    35, 80, 75),
						new MinetestBiome("Mangroves",              BiomeColor.from(122, 142,  92), (short)     -2,(short)     3, 80, 80),
						new MinetestBiome("Mulga lands",            BiomeColor.from(200, 146,  70), (short)     36,(short) 31000, 75, 45),
						new MinetestBiome("Murray darling basin",   BiomeColor.from(180, 166, 102), (short)     36,(short) 31000, 60, 40),
						new MinetestBiome("Pilbara",                BiomeColor.from(205, 111,  48), (short)      4,(short)    35, 80, 20),
						new MinetestBiome("Simpson desert",         BiomeColor.from(163,  42,  13), (short)     36,(short) 31000, 85, 10),
						new MinetestBiome("Tasmania",               BiomeColor.from( 36,  97,  28), (short)     36,(short) 31000, 15, 85),
						new MinetestBiome("Tasman sea",             BiomeColor.from(197, 196, 146), (short)    -64,(short)     3, 20, 50),
						new MinetestBiome("Timor sea",              BiomeColor.from(211, 214, 158), (short)    -64,(short)     3, 80, 90),
						new MinetestBiome("Underground",            BiomeColor.from( 20,  20,  20), (short) -31000,(short)   -65, 50, 50),
						new MinetestBiome("Victorian forests",      BiomeColor.from(61,  148,  38), (short)     36,(short) 31000, 40, 50),					}
				)
			);			
			
			biomeLists.put("MineClone 2", setIndexes(MineCloneBiomes.getBiomeList()));			
			biomeLists.put("Aotearoa (New Zealand) " + AotearoaBiomes.getVersion(), setIndexes(AotearoaBiomes.getBiomeList()));						
		}
		return biomeLists;
	}
	
	static MinetestBiome[] setIndexes(MinetestBiome[] array) {
		// Set correct indexes in case any part of the rest of the application needs them
		for(int i = array.length - 1; i >= 0; i--) array[i].setIndex(i);
		return array;		
	}
}
