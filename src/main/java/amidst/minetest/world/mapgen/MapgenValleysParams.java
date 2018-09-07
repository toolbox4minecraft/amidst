package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.biome.BiomeColor;

// TODO: be able to import these values from map_meta.txt files
public class MapgenValleysParams extends MapgenParams {

	public static final int FLAG_VALLEYS_ALT_CHILL         = 0x01;
	public static final int FLAG_VALLEYS_HUMID_RIVERS      = 0x02;
	public static final int FLAG_VALLEYS_VARY_RIVER_DEPTH  = 0x04;
	public static final int FLAG_VALLEYS_ALT_DRY           = 0x08;
		
	public int spflags = FLAG_VALLEYS_ALT_CHILL | FLAG_VALLEYS_HUMID_RIVERS;
	
	public NoiseParams np_cave1              = new NoiseParams(0,    12.0f, new Vector3f(61,   61,   61),   52534, (short)3, 0.5f,  2.0f);
	public NoiseParams np_cave2              = new NoiseParams(0,    12.0f, new Vector3f(67,   67,   67),   10325, (short)3, 0.5f,  2.0f);
	public NoiseParams np_filler_depth       = new NoiseParams(0,     1.2f, new Vector3f(256,  256,  256),   1605, (short)3, 0.5f,  2.0f);
	public NoiseParams np_inter_valley_fill  = new NoiseParams(0,     1.0f, new Vector3f(256,  512,  256),   1993, (short)6, 0.8f,  2.0f);
	public NoiseParams np_inter_valley_slope = new NoiseParams(0.5f,  0.5f, new Vector3f(128,  128,  128),    746, (short)1, 1.0f,  2.0f);
	public NoiseParams np_rivers             = new NoiseParams(0,     1.0f, new Vector3f(256,  256,  256),  -6050, (short)5, 0.6f,  2.0f);
	public NoiseParams np_massive_caves      = new NoiseParams(0,     1.0f, new Vector3f(768,  256,  768),  59033, (short)6, 0.63f, 2.0f);
	public NoiseParams np_terrain_height     = new NoiseParams(-10,  50.0f, new Vector3f(1024, 1024, 1024),  5202, (short)6, 0.4f,  2.0f);
	public NoiseParams np_valley_depth       = new NoiseParams(5,     4.0f, new Vector3f(512,  512,  512),  -1914, (short)1, 1.0f,  2.0f);
	public NoiseParams np_valley_profile     = new NoiseParams(0.6f,  0.5f, new Vector3f(512,  512,  512),    777, (short)1, 1.0f,  2.0f);
	       
	public short large_cave_depth   =    -33;
	public short massive_cave_depth =   -256; // highest altitude of massive caves
	public short altitude_chill     =     90; // The altitude at which temperature drops by 20C.
	public short lava_features      =      0; // How often water will occur in caves.
	public short river_depth        =      4; // How deep to carve river channels.
	public short river_size         =      5; // How wide to make rivers.
	public short water_features     =      0; // How often water will occur in caves.
	public float cave_width         =    0.09f;
	public short dungeon_ymin       = -31000;
	public short dungeon_ymax       =     63; // No higher than surface mapchunks

	
   	@Override
   	public String toString() {   		
   		String prefix = "mgvalleys_";
		StringBuilder result = new StringBuilder();
		result.append(prefix + "spflags = "); 
		appendFlags(result, spflags, new String[] {"altitude_chill", "humid_rivers"}); 
		result.append("\r\n");
		result.append(super.toString());
		/* TODO
		result.append(np_base.toString(         prefix +"np_base"));
		result.append(np_height1.toString(      prefix +"np_height1"));
		result.append(np_height2.toString(      prefix +"np_height2"));
		result.append(np_height3.toString(      prefix +"np_height3"));
		result.append(np_height4.toString(      prefix +"np_height4"));
		result.append(np_hills_terrain.toString(prefix +"np_hills_terrain"));
		result.append(np_ridge_terrain.toString(prefix +"np_ridge_terrain"));
		result.append(np_step_terrain.toString( prefix +"np_step_terrain"));
		result.append(np_hills.toString(        prefix +"np_hills"));
		result.append(np_ridge_mnt.toString(    prefix +"np_ridge_mnt"));
		result.append(np_step_mnt.toString(     prefix +"np_step_mnt"));
		result.append(np_mnt_var.toString(      prefix +"np_mnt_var"));
		*/
        return result.toString();		
   	}
	       
   	@Override
	public WorldType getWorldType() {
		return WorldType.VALLEYS;
	}		       
}
