package amidst.mojangapi.world.versionfeatures;

import static amidst.mojangapi.world.biome.BiomeType.BEACH;
import static amidst.mojangapi.world.biome.BiomeType.BEACH_CLIFFS;
import static amidst.mojangapi.world.biome.BiomeType.DEEP_OCEAN;
import static amidst.mojangapi.world.biome.BiomeType.HILLS;
import static amidst.mojangapi.world.biome.BiomeType.ISLAND;
import static amidst.mojangapi.world.biome.BiomeType.MOUNTAINS;
import static amidst.mojangapi.world.biome.BiomeType.OCEAN;
import static amidst.mojangapi.world.biome.BiomeType.PLAINS;
import static amidst.mojangapi.world.biome.BiomeType.PLAINS_FLAT;
import static amidst.mojangapi.world.biome.BiomeType.PLAINS_TAIGA;
import static amidst.mojangapi.world.biome.BiomeType.PLATEAU;
import static amidst.mojangapi.world.biome.BiomeType.RIVER;
import static amidst.mojangapi.world.biome.BiomeType.SWAMPLAND;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeList;

public enum DefaultBiomes {
	;

	// @formatter:off
	public static final int ocean                = 0;
	public static final int plains               = 1;
	public static final int desert               = 2;
	public static final int extremeHills         = 3;
	public static final int forest               = 4;
	public static final int taiga                = 5;
	public static final int swampland            = 6;
	public static final int river                = 7;
	public static final int hell                 = 8;
	public static final int theEnd               = 9;
	public static final int frozenOcean          = 10;
	public static final int frozenRiver          = 11;
	public static final int icePlains            = 12;
	public static final int iceMountains         = 13;
	public static final int mushroomIsland       = 14;
	public static final int mushroomIslandShore  = 15;
	public static final int beach                = 16;
	public static final int desertHills          = 17;
	public static final int forestHills          = 18;
	public static final int taigaHills           = 19;
	public static final int extremeHillsEdge     = 20;
	public static final int jungle               = 21;
	public static final int jungleHills          = 22;
	public static final int jungleEdge           = 23;
	public static final int deepOcean            = 24;
	public static final int stoneBeach           = 25;
	public static final int coldBeach            = 26;
	public static final int birchForest          = 27;
	public static final int birchForestHills     = 28;
	public static final int roofedForest         = 29;
	public static final int coldTaiga            = 30;
	public static final int coldTaigaHills       = 31;
	public static final int megaTaiga            = 32;
	public static final int megaTaigaHills       = 33;
	public static final int extremeHillsPlus     = 34;
	public static final int savanna              = 35;
	public static final int savannaPlateau       = 36;
	public static final int mesa                 = 37;
	public static final int mesaPlateauF         = 38;
	public static final int mesaPlateau          = 39;

	public static final int theEndLow            = 40;
	public static final int theEndMedium         = 41;
	public static final int theEndHigh           = 42;
	public static final int theEndBarren         = 43;

	public static final int warmOcean            = 44;
	public static final int lukewarmOcean        = 45;
	public static final int coldOcean            = 46;
	public static final int warmDeepOcean        = 47;
	public static final int lukewarmDeepOcean    = 48;
	public static final int coldDeepOcean        = 49;
	public static final int frozenDeepOcean      = 50;

	public static final int theVoid              = 127;

	public static final int sunflowerPlains      = plains + Biome.SPECIAL_BIOMES_START;
	public static final int desertM              = desert + Biome.SPECIAL_BIOMES_START;
	public static final int extremeHillsM        = extremeHills + Biome.SPECIAL_BIOMES_START;
	public static final int flowerForest         = forest + Biome.SPECIAL_BIOMES_START;
	public static final int taigaM               = taiga + Biome.SPECIAL_BIOMES_START;
	public static final int swamplandM           = swampland + Biome.SPECIAL_BIOMES_START;
	public static final int icePlainsSpikes      = icePlains + Biome.SPECIAL_BIOMES_START;
	public static final int jungleM              = jungle + Biome.SPECIAL_BIOMES_START;
	public static final int jungleEdgeM          = jungleEdge + Biome.SPECIAL_BIOMES_START;
	public static final int birchForestM         = birchForest + Biome.SPECIAL_BIOMES_START;
	public static final int birchForestHillsM    = birchForestHills + Biome.SPECIAL_BIOMES_START;
	public static final int roofedForestM        = roofedForest + Biome.SPECIAL_BIOMES_START;
	public static final int coldTaigaM           = coldTaiga + Biome.SPECIAL_BIOMES_START;
	public static final int megaSpruceTaiga      = megaTaiga + Biome.SPECIAL_BIOMES_START;
	public static final int megaSpruceTaigaHills = megaTaigaHills + Biome.SPECIAL_BIOMES_START;
	public static final int extremeHillsPlusM    = extremeHillsPlus + Biome.SPECIAL_BIOMES_START;
	public static final int savannaM             = savanna + Biome.SPECIAL_BIOMES_START;
	public static final int savannaPlateauM      = savannaPlateau + Biome.SPECIAL_BIOMES_START;
	public static final int mesaBryce            = mesa + Biome.SPECIAL_BIOMES_START;
	public static final int mesaPlateauFM        = mesaPlateauF + Biome.SPECIAL_BIOMES_START;
	public static final int mesaPlateauM         = mesaPlateau + Biome.SPECIAL_BIOMES_START;

	public static final int bambooJungle         = 168;
	public static final int bambooJungleHills    = 169;
	public static final int soulSandValley       = 170;
	public static final int crimsonForest        = 171;
	public static final int warpedForest         = 172;
	public static final int basaltDeltas         = 173;

	public static final VersionFeature<BiomeList> DEFAULT_BIOMES = VersionFeature.<Biome> listBuilder()
		.init( // Starts at beta 1.8
			new Biome(0, "Ocean", OCEAN),
			new Biome(1, "Plains", PLAINS),
			new Biome(2, "Desert", PLAINS_FLAT),
			new Biome(3, "Extreme Hills", MOUNTAINS),
			new Biome(4, "Forest", PLAINS),
			new Biome(5, "Taiga", PLAINS_TAIGA),
			new Biome(6, "Swampland", SWAMPLAND),
			new Biome(7, "River", RIVER),
			new Biome(8, "Hell", PLAINS),
			new Biome(9, "Sky", PLAINS)
		).sinceExtend(RecognisedVersion._b1_9_pre1,
			new Biome(10, "Frozen Ocean", OCEAN),
			new Biome(11, "Frozen River", RIVER),
			new Biome(12, "Ice Plains", PLAINS_FLAT),
			new Biome(13, "Ice Mountains", HILLS), // There was a bug causing this biome to not be generated until the next version
			new Biome(14, "Mushroom Island", ISLAND),
			new Biome(15, "Mushroom Island Shore", BEACH)
		).sinceExtend(RecognisedVersion._12w01a,
			new Biome(16, "Beach", BEACH),
			new Biome(17, "Desert Hills", HILLS),
			new Biome(18, "Forest Hills", HILLS),
			new Biome(19, "Taiga Hills", HILLS),
			new Biome(20, "Extreme Hills Edge", MOUNTAINS.weaken())
		).sinceExtend(RecognisedVersion._12w03a,
			new Biome(21, "Jungle", PLAINS),
			new Biome(22, "Jungle Hills", HILLS)
		).sinceExtend(RecognisedVersion._13w36a,
			new Biome(23, "Jungle Edge", PLAINS),
			new Biome(24, "Deep Ocean", DEEP_OCEAN),
			new Biome(25, "Stone Beach", BEACH_CLIFFS),
			new Biome(26, "Cold Beach", BEACH),
			new Biome(27, "Birch Forest", PLAINS),
			new Biome(28, "Birch Forest Hills", HILLS),
			new Biome(29, "Roofed Forest", PLAINS),
			new Biome(30, "Cold Taiga", PLAINS_TAIGA),
			new Biome(31, "Cold Taiga Hills", HILLS),
			new Biome(32, "Mega Taiga", PLAINS_TAIGA),
			new Biome(33, "Mega Taiga Hills", HILLS),
			new Biome(34, "Extreme Hills+", MOUNTAINS),
			new Biome(35, "Savanna", PLAINS_FLAT),
			new Biome(36, "Savanna Plateau", PLATEAU),
			new Biome(37, "Mesa", PLAINS),
			new Biome(38, "Mesa Plateau F", PLATEAU),
			new Biome(39, "Mesa Plateau", PLATEAU),
			// All of the modified biomes in this version just had an M after their original biome name
			new Biome("Plains M", 1, PLAINS),
			new Biome("Desert M", 2, PLAINS_FLAT),
			new Biome("Extreme Hills M", 3, MOUNTAINS),
			new Biome("Forest M", 4, PLAINS),
			new Biome("Taiga M", 5, PLAINS_TAIGA),
			new Biome("Swampland M", 6, SWAMPLAND),
			new Biome("Ice Plains M", 12, PLAINS_FLAT),
			new Biome("Jungle M", 21, PLAINS),
			new Biome("Jungle Edge M", 23, PLAINS),
			new Biome("Birch Forest M", 27, PLAINS),
			new Biome("Birch Forest Hills M", 28, HILLS),
			new Biome("Roofed Forest M", 29, PLAINS),
			new Biome("Cold Taiga M", 30, PLAINS_TAIGA),
			new Biome("Mega Taiga M", 32, PLAINS_TAIGA),
			new Biome(161, "Mega Taiga Hills M", PLAINS_TAIGA.strengthen(), true),
			new Biome("Extreme Hills+ M", 34, MOUNTAINS),
			new Biome("Savanna M", 35, PLAINS_FLAT),
			new Biome("Savanna Plateau M", 36, PLATEAU),
			new Biome("Mesa M", 37, PLAINS),
			new Biome("Mesa Plateau F M", 38, PLATEAU),
			new Biome("Mesa Plateau M", 39, PLATEAU)
		).sinceExtend(RecognisedVersion._14w02a, // Need confirmation on version; this was changed sometime after 1.7.10 and before 1.8.8
			new Biome(161,"Redwood Taiga Hills M", PLAINS_TAIGA.strengthen(), true)
		).sinceExtend(RecognisedVersion._14w17a,
			new Biome(9,  "The End", PLAINS)
		).sinceExtend(RecognisedVersion._15w31c, // Need confirmation on this version. Was after 1.8.8 and before 1.9.4
			new Biome("Sunflower Plains", 1, PLAINS),
			new Biome("Flower Forest", 4, PLAINS),
			new Biome("Ice Plains Spikes", 12, PLAINS_FLAT),
			new Biome("Mega Spruce Taiga", 32, PLAINS_TAIGA),
			new Biome("Mesa (Bryce)", 37, PLAINS)
		).sinceExtend(RecognisedVersion._15w40b, // Is actually 15w37a, but magic strings are identical
			new Biome(127,"The Void", PLAINS)
		).sinceExtend(RecognisedVersion._18w06a,
			new Biome(40, "The End - Floating Island", PLAINS),
			new Biome(41, "The End - Medium Island", PLAINS),
			new Biome(42, "The End - High Island", PLAINS),
			new Biome(43, "The End - Barren Island", PLAINS)
		).sinceExtend(RecognisedVersion._18w08a,
			new Biome(44, "Warm Ocean", OCEAN),
			new Biome(45, "Lukewarm Ocean", OCEAN),
			new Biome(46, "Cold Ocean", OCEAN),
			new Biome(47, "Warm Deep Ocean", OCEAN),
			new Biome(48, "Lukewarm Deep Ocean", OCEAN),
			new Biome(49, "Cold Deep Ocean", OCEAN),
			new Biome(50, "Frozen Deep Ocean", OCEAN)
		).sinceExtend(RecognisedVersion._18w16a,
			new Biome(8, "Nether", PLAINS),
			new Biome(38, "Mesa Forest Plateu", PLATEAU),
			new Biome("Mutated Desert", 2, PLAINS_FLAT),
			new Biome("Mutated Extreme Hills", 3, MOUNTAINS),
			new Biome("Mutated Taiga", 5, PLAINS_TAIGA),
			new Biome("Mutated Swampland", 6, SWAMPLAND),
			new Biome("Mutated Jungle", 21, PLAINS),
			new Biome("Mutated Jungle Edge", 23, PLAINS),
			new Biome("Mutated Birch Forest", 27, PLAINS),
			new Biome("Mutated Birch Forest Hills", 28, HILLS),
			new Biome("Mutated Roofed Forest", 29, PLAINS),
			new Biome("Mutated Cold Taiga", 30, PLAINS_TAIGA),
			new Biome("Mutated Extreme Hills+", 34, MOUNTAINS),
			new Biome("Mutated Savanna", 35, PLAINS_FLAT),
			new Biome("Mutated Savanna Plateau", 36, PLATEAU),
			new Biome("Mutated Mesa Forest Plateau", 38, PLATEAU),
			new Biome("Mutated Mesa Plateau", 39, PLATEAU)
		).sinceExtend(RecognisedVersion._18w19a,
			new Biome(3, "Mountains", MOUNTAINS),
			new Biome(6, "Swamp", SWAMPLAND),
			new Biome(12, "Snowy Tundra", PLAINS_FLAT),
			new Biome(13, "Snowy Mountains", HILLS),
			new Biome(14, "Mushroom Fields", ISLAND),
			new Biome(15, "Mushroom Field Shore", BEACH),
			new Biome(18, "Wooded Hills", HILLS),
			new Biome(20, "Mountain Edge", MOUNTAINS.weaken()),
			new Biome(25, "Stone Shore", BEACH_CLIFFS),
			new Biome(26, "Snowy Beach", BEACH),
			new Biome(29, "Dark Forest", PLAINS),
			new Biome(30, "Snowy Taiga", PLAINS_TAIGA),
			new Biome(31, "Snowy Taiga Hills", HILLS),
			new Biome(32, "Giant Tree Taiga", PLAINS_TAIGA),
			new Biome(33, "Giant Tree Taiga Hills", HILLS),
			new Biome(34, "Wooded Mountains", MOUNTAINS),
			new Biome(37, "Badlands", PLAINS),
			new Biome(38, "Wooded Badlands Plateau", PLATEAU),
			new Biome(39, "Badlands Plateau", PLATEAU),
			new Biome(40, "Small End Islands", PLAINS),
			new Biome(41, "End Midlands", PLAINS),
			new Biome(42, "End Highlands", PLAINS),
			new Biome(43, "End Barrens", PLAINS),
			new Biome(47, "Deep Warm Ocean", OCEAN),
			new Biome(48, "Deep Lukewarm Ocean", OCEAN),
			new Biome(49, "Deep Cold Ocean", OCEAN),
			new Biome(50, "Deep Frozen Ocean", OCEAN),
			new Biome("Desert Lakes", 2, PLAINS_FLAT),
			new Biome("Gravelly Mountains", 3, MOUNTAINS),
			new Biome("Taiga Mountains", 5, PLAINS_TAIGA),
			new Biome("Swamp Hills", 6, SWAMPLAND),
			new Biome("Ice Spikes", 12, PLAINS_FLAT),
			new Biome("Modified Jungle", 21, PLAINS),
			new Biome("Modified Jungle Edge", 23, PLAINS),
			new Biome("Tall Birch Forest", 27, PLAINS),
			new Biome("Tall Birch Hills", 28, HILLS),
			new Biome("Dark Forest Hills", 29, PLAINS),
			new Biome("Snowy Taiga Mountains", 30, PLAINS_TAIGA),
			new Biome("Giant Spruce Taiga", 32, PLAINS_TAIGA),
			new Biome(161, "Giant Spruce Taiga Hills", PLAINS_TAIGA, true), // Don't strengthen this in newer versions (might be wrong here)
			new Biome("Gravelly Mountains+", 34, MOUNTAINS),
			new Biome("Shattered Savanna", 35, PLAINS_FLAT),
			new Biome("Shattered Savanna Plateau", 36, PLATEAU),
			new Biome("Eroded Badlands", 37, PLAINS),
			new Biome("Modified Wooded Badlands Plateau", 38, PLATEAU),
			new Biome("Modified Badlands Plateau", 39, PLATEAU)
		).sinceExtend(RecognisedVersion._18w43a,
			new Biome(168, "Bamboo Jungle", PLAINS),
			new Biome(169, "Bamboo Jungle Hills", HILLS)
		).sinceExtend(RecognisedVersion._20w06a,
			new Biome(8, "Nether Wastes", PLAINS),
			new Biome(170, "Soul Sand Valley", PLAINS),
			new Biome(171, "Crimson Forest", PLAINS),
			new Biome(172, "Warped Forest", PLAINS)
		).sinceExtend(RecognisedVersion._20w15a,
			new Biome(173, "Basalt Deltas", PLAINS)
		).construct().andThen(BiomeList::new);
		// @formatter:on
}
