package amidst.fragment.layer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.AmidstSettings;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
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
import amidst.gui.main.viewer.Graphics2DAccelerationCounter;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.versionfeatures.FeatureKey;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;
import amidst.settings.Setting;

@Immutable
public class LayerBuilder {
	private final Iterable<FragmentConstructor> constructors;

	public LayerBuilder() {
		this.constructors = createConstructors();
	}

	/**
	 * This also defines the construction order.
	 */
	private Iterable<FragmentConstructor> createConstructors() {
		return Collections.unmodifiableList(
				Arrays.asList(
						new BiomeDataConstructor(Resolution.QUARTER),
						new EndIslandsConstructor(),
						new ImageConstructor(Resolution.QUARTER, LayerIds.BACKGROUND),
						new ImageConstructor(Resolution.CHUNK, LayerIds.SLIME)));
	}

	public Iterable<FragmentConstructor> getConstructors() {
		return constructors;
	}

	public int getNumberOfLayers() {
		return LayerIds.NUMBER_OF_LAYERS;
	}

	public LayerManager create(
			AmidstSettings settings,
			World world,
			BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection,
			Zoom zoom,
			Graphics2DAccelerationCounter accelerationCounter) {
		List<LayerDeclaration> declarations = createDeclarations(settings, world.getVersionFeatures());
		return new LayerManager(
				declarations,
				new LayerLoader(
						createLoaders(declarations, world, biomeSelection, settings),
						LayerIds.NUMBER_OF_LAYERS),
				createDrawers(declarations, zoom, worldIconSelection, accelerationCounter));
	}

	private List<LayerDeclaration> createDeclarations(AmidstSettings settings, VersionFeatures versionFeatures) {
		LayerDeclaration[] declarations = new LayerDeclaration[LayerIds.NUMBER_OF_LAYERS];
		// @formatter:off
		declare(settings, declarations, versionFeatures, LayerIds.ALPHA,           null,                false, Setting.createImmutable(true));
		declare(settings, declarations, versionFeatures, LayerIds.BIOME_DATA,      Dimension.OVERWORLD, false, Setting.createImmutable(true));
		declare(settings, declarations, versionFeatures, LayerIds.END_ISLANDS,     Dimension.END,       false, Setting.createImmutable(true));
		declare(settings, declarations, versionFeatures, LayerIds.BACKGROUND,      null,                false, Setting.createImmutable(true));
		declare(settings, declarations, versionFeatures, LayerIds.SLIME,           Dimension.OVERWORLD, false, settings.showSlimeChunks);
		declare(settings, declarations, versionFeatures, LayerIds.GRID,            null,                true,  settings.showGrid);
		declare(settings, declarations, versionFeatures, LayerIds.SPAWN,           Dimension.OVERWORLD, false, settings.showSpawn);
		declare(settings, declarations, versionFeatures, LayerIds.STRONGHOLD,      Dimension.OVERWORLD, false, settings.showStrongholds);
		declare(settings, declarations, versionFeatures, LayerIds.PLAYER,          null,                false, settings.showPlayers);
		declare(settings, declarations, versionFeatures, LayerIds.VILLAGE,         Dimension.OVERWORLD, false, settings.showVillages);
		declare(settings, declarations, versionFeatures, LayerIds.TEMPLE,          Dimension.OVERWORLD, false, settings.showTemples);
		declare(settings, declarations, versionFeatures, LayerIds.MINESHAFT,       Dimension.OVERWORLD, false, settings.showMineshafts);
		declare(settings, declarations, versionFeatures, LayerIds.OCEAN_MONUMENT,  Dimension.OVERWORLD, false, settings.showOceanMonuments);
		declare(settings, declarations, versionFeatures, LayerIds.WOODLAND_MANSION,Dimension.OVERWORLD, false, settings.showWoodlandMansions);
		declare(settings, declarations, versionFeatures, LayerIds.OCEAN_FEATURES,  Dimension.OVERWORLD, false, settings.showOceanFeatures);
		declare(settings, declarations, versionFeatures, LayerIds.NETHER_FORTRESS, Dimension.OVERWORLD, false, settings.showNetherFortresses);
		declare(settings, declarations, versionFeatures, LayerIds.END_CITY,        Dimension.END,       false, settings.showEndCities);
		// @formatter:on
		return Collections.unmodifiableList(Arrays.asList(declarations));
	}

	private void declare(
			AmidstSettings settings,
			LayerDeclaration[] declarations,
			VersionFeatures versionFeatures,
			int layerId,
			Dimension dimension,
			boolean drawUnloaded,
			Setting<Boolean> isVisibleSetting) {
		declarations[layerId] = new LayerDeclaration(
				layerId,
				dimension,
				drawUnloaded,
				versionFeatures.get(FeatureKey.ENABLED_LAYERS).contains(layerId),
				isVisibleSetting);
	}

	/**
	 * This also defines the loading and reloading order.
	 */
	private Iterable<FragmentLoader> createLoaders(
			List<LayerDeclaration> declarations,
			World world,
			BiomeSelection biomeSelection,
			AmidstSettings settings) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new AlphaInitializer( declarations.get(LayerIds.ALPHA),           settings.fragmentFading),
				new BiomeDataLoader(  declarations.get(LayerIds.BIOME_DATA),      world.getBiomeDataOracle()),
				new EndIslandsLoader( declarations.get(LayerIds.END_ISLANDS),     world.getEndIslandOracle()),
				new ImageLoader(	  declarations.get(LayerIds.BACKGROUND),      Resolution.QUARTER, new BackgroundColorProvider(new BiomeColorProvider(biomeSelection, settings.biomeProfileSelection), new TheEndColorProvider())),
				new ImageLoader(      declarations.get(LayerIds.SLIME),           Resolution.CHUNK,   new SlimeColorProvider(world.getSlimeChunkOracle())),
				new WorldIconLoader<>(declarations.get(LayerIds.SPAWN),           world.getSpawnProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.STRONGHOLD),      world.getStrongholdProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.PLAYER),          world.getPlayerProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.VILLAGE),         world.getVillageProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.TEMPLE),          world.getTempleProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.MINESHAFT),       world.getMineshaftProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.OCEAN_MONUMENT),  world.getOceanMonumentProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.WOODLAND_MANSION),world.getWoodlandMansionProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.OCEAN_FEATURES),  world.getOceanFeaturesProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.NETHER_FORTRESS), world.getNetherFortressProducer()),
				new WorldIconLoader<>(declarations.get(LayerIds.END_CITY),        world.getEndCityProducer(), Fragment::getEndIslands)
		));
		// @formatter:on
	}

	/**
	 * This also defines the rendering order.
	 */
	private Iterable<FragmentDrawer> createDrawers(
			List<LayerDeclaration> declarations,
			Zoom zoom,
			WorldIconSelection worldIconSelection,
			Graphics2DAccelerationCounter accelerationCounter) {
		// @formatter:off
		return Collections.unmodifiableList(Arrays.asList(
				new AlphaUpdater(   declarations.get(LayerIds.ALPHA)),
				new ImageDrawer(    declarations.get(LayerIds.BACKGROUND),      Resolution.QUARTER, accelerationCounter),
				new ImageDrawer(    declarations.get(LayerIds.SLIME),           Resolution.CHUNK,   accelerationCounter),
				new GridDrawer(     declarations.get(LayerIds.GRID),            zoom),
				new WorldIconDrawer(declarations.get(LayerIds.SPAWN),           zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.STRONGHOLD),      zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.PLAYER),          zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.VILLAGE),         zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.TEMPLE),          zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.MINESHAFT),       zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.OCEAN_MONUMENT),  zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.WOODLAND_MANSION),zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.OCEAN_FEATURES),  zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.NETHER_FORTRESS), zoom, worldIconSelection),
				new WorldIconDrawer(declarations.get(LayerIds.END_CITY),        zoom, worldIconSelection)
		));
		// @formatter:on
	}
}
