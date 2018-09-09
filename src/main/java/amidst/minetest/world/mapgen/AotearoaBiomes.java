package amidst.minetest.world.mapgen;

import java.util.ArrayList;
import java.util.Arrays;

import amidst.mojangapi.world.biome.BiomeColor;

/**
 * Generates a list of all the biomes for Aotearoa
 * https://github.com/DokimiCU/aotearoa 
 */

public class AotearoaBiomes {

	private static final short OVERWORLD_MIN  = -31000;
	private static final short OVERWORLD_MAX  =  31000;
	
	// ocean
	private static final short ocean_min = -150;
	private static final short ocean_max = -21;

	// rocks
	// private static final short basement_min = -10000;
	// private static final short basement_max = ocean_min - 1;

	// beaches
	private static final short beach_max = 2;
	private static final short beach_min = ocean_max + 1;
	// dunes
	private static final short dune_max = 5;
	private static final short dune_min = beach_max + 1;
	// coastal forest etc
	private static final short coastf_max = 14;
	private static final short coastf_min = dune_max + 1;
	// lowland forest etc
	private static final short lowf_max = 80;
	private static final short lowf_min = coastf_max + 1;
	// highland forest etc
	private static final short highf_max = 120;
	private static final short highf_min = lowf_max + 1;
	// alpine
	private static final short alp_max = 140;
	private static final short alp_min = highf_max + 1;
	// high alpine
	private static final short high_alp_max = 31000;
	private static final short high_alp_min = alp_max + 1;


	// // // // // // 
	// temp and humidity
	//  many here for re-use and access!

	// Shorelines...(from beach to dunes)
	// Normal sandy
	private static final short sandy_temp = 50;
	private static final short sandy_hum = 50;
	// shelly beach
	private static final short shelly_temp = 80;
	private static final short shelly_hum = 40;
	// gravel beaches
	private static final short gravel_temp = 30;
	private static final short gravel_hum = 10;
	// subantarctic_shore (bare rock)
	private static final short subantartic_temp = 0;
	private static final short subantartic_hum = 130;
	// volcanic_isle_shore (bare rock and volcanic)
	private static final short volcanic_isle_temp = 120;
	private static final short volcanic_isle_hum = 0;
	// iron sands
	private static final short iron_temp = 50;
	private static final short iron_hum = 90;
	// estuary
	private static final short estuary_temp = 90;
	private static final short estuary_hum = 80;

	// Coastal forest etc...
	// (i.e warm, subtropical ..also Kauri, mangroves t,)
	private static final short pohutukawa_temp = 90;
	private static final short pohutukawa_hum = 50;
	// coastal scrub
	private static final short c_scrub_temp = 50;
	private static final short c_scrub_hum = 50;
	// muttonbird_scrub
	private static final short muttonbird_temp = 20;
	private static final short muttonbird_hum = 70;
	// tussock (also higher alt tussock)
	private static final short tussock_temp = 15;
	private static final short tussock_hum = 5;
	// kahikatea_swamp
	private static final short kahi_swamp_temp = 30;
	private static final short kahi_swamp_hum = 100;
	// raupo_swamp
	private static final short raupo_swamp_temp = 75;
	private static final short raupo_swamp_hum = 100;

	// lowland forests etc...
	// kauri uses pohutukawa
	// northern podocarp
	private static final short npodo_temp = 60;
	private static final short npodo_hum = 55;
	// southern podocarp
	private static final short spodo_temp = 25;
	private static final short spodo_hum = 45;
	// kamahi
	private static final short kamahi_temp = 25;
	private static final short kamahi_hum = 100;
	// peat_bog
	private static final short peat_temp = 60;
	private static final short peat_hum = 125;
	// Shrublands...
	// gumland
	private static final short gumland_temp = 100;
	private static final short gumland_hum = 0;
	// fernland
	private static final short fernland_temp = 50;
	private static final short fernland_hum = 0;
	// matagouri_scrub..use tussock

	// Highlands...
	// pahautea forest
	private static final short pahautea_temp = 90;
	private static final short pahautea_hum = 70;
	// rangipo_desert
	private static final short rangipo_temp = 90;
	private static final short rangipo_hum = 0;
	// mt_beech
	private static final short mt_beech_temp = 40;
	private static final short mt_beech_hum = 70;
	// mountain_tussock
	private static final short mt_tussock_temp = 30;
	private static final short mt_tussock_hum = 30;
	// scree
	private static final short scree_temp = 10;
	private static final short scree_hum = 0;
	//  alpine peat bog
	private static final short alpine_peat_temp = 50;
	private static final short alpine_peat_hum = 100;
	
	public static String getVersion() {
		return "v0.1";
	}
	
	public static MinetestBiome[] getBiomeList() {
		
		ArrayList<MinetestBiome> biomes = new ArrayList<MinetestBiome>(
				Arrays.asList(		
					new MinetestBiome("underground",              BiomeColor.from( 20,  20,  20), (short) OVERWORLD_MIN,        (short) -113,            50, 50),
					//new MinetestBiome("greywacke basement",       BiomeColor.error(),             (short) basement_min,         (short) basement_max,    60, 40),
					new MinetestBiome("ocean",                    BiomeColor.from( 10,   5,  60), (short) -150,                 (short) beach_min,       50, 50),
					new MinetestBiome("Sandy beach",              BiomeColor.from(214, 207, 158), (short) beach_min,            (short) beach_max,       sandy_temp, sandy_hum),
					new MinetestBiome("Shelly beach",             BiomeColor.from(214, 217, 200), (short) -5,                   (short) beach_max,       shelly_temp, shelly_hum),
					new MinetestBiome("Gravel beach",             BiomeColor.from(131, 131, 131), (short) beach_min,            (short) beach_max,       gravel_temp, gravel_hum),
					new MinetestBiome("Subantarctic shore",       BiomeColor.from( 97,  94,  93), (short)(beach_min / 2),       (short) beach_max,       subantartic_temp, subantartic_hum),
					new MinetestBiome("Volcanic isle shore",      BiomeColor.from( 20,  22,  25), (short)(beach_min / 2),       (short)(beach_max + 3),  volcanic_isle_temp, volcanic_isle_hum),
					new MinetestBiome("Iron sand beach",          BiomeColor.from( 35,  35,  35), (short) beach_min,            (short) beach_max,       iron_temp, iron_hum),
					new MinetestBiome("Northern estuary",         BiomeColor.from( 60,  45,  30), (short) beach_min,            (short) beach_max,       estuary_temp, estuary_hum),
					new MinetestBiome("Pohutukawa dunes",         BiomeColor.from(234, 197, 158), (short) dune_min,             (short) dune_max,        pohutukawa_temp, pohutukawa_hum),
					new MinetestBiome("Sand dunes",               BiomeColor.from(210, 210, 118), (short) dune_min,             (short) dune_max,        sandy_temp, sandy_hum),
					new MinetestBiome("Iron sand dunes",          BiomeColor.from( 35,  48,  35), (short) dune_min,             (short) dune_max,        iron_temp, iron_hum),
					new MinetestBiome("Gravel dunes",             BiomeColor.from(108, 122,  94), (short) dune_min,             (short) dune_max,        gravel_temp, gravel_hum),
					new MinetestBiome("Subantarctic coast",       BiomeColor.from(140, 175,  33), (short) dune_min,             (short)(dune_max + 1),   subantartic_temp, subantartic_hum),
					new MinetestBiome("White island",             BiomeColor.from( 25,  27,  30), (short) dune_min,             (short)(dune_max + 1),   volcanic_isle_temp, volcanic_isle_hum),
					new MinetestBiome("Pohutukawa forest",        BiomeColor.from(170,  80,  40), (short) coastf_min,           (short) coastf_max,      pohutukawa_temp, pohutukawa_hum),
					new MinetestBiome("Coastal scrub",            BiomeColor.from( 77, 142,  57), (short) coastf_min,           (short) coastf_max,      c_scrub_temp, c_scrub_hum),
					new MinetestBiome("Muttonbird scrub",         BiomeColor.from(170, 180, 130), (short) coastf_min,           (short) coastf_max,      muttonbird_temp, muttonbird_hum),
					new MinetestBiome("Coastal tussock",          BiomeColor.from(200, 168,  70), (short) coastf_min,           (short) coastf_max,      tussock_temp, tussock_hum),
					new MinetestBiome("Kahikatea swamp",          BiomeColor.from(102, 103,  68), (short)(coastf_min + 2),      (short) 25,              kahi_swamp_temp, kahi_swamp_hum),
					new MinetestBiome("Kauri forest",             BiomeColor.from(108, 140,  61), (short) lowf_min,             (short) lowf_max,        pohutukawa_temp, pohutukawa_hum),
					new MinetestBiome("Northern podocarp forest", BiomeColor.from(137, 153,  83), (short) lowf_min,             (short) lowf_max,        npodo_temp, npodo_hum),
					new MinetestBiome("Southern podocarp forest", BiomeColor.from(110, 124,  60), (short) lowf_min,             (short) lowf_max,        spodo_temp, spodo_hum),
					new MinetestBiome("Kamahi forest",            BiomeColor.from( 93,  65,  30), (short)(lowf_max / 2),        (short) lowf_max,        kamahi_temp, kamahi_hum),
					new MinetestBiome("Peat bog",                 BiomeColor.from(158, 123,  61), (short) lowf_min,             (short) 40,              peat_temp, peat_hum),
					new MinetestBiome("Raupo swamp",              BiomeColor.from(136, 116,  80), (short)(lowf_min + 1),        (short)(lowf_min + 1),   raupo_swamp_temp, raupo_swamp_hum),
					new MinetestBiome("Gumland",                  BiomeColor.from(162, 139,  70), (short) lowf_min,             (short) lowf_max,        gumland_temp, gumland_hum),
					new MinetestBiome("Fernland",                 BiomeColor.from(112,  96,  42), (short) lowf_min,             (short) lowf_max,        fernland_temp, fernland_hum),
					new MinetestBiome("Matagouri scrub",          BiomeColor.from(168, 149,  91), (short) lowf_min,             (short) lowf_max,        tussock_temp, tussock_hum),
					new MinetestBiome("Pahautea forest",          BiomeColor.from( 97, 139,  94), (short) highf_min,            (short) highf_max,       pahautea_temp, pahautea_hum),
					new MinetestBiome("Rangipo desert",           BiomeColor.from(132, 121,  82), (short)(highf_min + 10),      (short) highf_max,       rangipo_temp, rangipo_hum),
					new MinetestBiome("Mountain beech forest",    BiomeColor.from(127, 162, 137), (short) highf_min,            (short) highf_max,       mt_beech_temp, mt_beech_hum),
					new MinetestBiome("Mountain tussock",         BiomeColor.from(212, 190, 123), (short) highf_min,            (short) highf_max,       mt_tussock_temp, mt_tussock_hum),
					new MinetestBiome("Alpine peat bog",          BiomeColor.from(128, 100,  50), (short) highf_min,            (short) highf_max,       alpine_peat_temp, alpine_peat_hum),
					new MinetestBiome("Scree",                    BiomeColor.from(137, 137, 137), (short) highf_min,            (short) alp_max,         scree_temp, scree_hum),
					new MinetestBiome("Fellfield",                BiomeColor.from(193, 194, 206), (short) alp_min,              (short) alp_max,         50, 50),
					new MinetestBiome("Alpine snow",              BiomeColor.from(214, 215, 228), (short) high_alp_min,         (short) high_alp_max,    50, 50),
					new MinetestBiome("Glacier",                  BiomeColor.from(224, 225, 248), (short)(alp_min + 4),         (short) high_alp_max,    0,  100),
					new MinetestBiome("Volcano",                  BiomeColor.from(238, 227, 234), (short) alp_min,              (short) high_alp_max,    100, 0)
				)
			);
				
		return biomes.toArray(new MinetestBiome[biomes.size()]);		
	}	
}
