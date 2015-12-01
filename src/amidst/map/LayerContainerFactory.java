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
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;
import amidst.preferences.PrefModel;

public class LayerContainerFactory {
	private Options options;

	public LayerContainerFactory(Options options) {
		this.options = options;
	}

	public LayerContainer create(World world, Map map) {
		// @formatter:off
		return new LayerContainer(
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
		// @formatter:on
	}

	private Layer createBiomeLayer(World world, Map map) {
		return doCreateBiomeLayer(LayerType.BIOME, options.showBiomes,
				new BiomeColorProvider(map), Resolution.QUARTER,
				world.getBiomeDataOracle());
	}

	private Layer doCreateBiomeLayer(LayerType layerType,
			PrefModel<Boolean> isVisiblePreference,
			BiomeColorProvider colorProvider, Resolution resolution,
			BiomeDataOracle biomeDataOracle) {
		return new Layer(layerType, isVisiblePreference, new ImageDrawer(
				resolution, layerType), new BiomeDataConstructor(layerType,
				resolution), new BiomeDataLoader(layerType, colorProvider,
				resolution, biomeDataOracle));
	}

	private Layer createSlimeLayer(World world) {
		return doCreateImageLayer(LayerType.SLIME, options.showSlimeChunks,
				new SlimeColorProvider(world), Resolution.CHUNK);
	}

	private Layer doCreateImageLayer(LayerType layerType,
			BooleanPrefModel isVisiblePreference,
			SlimeColorProvider colorProvider, Resolution resolution) {
		return new Layer(layerType, isVisiblePreference, new ImageDrawer(
				resolution, layerType), new ImageConstructor(layerType,
				resolution), new ImageLoader(layerType, colorProvider,
				resolution));
	}

	private Layer createGridLayer(Map map) {
		return doCreateGridLayer(LayerType.GRID, options.showGrid, map);
	}

	private Layer doCreateGridLayer(LayerType layerType,
			BooleanPrefModel isVisiblePreference, Map map) {
		return new Layer(layerType, isVisiblePreference, new GridDrawer(map),
				new DummyConstructor(), new DummyLoader());
	}

	private Layer createVillageLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.VILLAGE,
				options.showVillages, map, world.getVillageProducer());
	}

	private Layer createOceanMonumentLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.OCEAN_MONUMENT,
				options.showOceanMonuments, map,
				world.getOceanMonumentProducer());
	}

	private Layer createStrongholdLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.STRONGHOLD,
				options.showStrongholds, map, world.getStrongholdProducer());
	}

	private Layer createTempleLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.TEMPLE, options.showTemples,
				map, world.getTempleProducer());
	}

	private Layer createSpawnLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.SPAWN, options.showSpawn,
				map, world.getSpawnProducer());
	}

	private Layer createNetherFortressLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.NETHER_FORTRESS,
				options.showNetherFortresses, map,
				world.getNetherFortressProducer());
	}

	private Layer createPlayerLayer(World world, Map map) {
		return doCreateWorldObjectLayer(LayerType.PLAYER, options.showPlayers,
				map, world.getPlayerProducer());
	}

	private Layer doCreateWorldObjectLayer(LayerType layerType,
			BooleanPrefModel isVisiblePreference, Map map,
			WorldObjectProducer producer) {
		return new Layer(layerType, isVisiblePreference, new WorldObjectDrawer(
				map, layerType), new DummyConstructor(), new WorldObjectLoader(
				layerType, producer));
	}
}
