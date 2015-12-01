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
	private final Options options;
	private final LayerContainer layerContainer;

	// @formatter:off
	public LayerContainerFactory(Options options, World world, Map map) {
		this.options = options;
		this.layerContainer = new LayerContainer(
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

	public LayerContainer createLayerContainer() {
		return layerContainer;
	}

	private Layer createBiomeLayer(World world, Map map) {
		return new Layer(LayerType.BIOME, options.showBiomes,
				new BiomeDataConstructor(LayerType.BIOME, Resolution.QUARTER),
				new BiomeDataLoader(LayerType.BIOME, Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle()),
				new ImageDrawer(LayerType.BIOME, Resolution.QUARTER));
	}

	private Layer createSlimeLayer(World world) {
		return new Layer(LayerType.SLIME, options.showSlimeChunks,
				new ImageConstructor(LayerType.SLIME, Resolution.CHUNK),
				new ImageLoader(LayerType.SLIME, Resolution.CHUNK, new SlimeColorProvider(world)),
				new ImageDrawer(LayerType.SLIME, Resolution.CHUNK));
	}

	private Layer createGridLayer(Map map) {
		return new Layer(LayerType.GRID, options.showGrid,
				new DummyConstructor(),
				new DummyLoader(),
				new GridDrawer(map));
	}

	private Layer createVillageLayer(World world, Map map) {
		return new Layer(LayerType.VILLAGE, options.showVillages,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.VILLAGE, world.getVillageProducer()),
				new WorldObjectDrawer(LayerType.VILLAGE, map));
	}

	private Layer createOceanMonumentLayer(World world, Map map) {
		return new Layer(LayerType.OCEAN_MONUMENT, options.showOceanMonuments,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.OCEAN_MONUMENT, world.getOceanMonumentProducer()),
				new WorldObjectDrawer(LayerType.OCEAN_MONUMENT, map));
	}

	private Layer createStrongholdLayer(World world, Map map) {
		return new Layer(LayerType.STRONGHOLD, options.showStrongholds,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.STRONGHOLD, world.getStrongholdProducer()),
				new WorldObjectDrawer(LayerType.STRONGHOLD, map));
	}

	private Layer createTempleLayer(World world, Map map) {
		return new Layer(LayerType.TEMPLE, options.showTemples,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.TEMPLE, world.getTempleProducer()),
				new WorldObjectDrawer(LayerType.TEMPLE, map));
	}

	private Layer createSpawnLayer(World world, Map map) {
		return new Layer(LayerType.SPAWN, options.showSpawn,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.SPAWN, world.getSpawnProducer()),
				new WorldObjectDrawer(LayerType.SPAWN, map));
	}

	private Layer createNetherFortressLayer(World world, Map map) {
		return new Layer(LayerType.NETHER_FORTRESS, options.showNetherFortresses,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.NETHER_FORTRESS, world.getNetherFortressProducer()),
				new WorldObjectDrawer(LayerType.NETHER_FORTRESS, map));
	}

	private Layer createPlayerLayer(World world, Map map) {
		return new Layer(LayerType.PLAYER, options.showPlayers,
				new DummyConstructor(),
				new WorldObjectLoader(LayerType.PLAYER, world.getPlayerProducer()),
				new WorldObjectDrawer(LayerType.PLAYER, map));
	}
	// @formatter:on
}
