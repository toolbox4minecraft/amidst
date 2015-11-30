package amidst.map;

import amidst.Options;
import amidst.map.layer.BiomeLayer;
import amidst.map.layer.GridLayer;
import amidst.map.layer.WorldObjectLayer;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.map.layer.SlimeLayer;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private World world;
	private Map map;

	public synchronized LayerContainer create(World world, Map map) {
		this.world = world;
		this.map = map;
		return new LayerContainer(createBiomeLayer(), createSlimeLayer(),
				createGridLayer(), createVillageLayer(),
				createOceanMonumentLayer(), createStrongholdLayer(),
				createTempleLayer(), createSpawnLayer(),
				createNetherFortressLayer(), createPlayerLayer());
	}

	private Layer createBiomeLayer() {
		return new BiomeLayer(world, map);
	}

	private Layer createSlimeLayer() {
		return new SlimeLayer(world, map);
	}

	private Layer createGridLayer() {
		return new GridLayer(world, map);
	}

	private Layer createVillageLayer() {
		return new WorldObjectLayer(world, map, LayerType.VILLAGE,
				Options.instance.showVillages, world.getVillageProducer());
	}

	private Layer createOceanMonumentLayer() {
		return new WorldObjectLayer(world, map, LayerType.OCEAN_MONUMENT,
				Options.instance.showOceanMonuments,
				world.getOceanMonumentProducer());
	}

	private Layer createStrongholdLayer() {
		return new WorldObjectLayer(world, map, LayerType.STRONGHOLD,
				Options.instance.showStrongholds, world.getStrongholdProducer());
	}

	private Layer createTempleLayer() {
		return new WorldObjectLayer(world, map, LayerType.TEMPLE,
				Options.instance.showTemples, world.getTempleProducer());
	}

	private Layer createSpawnLayer() {
		return new WorldObjectLayer(world, map, LayerType.SPAWN,
				Options.instance.showSpawn, world.getSpawnProducer());
	}

	private Layer createNetherFortressLayer() {
		return new WorldObjectLayer(world, map, LayerType.NETHER_FORTRESS,
				Options.instance.showNetherFortresses,
				world.getNetherFortressProducer());
	}

	private Layer createPlayerLayer() {
		return new WorldObjectLayer(world, map, LayerType.PLAYER,
				Options.instance.showPlayers, world.getPlayerProducer());
	}
}
