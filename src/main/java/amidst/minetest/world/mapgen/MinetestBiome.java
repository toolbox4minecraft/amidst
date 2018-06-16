package amidst.minetest.world.mapgen;

import amidst.documentation.GsonConstructor;
import amidst.gameengineabstraction.world.biome.BiomeBase;
import amidst.mojangapi.world.biome.BiomeColor;

public class MinetestBiome extends BiomeBase {
	
	// The index for out-of-bounds biomes must be (Short.MIN_VALUE + x) rather than -x, as values  
	// like -1 would be destroyed by applying a bitplane mask. 
	public static MinetestBiome NONE = new MinetestBiome("None", Short.MIN_VALUE + 1, BiomeColor.from(0, 0, 0), (short)0, (short)0, 0, 0);
	public static MinetestBiome VOID = new MinetestBiome("Air", Short.MIN_VALUE + 2, BiomeColor.transparent(), (short)0, (short)0, 0, 0);

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
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!MinetestBiome.class.isAssignableFrom(obj.getClass())) return false;
	    final MinetestBiome other = (MinetestBiome)obj;
	    if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) return false;
	    if ((this.getDefaultColor() == null) ? (other.getDefaultColor() != null) : !this.getDefaultColor().equals(other.getDefaultColor())) return false;
	    
	    return  y_min == other.y_min &&
	    		y_max == other.y_max &&
	    		heat_point == other.heat_point &&
	    		humidity_point == other.humidity_point &&
	    		vertical_blend == other.vertical_blend;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 37 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
	    hash = 37 * hash + (this.getDefaultColor() != null ? this.getDefaultColor().hashCode() : 0);
	    hash = 37 * hash + (int)y_min;
	    hash = 37 * hash + (int)y_max;
	    hash = 37 * hash + (int)Float.floatToIntBits(heat_point);
	    hash = 37 * hash + (int)Float.floatToIntBits(humidity_point);
	    hash = 37 * hash + (int)vertical_blend;
	    return hash;
	}	
	
	public short y_min;
	public short y_max;
	public float heat_point;
	public float humidity_point;
	public short vertical_blend;
}
