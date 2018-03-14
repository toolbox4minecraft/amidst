package amidst.minetest.world.mapgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.settings.biomeprofile.BiomeProfile;

/**
 * Minetest-specific BiomeProfile, for the biome details Minetest needs
 */
@Immutable
public class MinetestBiomeProfileImpl implements BiomeProfile{
	private static List<MinetestBiome> createDefaultColorMap() {
		List<MinetestBiome> result = new ArrayList<MinetestBiome>();
		for (MinetestBiome biome : DefaultBiomes_v7.getDefaultBiomeSet()) {
			if (biome != MinetestBiome.NONE) result.add(biome);
		}
		return result;
	}

	public static BiomeProfile getDefaultProfile() {
		return DEFAULT_PROFILE;
	}

	private static final BiomeProfile DEFAULT_PROFILE = new MinetestBiomeProfileImpl("default", null, createDefaultColorMap());

	private volatile String name;
	private volatile String shortcut;
	private volatile List<MinetestBiome> biomeList;
	/**
	 * Don't access this directly, use getAllBiomes().
	 * A cached abstract version of biomeList which we expose, and which doesn't 
	 * include MinetestBiome.NONE */ 
	private List<IBiome> allBiomes = null;
	
	private List<IBiome> getAllBiomes() {
		if (allBiomes == null) {
			allBiomes = new ArrayList<IBiome>(biomeList);
			
			// Minetest biomes don't have permanent indexes, but Amidst needs a number
			// so assign those.
			int i = 0;
			for (MinetestBiome biome : biomeList) biome.setIndex(i++);
		}
		return allBiomes;		
	}

	@GsonConstructor
	public MinetestBiomeProfileImpl() {
	}

	private MinetestBiomeProfileImpl(String name, String shortcut, List<MinetestBiome> biomes) {
		this.name = name;
		this.shortcut = shortcut;
		this.biomeList = biomes;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getShortcut() {
		return shortcut;
	}
	
	/** 
	 * The Minecraft BiomeProfile will just pass this through to the fixed Biome singleton,
	 * but the Minetest BiomeProfile determines the biomes itself.
	 */
	@Override
	public Collection<IBiome> allBiomes() {
		return getAllBiomes();
	}	

	/** 
	 * The Minecraft BiomeProfile will just pass this through to the fixed Biome singleton,
	 * but the Minetest BiomeProfile determines the biomes itself.
	 * @throws UnknownBiomeIndexException 
	 */
	@Override
	public IBiome getByIndex(int index) throws UnknownBiomeIndexException {
		try {
			return getAllBiomes().get(index);
		} catch(IndexOutOfBoundsException ex) {
			throw new UnknownBiomeIndexException("unsupported biome index detected: " + index);
		}
	}
	
	@Override
	public boolean invalidatesBiomeData() {
		// Minetest BiomeData is calculated using the BiomeProfile, changes to the
		// BiomeProfile mean the BiomeData must be recalculated.
		return true;
	}		
	
	@Override
	public boolean validate() {
		for (MinetestBiome biome : biomeList) {
			if (!biome.validate()) {
				AmidstLogger.info("Biome in biomeprofile invalid");
				return false;
			}
		}
		return true;
	}

	/**
	 * Create an array of colors such that the index in the array will
	 * match with the index written by BiomeDataOracle 
	 */
	@Override
	public BiomeColor[] createBiomeColorArray() {
		BiomeColor[] result = new BiomeColor[biomeList.size()];
		int i = 0;
		for (MinetestBiome biome : biomeList) {
			result[i++] = biome.getDefaultColor();
		}
		return result;
	}

	@Override
	public boolean save(File file) {
		return writeToFile(file, serialize());
	}

	private String serialize() {
		String output = "{ \"name\":\"" + name + "\", \"biomeList\":[\r\n";
		output += serializeColorMap();
		return output + "\r\n] }\r\n";
	}

	/**
	 * This method uses the sorted color map, so the serialization will have a
	 * reproducible order.
	 */
	private String serializeColorMap() {
		String output = "";
		for (MinetestBiome biome : getSortedBiomeEntries()) {
			output += String.format("    { \"name\": \"%s\", \"color\": %s, \"y_min\": %d, \"y_max\": %d, \"heat_point\": %.2f, \"humidity_point\": %.2f },\r\n",
					biome.getName(), 
					biome.getDefaultColor().createBiomeColorJson().toString(),
					biome.y_min, biome.y_max,
					biome.heat_point, biome.humidity_point);
		}
		return output.substring(0, output.length() - 3);
	}

	private List<MinetestBiome> getSortedBiomeEntries() {

		Comparator<MinetestBiome> SortByName = new Comparator<MinetestBiome>() {			
			@Override
			public int compare(MinetestBiome a, MinetestBiome b) {
		        return a.getName().compareTo(b.getName());
		    }
		};
		List<MinetestBiome> result = new ArrayList<>(biomeList);		
		Collections.sort(result, SortByName);
		return result;
	}

	private boolean writeToFile(File file, String output) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(output);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
