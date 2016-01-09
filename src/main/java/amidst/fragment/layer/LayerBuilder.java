package amidst.fragment.layer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.Settings;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentQueueProcessor;
import amidst.fragment.colorprovider.BackgroundColorProvider;
import amidst.fragment.colorprovider.BiomeColorProvider;
import amidst.fragment.colorprovider.SlimeColorProvider;
import amidst.fragment.colorprovider.TheEndColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.constructor.EndIslandsConstructor;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.constructor.ImageConstructor;
import amidst.fragment.drawer.AlphaUpdater;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.drawer.GridDrawer;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.drawer.WorldIconDrawer;
import amidst.fragment.loader.AlphaInitializer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.fragment.loader.EndIslandsLoader;
import amidst.fragment.loader.FragmentLoader;
import amidst.fragment.loader.ImageLoader;
import amidst.fragment.loader.WorldIconLoader;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.DimensionSelection;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.settings.ImmutableSetting;

@Immutable
public class LayerBuilder {
	private final List<LayerDeclaration> declarations;
	private final Iterable<FragmentConstructor> constructors;

	public LayerBuilder(Settings settings) {
		this.declarations = createDeclarations(settings);
		this.constructors = createConstructors();
	}

	private List<LayerDeclaration> createDeclarations(Settings settings) {
		LayerDeclaration[] declarations = new LayerDeclaration[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		declarations[LayerIds.ALPHA]            = new LayerDeclaration(LayerIds.ALPHA,           false, new ImmutableSetting<Boolean>(true));
		declarations[LayerIds.BIOME_DATA]       = new LayerDeclaration(LayerIds.BIOME_DATA,      false, new ImmutableSetting<Boolean>(true));
		declarations[LayerIds.END_ISLANDS]      = new LayerDeclaration(LayerIds.END_ISLANDS,     false, new ImmutableSetting<Boolean>(true));
		declarations[LayerIds.BACKGROUND]       = new LayerDeclaration(LayerIds.BACKGROUND,      false, new ImmutableSetting<Boolean>(true));
		declarations[LayerIds.SLIME]            = new LayerDeclaration(LayerIds.SLIME,           false, settings.showSlimeChunks);
		declarations[LayerIds.GRID]             = new LayerDeclaration(LayerIds.GRID,            true,  settings.showGrid);
		declarations[LayerIds.SPAWN]            = new LayerDeclaration(LayerIds.SPAWN,           false, settings.showSpawn);
		declarations[LayerIds.STRONGHOLD]       = new LayerDeclaration(LayerIds.STRONGHOLD,      false, settings.showStrongholds);
		declarations[LayerIds.PLAYER]           = new LayerDeclaration(LayerIds.PLAYER,          false, settings.showPlayers);
		declarations[LayerIds.VILLAGE]          = new LayerDeclaration(LayerIds.VILLAGE,         false, settings.showVillages);
		declarations[LayerIds.TEMPLE]           = new LayerDeclaration(LayerIds.TEMPLE,          false, settings.showTemples);
		declarations[LayerIds.MINESHAFT]        = new LayerDeclaration(LayerIds.MINESHAFT,       false, settings.showMineshafts);
		declarations[LayerIds.NETHER_FORTRESS]  = new LayerDeclaration(LayerIds.NETHER_FORTRESS, false, settings.showNetherFortresses);
		declarations[LayerIds.OCEAN_MONUMENT]   = new LayerDeclaration(LayerIds.OCEAN_MONUMENT,  false, settings.showOceanMonuments);
		declarations[LayerIds.END_CITY]         = new LayerDeclaration(LayerIds.END_CITY,        false, settings.showEndCities);
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
				new EndIslandsConstructor(),
				new ImageConstructor(    Resolution.QUARTER,  LayerIds.BACKGROUND),
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
			BiomeSelection biomeSelection,
			DimensionSelection dimensionSelection, Settings settings) {
		return new LayerLoader(createLoaders(world, biomeSelection,
				dimensionSelection, settings), LayerIds.NUMBER_OF_LAYERS);
	}

	/**
	 * This also defines the loading and reloading order.
	 */
	private Iterable<FragmentLoader> createLoaders(World world,
			BiomeSelection biomeSelection,
			DimensionSelection dimensionSelection, Settings settings) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new AlphaInitializer(                declarations.get(LayerIds.ALPHA),           settings.fragmentFading),
				new BiomeDataLoader(                 declarations.get(LayerIds.BIOME_DATA),      world.getBiomeDataOracle()),
				new EndIslandsLoader(                declarations.get(LayerIds.END_ISLANDS),     world.getEndIslandOracle()),
				new ImageLoader(	                 declarations.get(LayerIds.BACKGROUND),      Resolution.QUARTER, new BackgroundColorProvider(new BiomeColorProvider(biomeSelection, settings.biomeColorProfileSelection), new TheEndColorProvider(), dimensionSelection)),
				new ImageLoader(                     declarations.get(LayerIds.SLIME),           Resolution.CHUNK,   new SlimeColorProvider(world.getSlimeChunkOracle())),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.SPAWN),           world.getSpawnProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.STRONGHOLD),      world.getStrongholdProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.PLAYER),          world.getPlayerProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.VILLAGE),         world.getVillageProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.TEMPLE),          world.getTempleProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.MINESHAFT),       world.getMineshaftProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.NETHER_FORTRESS), world.getNetherFortressProducer()),
				new WorldIconLoader<Void>(           declarations.get(LayerIds.OCEAN_MONUMENT),  world.getOceanMonumentProducer()),
				new WorldIconLoader<List<EndIsland>>(declarations.get(LayerIds.END_CITY),        world.getEndCityProducer(), Fragment::getEndIslands)
		));
		// @formatter:on
	}

	/**
	 * This also defines the rendering order.
	 * 
	 * @param dimensionSelection
	 */
	public Iterable<FragmentDrawer> createDrawers(Zoom zoom,
			WorldIconSelection worldIconSelection,
			DimensionSelection dimensionSelection) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new AlphaUpdater(   declarations.get(LayerIds.ALPHA)),
				new ImageDrawer(    declarations.get(LayerIds.BACKGROUND),      Resolution.QUARTER),
				new ImageDrawer(    declarations.get(LayerIds.SLIME),           Resolution.CHUNK),
				new GridDrawer(     declarations.get(LayerIds.GRID),            zoom),
				new WorldIconDrawer(declarations.get(LayerIds.SPAWN),           zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.STRONGHOLD),      zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.PLAYER),          zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.VILLAGE),         zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.TEMPLE),          zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.MINESHAFT),       zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.NETHER_FORTRESS), zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.OCEAN_MONUMENT),  zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.END_CITY),        zoom, worldIconSelection)
		));
		// @formatter:on
	}

	public LayerReloader createLayerReloader(World world,
			FragmentQueueProcessor fragmentQueueProcessor) {
		return new LayerReloader(world, fragmentQueueProcessor);
	}
}
