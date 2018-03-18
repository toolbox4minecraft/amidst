package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;

public class MapgenV5Params extends MapgenParams {

	public static final int FLAG_V5_CAVERNS   = 0x01;
	
	int spflags = FLAG_V5_CAVERNS;
	float cave_width       = 0.125f;
	short large_cave_depth = -256;
	short lava_depth       = -256;
	short cavern_limit     = -256;
	short cavern_taper     = 256;
	float cavern_threshold = 0.7f;
	short dungeon_ymin     = -31000;
	short dungeon_ymax     = 31000;
	
	public NoiseParams np_factor = new NoiseParams(0, 1,  new Vector3f(250, 250, 250), 920381, (short)3, 0.45f,  2.0f);
	public NoiseParams np_height = new NoiseParams(0, 10, new Vector3f(250, 250, 250), 84174,  (short)4, 0.5f,   2.0f);
	public NoiseParams np_ground = new NoiseParams(0, 40, new Vector3f( 80,  80,  80), 983240, (short)4, 0.55f,  2.0f, Noise.FLAG_EASED);
	
	@Override
	public WorldType getWorldType() {
		return WorldType.V5;
	}	
}
