package amidst.fragment.layer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.Options;
import amidst.fragment.colorprovider.BiomeColorProvider;
import amidst.fragment.colorprovider.SlimeColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.constructor.ImageConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.drawer.GridDrawer;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.drawer.WorldObjectDrawer;
import amidst.fragment.loader.BiomeDataLoader;
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
		this.constructors = createConstructors();
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

	private List<FragmentConstructor> createConstructors() {
		FragmentConstructor[] constructors = new FragmentConstructor[2];
		// @formatter:off
		constructors[0]            = new BiomeDataConstructor(declarations.get(LayerIds.BIOME), Resolution.QUARTER);
		constructors[1]            = new ImageConstructor(    declarations.get(LayerIds.SLIME), Resolution.CHUNK);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(constructors));
	}

	public List<LayerDeclaration> getDeclarations() {
		return declarations;
	}

	public List<FragmentConstructor> getConstructors() {
		return constructors;
	}

	public int getNumberOfLayers() {
		return LayerIds.NUMBER_OF_LAYERS;
	}

	public LayerManager createLayerManager(World world, Map map) {
		List<FragmentLoader> loaders = createLoaders(world, map);
		List<FragmentDrawer> drawers = createDrawers(map);
		return new LayerManager(invalidatedLayers, declarations, loaders,
				drawers);
	}

	private List<FragmentLoader> createLoaders(World world, Map map) {
		FragmentLoader[] loaders = new FragmentLoader[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		loaders[0]                 = new BiomeDataLoader(  declarations.get(LayerIds.BIOME), Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle());
		loaders[1]                 = new ImageLoader(      declarations.get(LayerIds.SLIME), Resolution.CHUNK, new SlimeColorProvider(world.getSlimeChunkOracle()));
		loaders[2]               = new WorldObjectLoader(declarations.get(LayerIds.VILLAGE), world.getVillageProducer());
		loaders[3]        = new WorldObjectLoader(declarations.get(LayerIds.OCEAN_MONUMENT), world.getOceanMonumentProducer());
		loaders[4]            = new WorldObjectLoader(declarations.get(LayerIds.STRONGHOLD), world.getStrongholdProducer());
		loaders[5]                = new WorldObjectLoader(declarations.get(LayerIds.TEMPLE), world.getTempleProducer());
		loaders[6]                 = new WorldObjectLoader(declarations.get(LayerIds.SPAWN), world.getSpawnProducer());
		loaders[7]       = new WorldObjectLoader(declarations.get(LayerIds.NETHER_FORTRESS), world.getNetherFortressProducer());
		loaders[8]                = new WorldObjectLoader(declarations.get(LayerIds.PLAYER), world.getPlayerProducer());
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(loaders));
	}

	private List<FragmentDrawer> createDrawers(Map map) {
		FragmentDrawer[] drawers = new FragmentDrawer[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		drawers[0]                 = new ImageDrawer(      declarations.get(LayerIds.BIOME), Resolution.QUARTER);
		drawers[1]                 = new ImageDrawer(      declarations.get(LayerIds.SLIME), Resolution.CHUNK);
		drawers[2]                  = new GridDrawer(       declarations.get(LayerIds.GRID), map);
		drawers[3]               = new WorldObjectDrawer(declarations.get(LayerIds.VILLAGE), map);
		drawers[4]        = new WorldObjectDrawer(declarations.get(LayerIds.OCEAN_MONUMENT), map);
		drawers[5]            = new WorldObjectDrawer(declarations.get(LayerIds.STRONGHOLD), map);
		drawers[6]                = new WorldObjectDrawer(declarations.get(LayerIds.TEMPLE), map);
		drawers[7]                 = new WorldObjectDrawer(declarations.get(LayerIds.SPAWN), map);
		drawers[8]       = new WorldObjectDrawer(declarations.get(LayerIds.NETHER_FORTRESS), map);
		drawers[9]                = new WorldObjectDrawer(declarations.get(LayerIds.PLAYER), map);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(drawers));
	}
}
