package amidst.map;

import java.util.ArrayList;
import java.util.List;

import amidst.Options;
import amidst.fragment.colorprovider.BiomeColorProvider;
import amidst.fragment.colorprovider.SlimeColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.constructor.DummyConstructor;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.constructor.ImageConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.drawer.GridDrawer;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.drawer.WorldObjectDrawer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.fragment.loader.DummyLoader;
import amidst.fragment.loader.FragmentLoader;
import amidst.fragment.loader.ImageLoader;
import amidst.fragment.loader.WorldObjectLoader;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private final LayerContainer layerContainer;

	public LayerContainerFactory(Options options, World world, Map map) {
		// @formatter:off
		List<LayerDeclaration> declarations = new ArrayList<LayerDeclaration>();
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
		List<FragmentConstructor> constructors = new ArrayList<FragmentConstructor>();
		constructors.add(new BiomeDataConstructor(declarations.get(0), Resolution.QUARTER));
		constructors.add(new ImageConstructor(declarations.get(1), Resolution.CHUNK));
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		constructors.add(new DummyConstructor());
		List<FragmentLoader> loaders = new ArrayList<FragmentLoader>();
		loaders.add(new BiomeDataLoader(declarations.get(0), Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle()));
		loaders.add(new ImageLoader(declarations.get(1), Resolution.CHUNK, new SlimeColorProvider(world)));
		loaders.add(new DummyLoader());
		loaders.add(new WorldObjectLoader(declarations.get(3), world.getVillageProducer()));
		loaders.add(new WorldObjectLoader(declarations.get(4), world.getOceanMonumentProducer()));
		loaders.add(new WorldObjectLoader(declarations.get(5), world.getStrongholdProducer()));
		loaders.add(new WorldObjectLoader(declarations.get(6), world.getTempleProducer()));
		loaders.add(new WorldObjectLoader(declarations.get(7), world.getSpawnProducer()));
		loaders.add(new WorldObjectLoader(declarations.get(8), world.getNetherFortressProducer()));
		loaders.add(new WorldObjectLoader(declarations.get(9), world.getPlayerProducer()));
		List<FragmentDrawer> drawers = new ArrayList<FragmentDrawer>();
		drawers.add(new ImageDrawer(declarations.get(0), Resolution.QUARTER));
		drawers.add(new ImageDrawer(declarations.get(1), Resolution.CHUNK));
		drawers.add(new GridDrawer(declarations.get(2), map));
		drawers.add(new WorldObjectDrawer(declarations.get(3), map));
		drawers.add(new WorldObjectDrawer(declarations.get(4), map));
		drawers.add(new WorldObjectDrawer(declarations.get(5), map));
		drawers.add(new WorldObjectDrawer(declarations.get(6), map));
		drawers.add(new WorldObjectDrawer(declarations.get(7), map));
		drawers.add(new WorldObjectDrawer(declarations.get(8), map));
		drawers.add(new WorldObjectDrawer(declarations.get(9), map));
		// @formatter:on
		this.layerContainer = new LayerContainer(declarations, constructors,
				loaders, drawers);
	}

	public LayerContainer createLayerContainer() {
		return layerContainer;
	}
}
