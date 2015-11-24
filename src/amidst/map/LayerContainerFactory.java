package amidst.map;

import amidst.map.layer.BiomeLayer;
import amidst.map.layer.GridLayer;
import amidst.map.layer.NetherFortressLayer;
import amidst.map.layer.OceanMonumentLayer;
import amidst.map.layer.PlayerLayer;
import amidst.map.layer.SlimeLayer;
import amidst.map.layer.SpawnLayer;
import amidst.map.layer.StrongholdLayer;
import amidst.map.layer.TempleLayer;
import amidst.map.layer.VillageLayer;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	public LayerContainer create(World world, Map map) {
		return new LayerContainer(new BiomeLayer(world, map), new SlimeLayer(
				world, map), new GridLayer(world, map), new VillageLayer(world,
				map), new OceanMonumentLayer(world, map), new StrongholdLayer(
				world, map), new TempleLayer(world, map), new SpawnLayer(world,
				map), new NetherFortressLayer(world, map), new PlayerLayer(
				world, map));
	}
}
