package amidst.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import amidst.map.layer.LayerId;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private final LayerContainer layerContainer;

	public LayerContainerFactory(Options options, World world, Map map) {
		List<AtomicBoolean> invalidatedLayers = createInvalidatedLayers();
		List<LayerDeclaration> declarations = createDeclarations(options);
		List<FragmentConstructor> constructors = createConstructors(declarations);
		List<FragmentLoader> loaders = createLoaders(declarations, world, map);
		List<FragmentDrawer> drawers = createDrawers(declarations, map);
		this.layerContainer = new LayerContainer(invalidatedLayers,
				declarations, constructors, loaders, drawers);
	}

	private List<AtomicBoolean> createInvalidatedLayers() {
		AtomicBoolean[] invalidatedLayers = new AtomicBoolean[LayerId.NUMBER_OF_LAYERS];
		for (int i = 0; i < LayerId.NUMBER_OF_LAYERS; i++) {
			invalidatedLayers[i] = new AtomicBoolean(false);
		}
		return Collections.unmodifiableList(Arrays.asList(invalidatedLayers));
	}

	private List<LayerDeclaration> createDeclarations(Options options) {
		LayerDeclaration[] declarations = new LayerDeclaration[LayerId.NUMBER_OF_LAYERS];
		// @formatter:off
		declarations[LayerId.BIOME]            = new LayerDeclaration(LayerType.BIOME,           options.showBiomes);
		declarations[LayerId.SLIME]            = new LayerDeclaration(LayerType.SLIME,           options.showSlimeChunks);
		declarations[LayerId.GRID]             = new LayerDeclaration(LayerType.GRID,            options.showGrid);
		declarations[LayerId.VILLAGE]          = new LayerDeclaration(LayerType.VILLAGE,         options.showVillages);
		declarations[LayerId.OCEAN_MONUMENT]   = new LayerDeclaration(LayerType.OCEAN_MONUMENT,  options.showOceanMonuments);
		declarations[LayerId.STRONGHOLD]       = new LayerDeclaration(LayerType.STRONGHOLD,      options.showStrongholds);
		declarations[LayerId.TEMPLE]           = new LayerDeclaration(LayerType.TEMPLE,          options.showTemples);
		declarations[LayerId.SPAWN]            = new LayerDeclaration(LayerType.SPAWN,           options.showSpawn);
		declarations[LayerId.NETHER_FORTRESS]  = new LayerDeclaration(LayerType.NETHER_FORTRESS, options.showNetherFortresses);
		declarations[LayerId.PLAYER]           = new LayerDeclaration(LayerType.PLAYER,          options.showPlayers);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(declarations));
	}

	private List<FragmentConstructor> createConstructors(
			List<LayerDeclaration> declarations) {
		FragmentConstructor[] constructors = new FragmentConstructor[LayerId.NUMBER_OF_LAYERS];
		// @formatter:off
		constructors[LayerId.BIOME]            = new BiomeDataConstructor(declarations.get(LayerId.BIOME), Resolution.QUARTER);
		constructors[LayerId.SLIME]            = new ImageConstructor(    declarations.get(LayerId.SLIME), Resolution.CHUNK);
		constructors[LayerId.GRID]             = new DummyConstructor();
		constructors[LayerId.VILLAGE]          = new DummyConstructor();
		constructors[LayerId.OCEAN_MONUMENT]   = new DummyConstructor();
		constructors[LayerId.STRONGHOLD]       = new DummyConstructor();
		constructors[LayerId.TEMPLE]           = new DummyConstructor();
		constructors[LayerId.SPAWN]            = new DummyConstructor();
		constructors[LayerId.NETHER_FORTRESS]  = new DummyConstructor();
		constructors[LayerId.PLAYER]           = new DummyConstructor();
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(constructors));
	}

	private List<FragmentLoader> createLoaders(
			List<LayerDeclaration> declarations, World world, Map map) {
		FragmentLoader[] loaders = new FragmentLoader[LayerId.NUMBER_OF_LAYERS];
		// @formatter:off
		loaders[LayerId.BIOME]                 = new BiomeDataLoader(  declarations.get(LayerId.BIOME), Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle());
		loaders[LayerId.SLIME]                 = new ImageLoader(      declarations.get(LayerId.SLIME), Resolution.CHUNK, new SlimeColorProvider(world.getSlimeChunkOracle()));
		loaders[LayerId.GRID]                  = new DummyLoader();
		loaders[LayerId.VILLAGE]               = new WorldObjectLoader(declarations.get(LayerId.VILLAGE), world.getVillageProducer());
		loaders[LayerId.OCEAN_MONUMENT]        = new WorldObjectLoader(declarations.get(LayerId.OCEAN_MONUMENT), world.getOceanMonumentProducer());
		loaders[LayerId.STRONGHOLD]            = new WorldObjectLoader(declarations.get(LayerId.STRONGHOLD), world.getStrongholdProducer());
		loaders[LayerId.TEMPLE]                = new WorldObjectLoader(declarations.get(LayerId.TEMPLE), world.getTempleProducer());
		loaders[LayerId.SPAWN]                 = new WorldObjectLoader(declarations.get(LayerId.SPAWN), world.getSpawnProducer());
		loaders[LayerId.NETHER_FORTRESS]       = new WorldObjectLoader(declarations.get(LayerId.NETHER_FORTRESS), world.getNetherFortressProducer());
		loaders[LayerId.PLAYER]                = new WorldObjectLoader(declarations.get(LayerId.PLAYER), world.getPlayerProducer());
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(loaders));
	}

	private List<FragmentDrawer> createDrawers(
			List<LayerDeclaration> declarations, Map map) {
		FragmentDrawer[] drawers = new FragmentDrawer[LayerId.NUMBER_OF_LAYERS];
		// @formatter:off
		drawers[LayerId.BIOME]                 = new ImageDrawer(      declarations.get(LayerId.BIOME), Resolution.QUARTER);
		drawers[LayerId.SLIME]                 = new ImageDrawer(      declarations.get(LayerId.SLIME), Resolution.CHUNK);
		drawers[LayerId.GRID]                  = new GridDrawer(       declarations.get(LayerId.GRID), map);
		drawers[LayerId.VILLAGE]               = new WorldObjectDrawer(declarations.get(LayerId.VILLAGE), map);
		drawers[LayerId.OCEAN_MONUMENT]        = new WorldObjectDrawer(declarations.get(LayerId.OCEAN_MONUMENT), map);
		drawers[LayerId.STRONGHOLD]            = new WorldObjectDrawer(declarations.get(LayerId.STRONGHOLD), map);
		drawers[LayerId.TEMPLE]                = new WorldObjectDrawer(declarations.get(LayerId.TEMPLE), map);
		drawers[LayerId.SPAWN]                 = new WorldObjectDrawer(declarations.get(LayerId.SPAWN), map);
		drawers[LayerId.NETHER_FORTRESS]       = new WorldObjectDrawer(declarations.get(LayerId.NETHER_FORTRESS), map);
		drawers[LayerId.PLAYER]                = new WorldObjectDrawer(declarations.get(LayerId.PLAYER), map);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(drawers));
	}

	public LayerContainer createLayerContainer() {
		return layerContainer;
	}
}
