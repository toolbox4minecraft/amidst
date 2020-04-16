package amidst.mojangapi.world.versionfeatures;

import java.util.TreeMap;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.biome.UnknownBiomeNameException;

public class BiomeList {
	private TreeMap<Integer, Biome> biomes = new TreeMap<Integer, Biome>(Biome.biomeIdComparator());
	private boolean isModifiable;

	public BiomeList() {
		this.isModifiable = true;
	}
	
	public BiomeList(Biome[] biomeArray) {
		this();
		for(Biome b : biomeArray) {
			biomes.put(b.getId(), b);
		}
	}
	
	public BiomeList(BiomeList other) {
		this();
		this.biomes.putAll(other.biomes);
	}
	
	public Biome getByIdOrNull(int id) {
		return biomes.get(id);
	}
	
	public Biome getById(int id) throws UnknownBiomeIdException {
		try {
			Biome b = biomes.get(id);
			if (b != null) {
				return b;
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
		throw new UnknownBiomeIdException("couldn't find biome with id " + id);
	}
	
	public Biome getBiomeFromName(String name) throws UnknownBiomeNameException {
		for (Biome biome : biomes.values()) {
			if (biome.getName().equals(name)) {
				return biome;
			}
		}
		throw new UnknownBiomeNameException("couldn't find biome with name " + name);
	}
	
	public boolean doesNameExist(String name) {
		for (Biome biome : biomes.values()) {
			if (biome.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesIdExist(int id) {
		try {
			getById(id);
			return true;
		} catch (UnknownBiomeIdException e) {
			return false;
		}
	}

	public void add(Biome newBiome) {
		if (isModifiable) {
			biomes.put(newBiome.getId(), newBiome);
		} else {
			throw new UnsupportedOperationException("List is locked");
		}
	}
	
	public Iterable<Biome> iterable() {
		return biomes.values();
	}
	
	public int size() {
		return biomes.values().size();
	}
	
	public BiomeList addAllToNew(BiomeList biomeList, Biome[] newBiomes) {
		BiomeList newList = new BiomeList(this);
		for(int i = 0; i < newBiomes.length; i++) {
			newList.add(newBiomes[i]);
		}
		return newList;
	}
	
	public static BiomeList construct(BiomeList biomeList) {
		biomeList.isModifiable = false;
		return biomeList;
	}
	
}
