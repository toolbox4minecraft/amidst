package amidst.minetest.world.mapgen;

import amidst.documentation.GsonConstructor;
import amidst.gameengineabstraction.world.biome.BiomeBase;
import amidst.mojangapi.world.biome.BiomeColor;

public class MinetestBiome extends BiomeBase {
	
	public static MinetestBiome NONE = new MinetestBiome("None", -1, BiomeColor.from(0, 0, 0), (short)0, (short)0, 0, 0);

	protected MinetestBiome(String name, BiomeColor defaultColor, short y_min, short y_max, float heat_point, float humidity_point) {
		this(name, -1, defaultColor, y_min, y_max, heat_point, humidity_point);
	}
	
	protected MinetestBiome(String name, int index, BiomeColor defaultColor, short y_min, short y_max, float heat_point, float humidity_point) {
		super(name, index, defaultColor);
		
		this.y_min = y_min;
		this.y_max = y_max;
		this.heat_point = heat_point;
		this.humidity_point = humidity_point;
		this.vertical_blend = 0;
	}
		
	@GsonConstructor
	public MinetestBiome() {
	}
	
	public void setIndex(int index) {
		super.setIndex(index);
	}
	
	public short y_min;
	public short y_max;
	public float heat_point;
	public float humidity_point;
	public short vertical_blend;
}
