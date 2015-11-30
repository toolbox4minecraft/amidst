package amidst.map;

import amidst.Options;
import amidst.map.layer.BiomeColorProvider;
import amidst.map.layer.BiomeLayer;
import amidst.map.layer.GridLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.map.layer.SlimeColorProvider;
import amidst.map.layer.WorldObjectLayer;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private Options options;

	public LayerContainerFactory(Options options) {
		this.options = options;
	}

	public LayerContainer create(World world, Map map) {
		// @formatter:off
		return new LayerContainer(
				createBiomeLayer(world, map),
				createSlimeLayer(world),
				createGridLayer(map),
				createVillageLayer(world, map),
				createOceanMonumentLayer(world, map),
				createStrongholdLayer(world, map),
				createTempleLayer(world, map),
				createSpawnLayer(world, map),
				createNetherFortressLayer(world, map),
				createPlayerLayer(world, map)
		);
		// @formatter:on
	}

	private Layer createBiomeLayer(World world, Map map) {
		return new BiomeLayer(LayerType.BIOME, options.showBiomes,
				new BiomeColorProvider(map), Resolution.QUARTER,
				world.getBiomeDataOracle());
	}

	private Layer createSlimeLayer(World world) {
		return new ImageLayer(LayerType.SLIME, options.showSlimeChunks,
				new SlimeColorProvider(world), Resolution.CHUNK);
	}

	private Layer createGridLayer(Map map) {
		return new GridLayer(LayerType.GRID, options.showGrid, map);
	}

	private Layer createVillageLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.VILLAGE, options.showVillages,
				map, world.getVillageProducer());
	}

	private Layer createOceanMonumentLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.OCEAN_MONUMENT,
				options.showOceanMonuments, map,
				world.getOceanMonumentProducer());
	}

	private Layer createStrongholdLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.STRONGHOLD,
				options.showStrongholds, map, world.getStrongholdProducer());
	}

	private Layer createTempleLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.TEMPLE, options.showTemples, map,
				world.getTempleProducer());
	}

	private Layer createSpawnLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.SPAWN, options.showSpawn, map,
				world.getSpawnProducer());
	}

	private Layer createNetherFortressLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.NETHER_FORTRESS,
				options.showNetherFortresses, map,
				world.getNetherFortressProducer());
	}

	private Layer createPlayerLayer(World world, Map map) {
		return new WorldObjectLayer(LayerType.PLAYER, options.showPlayers, map,
				world.getPlayerProducer());
	}
}
