package amidst.fragment.layer;

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
import amidst.map.Map;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerManagerFactory {
	private final List<AtomicBoolean> invalidatedLayers;
	private final List<LayerDeclaration> declarations;
	private final List<FragmentConstructor> constructors;

	public LayerManagerFactory(Options options) {
		this.invalidatedLayers = createInvalidatedLayers();
		this.declarations = createDeclarations(options);
		this.constructors = createConstructors(declarations);
	}

	private List<AtomicBoolean> createInvalidatedLayers() {
		AtomicBoolean[] invalidatedLayers = new AtomicBoolean[LayerIds.NUMBER_OF_LAYERS];
		for (int i = 0; i < LayerIds.NUMBER_OF_LAYERS; i++) {
			invalidatedLayers[i] = new AtomicBoolean(false);
		}
		return Collections.unmodifiableList(Arrays.asList(invalidatedLayers));
	}

	private List<LayerDeclaration> createDeclarations(Options options) {
		LayerDeclaration[] declarations = new LayerDeclaration[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		declarations[LayerIds.BIOME]            = new LayerDeclaration(LayerIds.BIOME,           options.showBiomes);
		declarations[LayerIds.SLIME]            = new LayerDeclaration(LayerIds.SLIME,           options.showSlimeChunks);
		declarations[LayerIds.GRID]             = new LayerDeclaration(LayerIds.GRID,            options.showGrid);
		declarations[LayerIds.VILLAGE]          = new LayerDeclaration(LayerIds.VILLAGE,         options.showVillages);
		declarations[LayerIds.OCEAN_MONUMENT]   = new LayerDeclaration(LayerIds.OCEAN_MONUMENT,  options.showOceanMonuments);
		declarations[LayerIds.STRONGHOLD]       = new LayerDeclaration(LayerIds.STRONGHOLD,      options.showStrongholds);
		declarations[LayerIds.TEMPLE]           = new LayerDeclaration(LayerIds.TEMPLE,          options.showTemples);
		declarations[LayerIds.SPAWN]            = new LayerDeclaration(LayerIds.SPAWN,           options.showSpawn);
		declarations[LayerIds.NETHER_FORTRESS]  = new LayerDeclaration(LayerIds.NETHER_FORTRESS, options.showNetherFortresses);
		declarations[LayerIds.PLAYER]           = new LayerDeclaration(LayerIds.PLAYER,          options.showPlayers);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(declarations));
	}

	private List<FragmentConstructor> createConstructors(
			List<LayerDeclaration> declarations) {
		FragmentConstructor[] constructors = new FragmentConstructor[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		constructors[LayerIds.BIOME]            = new BiomeDataConstructor(declarations.get(LayerIds.BIOME), Resolution.QUARTER);
		constructors[LayerIds.SLIME]            = new ImageConstructor(    declarations.get(LayerIds.SLIME), Resolution.CHUNK);
		constructors[LayerIds.GRID]             = new DummyConstructor();
		constructors[LayerIds.VILLAGE]          = new DummyConstructor();
		constructors[LayerIds.OCEAN_MONUMENT]   = new DummyConstructor();
		constructors[LayerIds.STRONGHOLD]       = new DummyConstructor();
		constructors[LayerIds.TEMPLE]           = new DummyConstructor();
		constructors[LayerIds.SPAWN]            = new DummyConstructor();
		constructors[LayerIds.NETHER_FORTRESS]  = new DummyConstructor();
		constructors[LayerIds.PLAYER]           = new DummyConstructor();
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(constructors));
	}

	public List<LayerDeclaration> getDeclarations() {
		return declarations;
	}

	public List<FragmentConstructor> getConstructors() {
		return constructors;
	}

	public LayerManager createLayerManager(World world, Map map) {
		List<FragmentLoader> loaders = createLoaders(declarations, world, map);
		List<FragmentDrawer> drawers = createDrawers(declarations, map);
		return new LayerManager(invalidatedLayers, declarations, loaders,
				drawers);
	}

	private List<FragmentLoader> createLoaders(
			List<LayerDeclaration> declarations, World world, Map map) {
		FragmentLoader[] loaders = new FragmentLoader[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		loaders[LayerIds.BIOME]                 = new BiomeDataLoader(  declarations.get(LayerIds.BIOME), Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle());
		loaders[LayerIds.SLIME]                 = new ImageLoader(      declarations.get(LayerIds.SLIME), Resolution.CHUNK, new SlimeColorProvider(world.getSlimeChunkOracle()));
		loaders[LayerIds.GRID]                  = new DummyLoader();
		loaders[LayerIds.VILLAGE]               = new WorldObjectLoader(declarations.get(LayerIds.VILLAGE), world.getVillageProducer());
		loaders[LayerIds.OCEAN_MONUMENT]        = new WorldObjectLoader(declarations.get(LayerIds.OCEAN_MONUMENT), world.getOceanMonumentProducer());
		loaders[LayerIds.STRONGHOLD]            = new WorldObjectLoader(declarations.get(LayerIds.STRONGHOLD), world.getStrongholdProducer());
		loaders[LayerIds.TEMPLE]                = new WorldObjectLoader(declarations.get(LayerIds.TEMPLE), world.getTempleProducer());
		loaders[LayerIds.SPAWN]                 = new WorldObjectLoader(declarations.get(LayerIds.SPAWN), world.getSpawnProducer());
		loaders[LayerIds.NETHER_FORTRESS]       = new WorldObjectLoader(declarations.get(LayerIds.NETHER_FORTRESS), world.getNetherFortressProducer());
		loaders[LayerIds.PLAYER]                = new WorldObjectLoader(declarations.get(LayerIds.PLAYER), world.getPlayerProducer());
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(loaders));
	}

	private List<FragmentDrawer> createDrawers(
			List<LayerDeclaration> declarations, Map map) {
		FragmentDrawer[] drawers = new FragmentDrawer[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		drawers[LayerIds.BIOME]                 = new ImageDrawer(      declarations.get(LayerIds.BIOME), Resolution.QUARTER);
		drawers[LayerIds.SLIME]                 = new ImageDrawer(      declarations.get(LayerIds.SLIME), Resolution.CHUNK);
		drawers[LayerIds.GRID]                  = new GridDrawer(       declarations.get(LayerIds.GRID), map);
		drawers[LayerIds.VILLAGE]               = new WorldObjectDrawer(declarations.get(LayerIds.VILLAGE), map);
		drawers[LayerIds.OCEAN_MONUMENT]        = new WorldObjectDrawer(declarations.get(LayerIds.OCEAN_MONUMENT), map);
		drawers[LayerIds.STRONGHOLD]            = new WorldObjectDrawer(declarations.get(LayerIds.STRONGHOLD), map);
		drawers[LayerIds.TEMPLE]                = new WorldObjectDrawer(declarations.get(LayerIds.TEMPLE), map);
		drawers[LayerIds.SPAWN]                 = new WorldObjectDrawer(declarations.get(LayerIds.SPAWN), map);
		drawers[LayerIds.NETHER_FORTRESS]       = new WorldObjectDrawer(declarations.get(LayerIds.NETHER_FORTRESS), map);
		drawers[LayerIds.PLAYER]                = new WorldObjectDrawer(declarations.get(LayerIds.PLAYER), map);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(drawers));
	}
}
