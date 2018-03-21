package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;

public class MapgenFlatParams extends MapgenParams {

	public static final int FLAG_FLAT_LAKES   = 0x01;
	public static final int FLAG_FLAT_HILLS   = 0x02;
	
	public int spflags = 0;
	public short ground_level = 8;

	short large_cave_depth = -33;
	short lava_depth = -256;
	float cave_width = 0.09f;
	float lake_threshold = -0.45f;
	float lake_steepness = 48.0f;
	float hill_threshold = 0.45f;
	float hill_steepness = 64.0f;
	short dungeon_ymin = -31000;
	short dungeon_ymax = 31000;

	NoiseParams np_terrain = new NoiseParams(0, 1,  new Vector3f(600, 600, 600), 7244, (short)5, 0.6f,  2.0f);
	
   	@Override
   	public String toString() {   		
   		String prefix = "mgflat_";
		StringBuilder result = new StringBuilder();
		result.append("mg_flags     = "); 
		appendFlags(result, spflags, new String[] {"lakes", "hills"}); 
		result.append("\r\n");
		result.append(String.format("ground_level = %s\r\n", formatFloat(ground_level))); 
		result.append(super.toString());
		result.append(np_terrain.toString(prefix + "np_terrain"));
        return result.toString();		
   	}		
	
	@Override
	public WorldType getWorldType() {
		return WorldType.FLAT;
	}
}
