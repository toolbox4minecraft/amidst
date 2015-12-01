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
import amidst.fragment.drawer.WorldIconDrawer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.fragment.loader.FragmentLoader;
import amidst.fragment.loader.ImageLoader;
import amidst.fragment.loader.WorldIconLoader;
import amidst.map.Map;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerManagerFactory {
	private final List<AtomicBoolean> invalidatedLayers;
	private final List<LayerDeclaration> declarations;
	private final Iterable<FragmentConstructor> constructors;

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

	/**
	 * This also defines the construction order.
	 */
	private Iterable<FragmentConstructor> createConstructors() {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList((FragmentConstructor)
				new BiomeDataConstructor(declarations.get(LayerIds.BIOME), Resolution.QUARTER),
				new ImageConstructor(    declarations.get(LayerIds.SLIME), Resolution.CHUNK)
		));
		// @formatter:on
	}

	public List<LayerDeclaration> getDeclarations() {
		return declarations;
	}

	public Iterable<FragmentConstructor> getConstructors() {
		return constructors;
	}

	public int getNumberOfLayers() {
		return LayerIds.NUMBER_OF_LAYERS;
	}

	public LayerManager createLayerManager(World world, Map map) {
		Iterable<FragmentLoader> loaders = createLoaders(world, map);
		Iterable<FragmentDrawer> drawers = createDrawers(map);
		return new LayerManager(invalidatedLayers, declarations, loaders,
				drawers);
	}

	/**
	 * This also defines the loading and reloading order.
	 */
	private Iterable<FragmentLoader> createLoaders(World world, Map map) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new BiomeDataLoader(declarations.get(LayerIds.BIOME), world.getBiomeDataOracle()),
				new ImageLoader(	declarations.get(LayerIds.BIOME), Resolution.QUARTER, new BiomeColorProvider(map.getBiomeSelection())),
				new ImageLoader(    declarations.get(LayerIds.SLIME), Resolution.CHUNK,   new SlimeColorProvider(world.getSlimeChunkOracle())),
				new WorldIconLoader(declarations.get(LayerIds.VILLAGE),         world.getVillageProducer()),
				new WorldIconLoader(declarations.get(LayerIds.OCEAN_MONUMENT),  world.getOceanMonumentProducer()),
				new WorldIconLoader(declarations.get(LayerIds.STRONGHOLD),      world.getStrongholdProducer()),
				new WorldIconLoader(declarations.get(LayerIds.TEMPLE),          world.getTempleProducer()),
				new WorldIconLoader(declarations.get(LayerIds.SPAWN),           world.getSpawnProducer()),
				new WorldIconLoader(declarations.get(LayerIds.NETHER_FORTRESS), world.getNetherFortressProducer()),
				new WorldIconLoader(declarations.get(LayerIds.PLAYER),          world.getPlayerProducer())
		));
		// @formatter:on
	}

	/**
	 * This also defines the rendering order.
	 */
	private Iterable<FragmentDrawer> createDrawers(Map map) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new ImageDrawer(    declarations.get(LayerIds.BIOME),           Resolution.QUARTER),
				new ImageDrawer(    declarations.get(LayerIds.SLIME),           Resolution.CHUNK),
				new GridDrawer(     declarations.get(LayerIds.GRID),            map),
				new WorldIconDrawer(declarations.get(LayerIds.VILLAGE),         map),
				new WorldIconDrawer(declarations.get(LayerIds.OCEAN_MONUMENT),  map),
				new WorldIconDrawer(declarations.get(LayerIds.STRONGHOLD),      map),
				new WorldIconDrawer(declarations.get(LayerIds.TEMPLE),          map),
				new WorldIconDrawer(declarations.get(LayerIds.SPAWN),           map),
				new WorldIconDrawer(declarations.get(LayerIds.NETHER_FORTRESS), map),
				new WorldIconDrawer(declarations.get(LayerIds.PLAYER),          map)
		));
		// @formatter:on
	}
}
