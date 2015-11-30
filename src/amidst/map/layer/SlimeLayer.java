package amidst.map.layer;

import amidst.map.Map;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.preferences.PrefModel;

public class SlimeLayer extends ImageLayer {
	public SlimeLayer(World world, Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		super(world, map, layerType, isVisiblePreference,
				new SlimeColorProvider(world), Resolution.CHUNK);
	}
}
