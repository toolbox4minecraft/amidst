package amidst.mojangapi.world.versionfeatures;

import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@Immutable
public class VersionFeatures {
	private final List<Integer> enabledLayers;
	private final List<Biome> validBiomesForStructure_Spawn;
	private final List<Biome> validBiomesAtMiddleOfChunk_Stronghold;
	private final TriFunction<Long, BiomeDataOracle, List<Biome>, CachedWorldIconProducer> strongholdProducerFactory;
	private final List<Biome> validBiomesForStructure_Village;
	private final List<Biome> validBiomesForStructure_PillagerOutpost;
	private final Boolean doComplexVillageCheck;
	private final Integer outpostVillageAvoidDistance;
	private final List<Biome> validBiomesAtMiddleOfChunk_DesertTemple;
	private final List<Biome> validBiomesAtMiddleOfChunk_Igloo;
	private final List<Biome> validBiomesAtMiddleOfChunk_JungleTemple;
	private final List<Biome> validBiomesAtMiddleOfChunk_WitchHut;
	private final List<Biome> validBiomesAtMiddleOfChunk_OceanRuins;
	private final List<Biome> validBiomesAtMiddleOfChunk_Shipwreck;
	private final Function<Long, LocationChecker> mineshaftAlgorithmFactory;
	private final QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker> oceanMonumentLocationCheckerFactory;
	private final List<Biome> validBiomesAtMiddleOfChunk_OceanMonument;
	private final List<Biome> validBiomesAtMiddleOfChunk_BuriedTreasure;
	private final List<Biome> validBiomesForStructure_OceanMonument;
	private final List<Biome> validBiomesForStructure_WoodlandMansion;
	private final Long seedForStructure_DesertTemple;
	private final Long seedForStructure_Igloo;
	private final Long seedForStructure_JungleTemple;
	private final Long seedForStructure_WitchHut;
	private final Long seedForStructure_OceanRuins;
	private final Long seedForStructure_Shipwreck;
	private final Long seedForStructure_BuriedTreasure;
	private final Byte maxDistanceScatteredFeatures_Shipwreck;
	private final Byte minDistanceScatteredFeatures_Shipwreck;
	private final Byte maxDistanceScatteredFeatures_OceanRuins;
	private final Boolean buggyStructureCoordinateMath;

	public VersionFeatures(
			List<Integer> enabledLayers,
			List<Biome> validBiomesForStructure_Spawn,
			List<Biome> validBiomesAtMiddleOfChunk_Stronghold,
			TriFunction<Long, BiomeDataOracle, List<Biome>, CachedWorldIconProducer> strongholdProducerFactory,
			List<Biome> validBiomesForStructure_Village,
			List<Biome> validBiomesForStructure_PillagerOutpost,
			Boolean doComplexVillageCheck,
			Integer outpostVillageAvoidDistance,
			List<Biome> validBiomesAtMiddleOfChunk_DesertTemple,
			List<Biome> validBiomesAtMiddleOfChunk_Igloo,
			List<Biome> validBiomesAtMiddleOfChunk_JungleTemple,
			List<Biome> validBiomesAtMiddleOfChunk_WitchHut,
			List<Biome> validBiomesAtMiddleOfChunk_OceanRuins,
			List<Biome> validBiomesAtMiddleOfChunk_Shipwreck,
			Function<Long, LocationChecker> mineshaftAlgorithmFactory,
			QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker> oceanMonumentLocationCheckerFactory,
			List<Biome> validBiomesAtMiddleOfChunk_OceanMonument,
			List<Biome> validBiomesAtMiddleOfChunk_BuriedTreasure,
			List<Biome> validBiomesForStructure_OceanMonument,
			List<Biome> validBiomesForStructure_WoodlandMansion,
			Long seedForStructure_DesertTemple,
			Long seedForStructure_Igloo,
			Long seedForStructure_JungleTemple,
			Long seedForStructure_WitchHut,
			Long seedForStructure_OceanRuins,
			Long seedForStructure_Shipwreck,
			Long seedForStructure_BuriedTreasure,
			Byte maxDistanceScatteredFeatures_Shipwreck,
			Byte minDistanceScatteredFeatures_Shipwreck,
			Byte maxDistanceScatteredFeatures_OceanRuins,
			Boolean buggyStructureCoordinateMath) {
		this.enabledLayers = enabledLayers;
		this.validBiomesForStructure_Spawn = validBiomesForStructure_Spawn;
		this.validBiomesAtMiddleOfChunk_Stronghold = validBiomesAtMiddleOfChunk_Stronghold;
		this.strongholdProducerFactory = strongholdProducerFactory;
		this.validBiomesForStructure_Village = validBiomesForStructure_Village;
		this.validBiomesForStructure_PillagerOutpost = validBiomesForStructure_PillagerOutpost;
		this.doComplexVillageCheck = doComplexVillageCheck;
		this.outpostVillageAvoidDistance = outpostVillageAvoidDistance;
		this.validBiomesAtMiddleOfChunk_DesertTemple = validBiomesAtMiddleOfChunk_DesertTemple;
		this.validBiomesAtMiddleOfChunk_Igloo = validBiomesAtMiddleOfChunk_Igloo;
		this.validBiomesAtMiddleOfChunk_JungleTemple = validBiomesAtMiddleOfChunk_JungleTemple;
		this.validBiomesAtMiddleOfChunk_WitchHut = validBiomesAtMiddleOfChunk_WitchHut;
		this.validBiomesAtMiddleOfChunk_OceanRuins = validBiomesAtMiddleOfChunk_OceanRuins;
		this.validBiomesAtMiddleOfChunk_Shipwreck = validBiomesAtMiddleOfChunk_Shipwreck;
		this.mineshaftAlgorithmFactory = mineshaftAlgorithmFactory;
		this.oceanMonumentLocationCheckerFactory = oceanMonumentLocationCheckerFactory;
		this.validBiomesAtMiddleOfChunk_OceanMonument = validBiomesAtMiddleOfChunk_OceanMonument;
		this.validBiomesAtMiddleOfChunk_BuriedTreasure = validBiomesAtMiddleOfChunk_BuriedTreasure;
		this.validBiomesForStructure_OceanMonument = validBiomesForStructure_OceanMonument;
		this.validBiomesForStructure_WoodlandMansion = validBiomesForStructure_WoodlandMansion;
		this.seedForStructure_DesertTemple = seedForStructure_DesertTemple;
		this.seedForStructure_Igloo = seedForStructure_Igloo;
		this.seedForStructure_JungleTemple = seedForStructure_JungleTemple;
		this.seedForStructure_WitchHut = seedForStructure_WitchHut;
		this.seedForStructure_OceanRuins = seedForStructure_OceanRuins;
		this.seedForStructure_Shipwreck = seedForStructure_Shipwreck;
		this.seedForStructure_BuriedTreasure = seedForStructure_BuriedTreasure;
		this.maxDistanceScatteredFeatures_Shipwreck = maxDistanceScatteredFeatures_Shipwreck;
		this.minDistanceScatteredFeatures_Shipwreck = minDistanceScatteredFeatures_Shipwreck;
		this.maxDistanceScatteredFeatures_OceanRuins = maxDistanceScatteredFeatures_OceanRuins;
		this.buggyStructureCoordinateMath = buggyStructureCoordinateMath;
	}

	public boolean hasLayer(int layerId) {
		return enabledLayers.contains(layerId);
	}

	public List<Biome> getValidBiomesForStructure_Spawn() {
		return validBiomesForStructure_Spawn;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Stronghold() {
		return validBiomesAtMiddleOfChunk_Stronghold;
	}

	public TriFunction<Long, BiomeDataOracle, List<Biome>, CachedWorldIconProducer> getStrongholdProducerFactory() {
		return strongholdProducerFactory;
	}

	public List<Biome> getValidBiomesForStructure_Village() {
		return validBiomesForStructure_Village;
	}

	public List<Biome> getValidBiomesForStructure_PillagerOutpost() {
		return validBiomesForStructure_PillagerOutpost;
	}

	public Boolean getDoComplexVillageCheck() {
		return doComplexVillageCheck;
	}

	public Integer getOutpostVillageAvoidDistance() {
		return outpostVillageAvoidDistance;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_DesertTemple() {
		return validBiomesAtMiddleOfChunk_DesertTemple;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Igloo() {
		return validBiomesAtMiddleOfChunk_Igloo;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_JungleTemple() {
		return validBiomesAtMiddleOfChunk_JungleTemple;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_WitchHut() {
		return validBiomesAtMiddleOfChunk_WitchHut;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_OceanRuins() {
		return validBiomesAtMiddleOfChunk_OceanRuins;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_Shipwreck() {
		return validBiomesAtMiddleOfChunk_Shipwreck;
	}

	public Function<Long, LocationChecker> getMineshaftAlgorithmFactory() {
		return mineshaftAlgorithmFactory;
	}

	public QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker> getOceanMonumentLocationCheckerFactory() {
		return oceanMonumentLocationCheckerFactory;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_OceanMonument() {
		return validBiomesAtMiddleOfChunk_OceanMonument;
	}

	public List<Biome> getValidBiomesAtMiddleOfChunk_BuriedTreasure() {
		return validBiomesAtMiddleOfChunk_BuriedTreasure;
	}

	public List<Biome> getValidBiomesForStructure_OceanMonument() {
		return validBiomesForStructure_OceanMonument;
	}

	public List<Biome> getValidBiomesForStructure_WoodlandMansion() {
		return validBiomesForStructure_WoodlandMansion;
	}

	public Long getSeedForStructure_DesertTemple() {
		return seedForStructure_DesertTemple;
	}

	public Long getSeedForStructure_Igloo() {
		return seedForStructure_Igloo;
	}

	public Long getSeedForStructure_JungleTemple() {
		return seedForStructure_JungleTemple;
	}

	public Long getSeedForStructure_WitchHut() {
		return seedForStructure_WitchHut;
	}

	public Long getSeedForStructure_OceanRuins() {
		return seedForStructure_OceanRuins;
	}

	public Long getSeedForStructure_Shipwreck() {
		return seedForStructure_Shipwreck;
	}

	public Long getSeedForStructure_BuriedTreasure() {
		return seedForStructure_BuriedTreasure;
	}

	public Byte getMaxDistanceScatteredFeatures_Shipwreck() {
		return maxDistanceScatteredFeatures_Shipwreck;
	}
	
	public Byte getMinDistanceScatteredFeatures_Shipwreck() {
		return minDistanceScatteredFeatures_Shipwreck;
	}
	
	public Byte getMaxDistanceScatteredFeatures_OceanRuins() {
		return maxDistanceScatteredFeatures_OceanRuins;
	}

	public Boolean getBuggyStructureCoordinateMath() {
		return buggyStructureCoordinateMath;
	}
}
