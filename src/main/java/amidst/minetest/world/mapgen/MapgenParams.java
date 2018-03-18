package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;

public abstract class MapgenParams {

	public short water_level = 1; // from MapgenParams	
	int chunksize_in_mapblocks = 5;
	
	public int chunk_length_x = chunksize_in_mapblocks * Constants.MAP_BLOCKSIZE;
	public int chunk_length_y = chunk_length_x;
	public int chunk_length_z = chunk_length_x;
	
	
	// BiomeParamsOriginal
	public NoiseParams np_heat           = new NoiseParams(50,   50, new Vector3f(1000, 1000, 1000),  5349, (short)3, 0.5f, 2.0f);
	public NoiseParams np_humidity       = new NoiseParams(50,   50, new Vector3f(1000, 1000, 1000),   842, (short)3, 0.5f, 2.0f);
	public NoiseParams np_heat_blend     = new NoiseParams( 0, 1.5f, new Vector3f(   8,    8,    8),    13, (short)2, 1.0f, 2.0f);
	public NoiseParams np_humidity_blend = new NoiseParams( 0, 1.5f, new Vector3f(   8,    8,    8), 90003, (short)2, 1.0f, 2.0f);

	abstract WorldType getWorldType();
}
