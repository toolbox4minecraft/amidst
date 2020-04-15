package amidst.mojangapi.world.biome;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;

@Immutable
public class Biome {
	public static final int SPECIAL_BIOMES_START = 128;

	public static List<Biome> getBiomeListFromIdList(BiomeList biomeList, List<Integer> idList) {
		return idList.stream().map(i -> biomeList.getByIdOrNull(i)).collect(Collectors.toList());
	}

	public static Comparator<Integer> biomeIdComparator() {
		return (a,b) -> Integer.compare(Math.abs(a), Math.abs(b));
	}

	private final int id;

	private final String name;
	private final BiomeType type;
	private final boolean isSpecialBiome;

	public Biome(String name, int baseId, BiomeType baseType) {
		this(baseId + SPECIAL_BIOMES_START, name, baseType.strengthen(), true);
	}

	public Biome(int id, String name, BiomeType type) {
		this(id, name, type, false);
	}

	public Biome(int id, String name, BiomeType type, boolean isSpecialBiome) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.isSpecialBiome = isSpecialBiome;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BiomeType getType() {
		return type;
	}

	public boolean isSpecialBiome() {
		return isSpecialBiome;
	}

	@Override
	public String toString() {
		return "[Biome " + name + "]";
	}
}
