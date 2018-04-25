package amidst.minetest.world.mapgen;

public class Constants {

	// Dimension of a MapBlock
	// WARNING: A MineTest MapBlock is similar to a Minecraft Chunk, while
	// a Minetest Chunk is 5 x 5 x 5 MapBlocks!
	public static final int MAP_BLOCKSIZE = 16;	
	
	// The absolute working limit is (2^15 - viewing_range).
	// I really don't want to make every algorithm to check if it's going near
	// the limit or not, so this is lower.
	// This is the maximum value the setting map_generation_limit can be	
	public static final short MAX_MAP_GENERATION_LIMIT = 31000;	
	
}
