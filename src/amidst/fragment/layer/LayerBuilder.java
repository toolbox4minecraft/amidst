package amidst.fragment.layer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.Options;
import amidst.fragment.FragmentManager;
import amidst.fragment.colorprovider.BiomeColorProvider;
import amidst.fragment.colorprovider.SlimeColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.constructor.ImageConstructor;
import amidst.fragment.drawer.AlphaUpdater;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.drawer.GridDrawer;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.drawer.WorldIconDrawer;
import amidst.fragment.loader.AlphaInitializer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.fragment.loader.FragmentLoader;
import amidst.fragment.loader.ImageLoader;
import amidst.fragment.loader.WorldIconLoader;
import amidst.gui.worldsurroundings.BiomeSelection;
import amidst.gui.worldsurroundings.WorldIconSelection;
import amidst.gui.worldsurroundings.Zoom;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.preferences.AlwaysTruePreference;

public class LayerBuilder {
	private final List<LayerDeclaration> declarations;
	private final Iterable<FragmentConstructor> constructors;

	public LayerBuilder(Options options) {
		this.declarations = createDeclarations(options);
		this.constructors = createConstructors();
	}

	private List<LayerDeclaration> createDeclarations(Options options) {
		LayerDeclaration[] declarations = new LayerDeclaration[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		declarations[LayerIds.ALPHA]            = new LayerDeclaration(LayerIds.ALPHA,           new AlwaysTruePreference());
		declarations[LayerIds.BIOME]            = new LayerDeclaration(LayerIds.BIOME,           new AlwaysTruePreference());
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
		return Collections.unmodifiableList(Arrays.asList(
				new BiomeDataConstructor(Resolution.QUARTER),
				new ImageConstructor(    Resolution.QUARTER,  LayerIds.BIOME),
				new ImageConstructor(    Resolution.CHUNK,    LayerIds.SLIME)
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

	public LayerLoader createLayerLoader(World world,
			BiomeSelection biomeSelection, Options options) {
		return new LayerLoader(createLoaders(world, biomeSelection, options),
				LayerIds.NUMBER_OF_LAYERS);
	}

	/**
	 * This also defines the loading and reloading order.
	 */
	private Iterable<FragmentLoader> createLoaders(World world,
			BiomeSelection biomeSelection, Options options) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new AlphaInitializer(declarations.get(LayerIds.ALPHA),           options.fragmentFading),
				new BiomeDataLoader( declarations.get(LayerIds.BIOME),           world.getBiomeDataOracle()),
				new ImageLoader(	 declarations.get(LayerIds.BIOME),           Resolution.QUARTER, new BiomeColorProvider(biomeSelection, options.biomeColorProfileSelection)),
				new ImageLoader(     declarations.get(LayerIds.SLIME),           Resolution.CHUNK,   new SlimeColorProvider(world.getSlimeChunkOracle())),
				new WorldIconLoader( declarations.get(LayerIds.VILLAGE),         world.getVillageProducer()),
				new WorldIconLoader( declarations.get(LayerIds.OCEAN_MONUMENT),  world.getOceanMonumentProducer()),
				new WorldIconLoader( declarations.get(LayerIds.STRONGHOLD),      world.getStrongholdProducer()),
				new WorldIconLoader( declarations.get(LayerIds.TEMPLE),          world.getTempleProducer()),
				new WorldIconLoader( declarations.get(LayerIds.SPAWN),           world.getSpawnProducer()),
				new WorldIconLoader( declarations.get(LayerIds.NETHER_FORTRESS), world.getNetherFortressProducer()),
				new WorldIconLoader( declarations.get(LayerIds.PLAYER),          world.getPlayerProducer())
		));
		// @formatter:on
	}

	/**
	 * This also defines the rendering order.
	 */
	public Iterable<FragmentDrawer> createDrawers(Zoom zoom,
			WorldIconSelection worldIconSelection) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new AlphaUpdater(   declarations.get(LayerIds.ALPHA)),
				new ImageDrawer(    declarations.get(LayerIds.BIOME),           Resolution.QUARTER),
				new ImageDrawer(    declarations.get(LayerIds.SLIME),           Resolution.CHUNK),
				new GridDrawer(     declarations.get(LayerIds.GRID),            zoom),
				new WorldIconDrawer(declarations.get(LayerIds.VILLAGE),         zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.OCEAN_MONUMENT),  zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.STRONGHOLD),      zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.TEMPLE),          zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.SPAWN),           zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.NETHER_FORTRESS), zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.PLAYER),          zoom, worldIconSelection)
		));
		// @formatter:on
	}

	public LayerReloader createLayerReloader(FragmentManager fragmentManager) {
		return new LayerReloader(fragmentManager);
	}
}
