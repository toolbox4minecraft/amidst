package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.preferences.PrefModel;

public class SlimeLayer extends ImageLayer {
	private static final int SLIME_CHUNK_COLOR = 0xA0FF00FF;
	private static final int NOT_SLIME_CHUNK_COLOR = 0x00000000;

	public SlimeLayer(World world, Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		super(world, map, layerType, isVisiblePreference, Resolution.CHUNK);
	}

	@Override
	protected int getColorAt(Fragment fragment, long cornerX, long cornerY,
			int x, int y) {
		if (world.getSlimeChunkOracle().isSlimeChunk(cornerX + x, cornerY + y)) {
			return SLIME_CHUNK_COLOR;
		} else {
			return NOT_SLIME_CHUNK_COLOR;
		}
	}
}
