package amidst.mojangapi.world.versionfeatures;

import java.util.Arrays;

import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.mojangapi.world.biome.UnknownBiomeNameException;

public class BiomeList {
	private Biome[] biomes = new Biome[1];
	private boolean isModifiable;

	public BiomeList() {
		this.isModifiable = true;
	}
	
	public BiomeList(Biome[] biomes) {
		this();
		this.biomes = biomes;
	}
	
	public BiomeList(BiomeList other) {
		this();
		this.biomes = other.biomes;
	}
	
	public Biome getByIdOrNull(int id) {
		return biomes[id];
	}
	
	public Biome getById(int id) throws UnknownBiomeIdException {
		try {
			Biome b = biomes[id];
			if (b != null) {
				return b;
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
		throw new UnknownBiomeIdException("couldn't find biome with id " + id);
	}
	
	public Biome getBiomeFromName(String name) throws UnknownBiomeNameException {
		for (Biome biome : biomes) {
			if (biome.getName().equals(name)) {
				return biome;
			}
		}
		throw new UnknownBiomeNameException("couldn't find biome with name " + name);
	}
	
	public boolean doesNameExist(String name) {
		for (Biome biome : biomes) {
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
			ensureArraySize(newBiome.getId());
			biomes[newBiome.getId()] = newBiome;
		} else {
			//throw new UnsupportedOperationException("List is locked");
		}
	}
	
	public void ensureArraySize(int newSize) {
		if (biomes.length <= newSize) {
			Biome[] newArray = new Biome[newSize + 1];
			System.arraycopy(biomes, 0, newArray, 0, biomes.length);
			biomes = newArray;
		}
	}
	
	public Iterable<Biome> iterable() {
		return () -> Arrays.stream(biomes).filter(b -> b != null).iterator();
	}
	
	public int size() {
		return biomes.length;
	}
	
	public int items() {
		int biomeAmount = 0;
		for(int i = 0; i < biomes.length; i++) {
			if(biomes[i] != null) {
				biomeAmount++;
			}
		}
		return biomeAmount;
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
