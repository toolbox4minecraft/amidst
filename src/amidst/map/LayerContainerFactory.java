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
		LayerDeclaration declaration = new LayerDeclaration(LayerType.BIOME, options.showBiomes);
		return new Layer(declaration,
				new BiomeDataConstructor(declaration, Resolution.QUARTER),
				new BiomeDataLoader(declaration, Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle()),
				new ImageDrawer(declaration, Resolution.QUARTER));
	}

	private Layer createSlimeLayer(World world) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.SLIME, options.showSlimeChunks);
		return new Layer(declaration,
				new ImageConstructor(declaration, Resolution.CHUNK),
				new ImageLoader(declaration, Resolution.CHUNK, new SlimeColorProvider(world)),
				new ImageDrawer(declaration, Resolution.CHUNK));
	}

	private Layer createGridLayer(Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.GRID, options.showGrid);
		return new Layer(declaration,
				new DummyConstructor(),
				new DummyLoader(),
				new GridDrawer(map));
	}

	private Layer createVillageLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.VILLAGE, options.showVillages);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getVillageProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createOceanMonumentLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.OCEAN_MONUMENT, options.showOceanMonuments);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getOceanMonumentProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createStrongholdLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.STRONGHOLD, options.showStrongholds);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getStrongholdProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createTempleLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.TEMPLE, options.showTemples);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getTempleProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createSpawnLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.SPAWN, options.showSpawn);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getSpawnProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createNetherFortressLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.NETHER_FORTRESS, options.showNetherFortresses);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getNetherFortressProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createPlayerLayer(World world, Map map) {
		LayerDeclaration declaration = new LayerDeclaration(LayerType.PLAYER, options.showPlayers);
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getPlayerProducer()),
				new WorldObjectDrawer(declaration, map));
	}
	// @formatter:on
}
