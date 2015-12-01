package amidst.map;

import amidst.Options;
import amidst.fragment.colorprovider.BiomeColorProvider;
import amidst.fragment.colorprovider.SlimeColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.constructor.DummyConstructor;
import amidst.fragment.constructor.ImageConstructor;
import amidst.fragment.drawer.GridDrawer;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.drawer.WorldObjectDrawer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.fragment.loader.DummyLoader;
import amidst.fragment.loader.ImageLoader;
import amidst.fragment.loader.WorldObjectLoader;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private Options options;

	public LayerContainerFactory(Options options) {
		this.options = options;
	}

	// @formatter:off
	public LayerContainer create(World world, Map map) {
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
	}

	private Layer createBiomeLayer(World world, Map map) {
		LayerType layerType = LayerType.BIOME;
		Resolution resolution = Resolution.QUARTER;
		return new Layer(layerType, options.showBiomes,
				new BiomeDataConstructor(layerType, resolution),
				new BiomeDataLoader(layerType, new BiomeColorProvider(map), resolution, world.getBiomeDataOracle()),
				new ImageDrawer(resolution, layerType));
	}

	private Layer createSlimeLayer(World world) {
		LayerType layerType = LayerType.SLIME;
		Resolution resolution = Resolution.CHUNK;
		return new Layer(layerType, options.showSlimeChunks,
				new ImageConstructor(layerType, resolution),
				new ImageLoader(layerType, new SlimeColorProvider(world), resolution),
				new ImageDrawer(resolution, layerType));
	}

	private Layer createGridLayer(Map map) {
		return new Layer(LayerType.GRID, options.showGrid,
				new DummyConstructor(),
				new DummyLoader(),
				new GridDrawer(map));
	}

	private Layer createVillageLayer(World world, Map map) {
		LayerType layerType = LayerType.VILLAGE;
		return new Layer(layerType, options.showVillages,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getVillageProducer()),
				new WorldObjectDrawer(map, layerType));
	}

	private Layer createOceanMonumentLayer(World world, Map map) {
		LayerType layerType = LayerType.OCEAN_MONUMENT;
		return new Layer(layerType, options.showOceanMonuments,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getOceanMonumentProducer()),
				new WorldObjectDrawer(map, layerType));
	}

	private Layer createStrongholdLayer(World world, Map map) {
		LayerType layerType = LayerType.STRONGHOLD;
		return new Layer(layerType, options.showStrongholds,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getStrongholdProducer()),
				new WorldObjectDrawer(map, layerType));
	}

	private Layer createTempleLayer(World world, Map map) {
		LayerType layerType = LayerType.TEMPLE;
		return new Layer(layerType, options.showTemples,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getTempleProducer()),
				new WorldObjectDrawer(map, layerType));
	}

	private Layer createSpawnLayer(World world, Map map) {
		LayerType layerType = LayerType.SPAWN;
		return new Layer(layerType, options.showSpawn,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getSpawnProducer()),
				new WorldObjectDrawer(map, layerType));
	}

	private Layer createNetherFortressLayer(World world, Map map) {
		LayerType layerType = LayerType.NETHER_FORTRESS;
		return new Layer(layerType, options.showNetherFortresses,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getNetherFortressProducer()),
				new WorldObjectDrawer(map, layerType));
	}

	private Layer createPlayerLayer(World world, Map map) {
		LayerType layerType = LayerType.PLAYER;
		return new Layer(layerType, options.showPlayers,
				new DummyConstructor(),
				new WorldObjectLoader(layerType, world.getPlayerProducer()),
				new WorldObjectDrawer(map, layerType));
	}
	// @formatter:on
}
