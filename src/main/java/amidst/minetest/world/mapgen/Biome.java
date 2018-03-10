package amidst.minetest.world.mapgen;

import amidst.gameengineabstraction.world.mapgen.BiomeBase;
import amidst.mojangapi.world.biome.BiomeColor;

public class Biome extends BiomeBase {
	
	public static Biome NONE = new Biome("None", 0, BiomeColor.from(0, 0, 0), (short)0, (short)0, 0, 0);
	
	protected Biome(String name, int index, BiomeColor defaultColor, short y_min, short y_max, float heat_point, float humidity_point) {
		super(name, index, defaultColor);
		
		this.y_min = y_min;
		this.y_max = y_max;
		this.heat_point = heat_point;
		this.humidity_point = humidity_point;
		this.vertical_blend = 0;
	}
		
	public short y_min;
	public short y_max;
	public float heat_point;
	public float humidity_point;
	public short vertical_blend;
}
