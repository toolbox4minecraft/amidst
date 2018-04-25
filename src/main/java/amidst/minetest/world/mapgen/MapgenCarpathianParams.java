package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.biome.BiomeColor;

// TODO: be able to import these values from map_meta.txt files
public class MapgenCarpathianParams extends MapgenParams {

	public static final int FLAG_CARPATHIAN_CAVERNS     = 0x01;
		
	public int spflags = FLAG_CARPATHIAN_CAVERNS;
	
	public NoiseParams np_base          = new NoiseParams(12, 1,  new Vector3f(2557, 2557, 2557), 6538,  (short)4, 0.8f,  0.5f);
	public NoiseParams np_height1       = new NoiseParams(0,  5,  new Vector3f(251,  251,  251),  9613,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_height2       = new NoiseParams(0,  5,  new Vector3f(383,  383,  383),  1949,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_height3       = new NoiseParams(0,  5,  new Vector3f(509,  509,  509),  3211,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_height4       = new NoiseParams(0,  5,  new Vector3f(631,  631,  631),  1583,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_hills_terrain = new NoiseParams(1,  1,  new Vector3f(1301, 1301, 1301), 1692,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_ridge_terrain = new NoiseParams(1,  1,  new Vector3f(1889, 1889, 1889), 3568,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_step_terrain  = new NoiseParams(1,  1,  new Vector3f(1889, 1889, 1889), 4157,  (short)5, 0.5f,  2.0f);
	public NoiseParams np_hills         = new NoiseParams(0,  3,  new Vector3f(257,  257,  257),  6604,  (short)6, 0.5f,  2.0f);
	public NoiseParams np_ridge_mnt     = new NoiseParams(0,  12, new Vector3f(743,  743,  743),  5520,  (short)6, 0.7f,  2.0f);
	public NoiseParams np_step_mnt      = new NoiseParams(0,  8,  new Vector3f(509,  509,  509),  2590,  (short)6, 0.6f,  2.0f);
	public NoiseParams np_mnt_var       = new NoiseParams(0,  1,  new Vector3f(499,  499,  499),  2490,  (short)5, 0.55f, 2.0f);
	       
	       
   	@Override
   	public String toString() {   		
   		String prefix = "mgcarpathian_";
		StringBuilder result = new StringBuilder();
		result.append("mgcarpathian_spflags = "); 
		appendFlags(result, spflags, new String[] {"caverns"}); 
		result.append("\r\n");
		result.append(super.toString());
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
        return result.toString();		
   	}
	       
   	@Override
	public WorldType getWorldType() {
		return WorldType.CARPATHIAN;
	}		       
}
