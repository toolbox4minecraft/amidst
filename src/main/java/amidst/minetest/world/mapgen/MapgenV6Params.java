package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;

public class MapgenV6Params extends MapgenParams {

	public static final int FLAG_V6_JUNGLES    = 0x01;
	public static final int FLAG_V6_BIOMEBLEND = 0x02;
	public static final int FLAG_V6_MUDFLOW    = 0x04;
	public static final int FLAG_V6_SNOWBIOMES = 0x08;
	public static final int FLAG_V6_FLAT       = 0x10;
	public static final int FLAG_V6_TREES      = 0x20;
		
	public int spflags = FLAG_V6_JUNGLES | FLAG_V6_SNOWBIOMES | FLAG_V6_TREES |
			FLAG_V6_BIOMEBLEND | FLAG_V6_MUDFLOW;	
	
	public float freq_desert  = 0.45f;
	public float freq_beach   = 0.15f;
	short dungeon_ymin = -31000;
	short dungeon_ymax = 31000;
	
	public NoiseParams np_terrain_base   = new NoiseParams(-4,   20.0f, new Vector3f(250, 250, 250),  82341, (short)5,  0.6f, 2.0f);
	public NoiseParams np_terrain_higher = new NoiseParams(20,   16.0f, new Vector3f(500, 500, 500),  85039, (short)5,  0.6f, 2.0f);
	public NoiseParams np_steepness      = new NoiseParams(0.85f, 0.5f, new Vector3f(125, 125, 125),   -932, (short)5,  0.7f, 2.0f);
	public NoiseParams np_height_select  = new NoiseParams(0,     1.0f, new Vector3f(250, 250, 250),   4213, (short)5, 0.69f, 2.0f);
	public NoiseParams np_mud            = new NoiseParams(4,     2.0f, new Vector3f(200, 200, 200),  91013, (short)3, 0.55f, 2.0f);
	public NoiseParams np_beach          = new NoiseParams(0,     1.0f, new Vector3f(250, 250, 250),  59420, (short)3, 0.50f, 2.0f);
	public NoiseParams np_biome          = new NoiseParams(0,     1.0f, new Vector3f(500, 500, 500),   9130, (short)3, 0.50f, 2.0f);
	public NoiseParams np_cave           = new NoiseParams(6,     6.0f, new Vector3f(250, 250, 250),  34329, (short)3, 0.50f, 2.0f);
	public NoiseParams np_humidity       = new NoiseParams(0.5f,  0.5f, new Vector3f(500, 500, 500),  72384, (short)3, 0.50f, 2.0f);
	public NoiseParams np_trees          = new NoiseParams(0,     1.0f, new Vector3f(125, 125, 125),      2, (short)4, 0.66f, 2.0f);
	public NoiseParams np_apple_trees    = new NoiseParams(0,     1.0f, new Vector3f(100, 100, 100), 342902, (short)3, 0.45f, 2.0f);
	
	@Override
	public WorldType getWorldType() {
		return WorldType.V6;
	}	
}
