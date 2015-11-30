package amidst.map;

import amidst.Options;
import amidst.map.layer.BiomeLayer;
import amidst.map.layer.GridLayer;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.map.layer.SlimeLayer;
import amidst.map.layer.WorldObjectLayer;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private Options options;

	public LayerContainerFactory(Options options) {
		this.options = options;
	}

	public LayerContainer create(World world, Map map) {
		// @formatter:off
		return new LayerContainer(
				createBiomeLayer(world, map),
				createSlimeLayer(world, map),
				createGridLayer(world, map),
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
		return new BiomeLayer(world, map, LayerType.BIOME,
				options.alwaysTruePreference);
	}

	private Layer createSlimeLayer(World world, Map map) {
		return new SlimeLayer(world, map, LayerType.SLIME,
				options.showSlimeChunks);
	}

	private Layer createGridLayer(World world, Map map) {
		return new GridLayer(world, map, LayerType.GRID, options.showGrid);
	}

	private Layer createVillageLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.VILLAGE,
				options.showVillages, world.getVillageProducer());
	}

	private Layer createOceanMonumentLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.OCEAN_MONUMENT,
				options.showOceanMonuments, world.getOceanMonumentProducer());
	}

	private Layer createStrongholdLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.STRONGHOLD,
				options.showStrongholds, world.getStrongholdProducer());
	}

	private Layer createTempleLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.TEMPLE,
				options.showTemples, world.getTempleProducer());
	}

	private Layer createSpawnLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.SPAWN,
				options.showSpawn, world.getSpawnProducer());
	}

	private Layer createNetherFortressLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.NETHER_FORTRESS,
				options.showNetherFortresses, world.getNetherFortressProducer());
	}

	private Layer createPlayerLayer(World world, Map map) {
		return new WorldObjectLayer(world, map, LayerType.PLAYER,
				options.showPlayers, world.getPlayerProducer());
	}
}
