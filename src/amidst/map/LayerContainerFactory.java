package amidst.map;

import java.util.ArrayList;
import java.util.List;

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
	private final LayerContainer layerContainer;

	// @formatter:off
	public LayerContainerFactory(Options options, World world, Map map) {
		List<LayerDeclaration> declarations = new ArrayList<LayerDeclaration>(10);
		declarations.add(new LayerDeclaration(LayerType.BIOME, options.showBiomes));
		declarations.add(new LayerDeclaration(LayerType.SLIME, options.showSlimeChunks));
		declarations.add(new LayerDeclaration(LayerType.GRID, options.showGrid));
		declarations.add(new LayerDeclaration(LayerType.VILLAGE, options.showVillages));
		declarations.add(new LayerDeclaration(LayerType.OCEAN_MONUMENT, options.showOceanMonuments));
		declarations.add(new LayerDeclaration(LayerType.STRONGHOLD, options.showStrongholds));
		declarations.add(new LayerDeclaration(LayerType.TEMPLE, options.showTemples));
		declarations.add(new LayerDeclaration(LayerType.SPAWN, options.showSpawn));
		declarations.add(new LayerDeclaration(LayerType.NETHER_FORTRESS, options.showNetherFortresses));
		declarations.add(new LayerDeclaration(LayerType.PLAYER, options.showPlayers));
		this.layerContainer = new LayerContainer(
				createBiomeLayer(declarations.get(0), world, map),
				createSlimeLayer(declarations.get(1), world),
				createGridLayer(declarations.get(2), map),
				createVillageLayer(declarations.get(3), world, map),
				createOceanMonumentLayer(declarations.get(4), world, map),
				createStrongholdLayer(declarations.get(5), world, map),
				createTempleLayer(declarations.get(6), world, map),
				createSpawnLayer(declarations.get(7), world, map),
				createNetherFortressLayer(declarations.get(8), world, map),
				createPlayerLayer(declarations.get(9), world, map)
		);
	}

	public LayerContainer createLayerContainer() {
		return layerContainer;
	}

	private Layer createBiomeLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new BiomeDataConstructor(declaration, Resolution.QUARTER),
				new BiomeDataLoader(declaration, Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle()),
				new ImageDrawer(declaration, Resolution.QUARTER));
	}

	private Layer createSlimeLayer(LayerDeclaration declaration, World world) {
		return new Layer(declaration,
				new ImageConstructor(declaration, Resolution.CHUNK),
				new ImageLoader(declaration, Resolution.CHUNK, new SlimeColorProvider(world)),
				new ImageDrawer(declaration, Resolution.CHUNK));
	}

	private Layer createGridLayer(LayerDeclaration declaration, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new DummyLoader(),
				new GridDrawer(map));
	}

	private Layer createVillageLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getVillageProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createOceanMonumentLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getOceanMonumentProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createStrongholdLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getStrongholdProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createTempleLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getTempleProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createSpawnLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getSpawnProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createNetherFortressLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getNetherFortressProducer()),
				new WorldObjectDrawer(declaration, map));
	}

	private Layer createPlayerLayer(LayerDeclaration declaration, World world, Map map) {
		return new Layer(declaration,
				new DummyConstructor(),
				new WorldObjectLoader(declaration, world.getPlayerProducer()),
				new WorldObjectDrawer(declaration, map));
	}
	// @formatter:on
}
