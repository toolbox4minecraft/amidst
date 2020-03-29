package amidst.mojangapi.world.biome;

import com.google.common.collect.BiMap;

public class BiomeIdNameMap {
	private final BiMap<Integer, String> biomeIdNameMap;
	
	public BiomeIdNameMap(BiMap<Integer, String> biomeIdNameMap) {
		this.biomeIdNameMap = biomeIdNameMap;
	}
	
	public String getNameFromId(int id) {
		return biomeIdNameMap.get(id);
	}
	
	public Biome getBiomeFromName(String name) throws UnknownBiomeNameException {
		try {
			return Biome.getByIndex(biomeIdNameMap.inverse().get(name));
		} catch (UnknownBiomeIndexException e) {
			throw new UnknownBiomeNameException("biome name provided did not match any id.");
		}
	}
	
	public String getNameFromBiome(Biome b) {
		// This is extremely unlikely to return null because we are providing it with a biome object
		return biomeIdNameMap.get(b.getIndex());
	}
	
	public boolean doesNameExist(String name) {
		return biomeIdNameMap.containsValue(name);
	}
	
	public boolean doesIdExist(int id) {
		return biomeIdNameMap.containsKey(id);
	}
	
}
