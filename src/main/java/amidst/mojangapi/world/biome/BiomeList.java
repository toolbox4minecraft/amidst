package amidst.mojangapi.world.biome;

import java.util.Collection;
import java.util.TreeMap;

public class BiomeList {
	private TreeMap<Integer, Biome> biomes = new TreeMap<Integer, Biome>(Biome.biomeIdComparator());
	private boolean isModifiable;

	public BiomeList() {
		this.isModifiable = true;
	}

	public BiomeList(Collection<Biome> biomes) {
		this();
		for(Biome b : biomes) {
			this.biomes.put(b.getId(), b);
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
		Biome b = biomes.get(id);
		if (b != null) {
			return b;
		}
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

	public static BiomeList construct(BiomeList biomeList) {
		biomeList.isModifiable = false;
		return biomeList;
	}

}
