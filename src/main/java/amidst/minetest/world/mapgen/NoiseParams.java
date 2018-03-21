package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

public class NoiseParams {

	public float offset = 0.0f;
	float scale = 1.0f;
	public Vector3f spread = new Vector3f(250, 250, 250);
	int seed = 12345;
	short octaves = 3;
	public float persist = 0.6f;
	float lacunarity = 2.0f;
	int flags = Noise.FLAG_DEFAULTS; // Static methods like compareUnsigned, divideUnsigned etc have been added to the Integer class to support the arithmetic operations for unsigned integers


	public NoiseParams() { }

	public NoiseParams(float offset_, float scale_, Vector3f spread_, int seed_,
			short octaves_, float persist_, float lacunarity_)
	{
		this(offset_, scale_, spread_, seed_, octaves_, persist_, lacunarity_, Noise.FLAG_DEFAULTS);
	}
	
	public String toString(String name) {
		StringBuilder result = new StringBuilder();
		result.append(name); 
		result.append(" = {\r\n");
		result.append("    flags       = "); 
		MapgenParams.appendFlags(result, flags, new String[] {"defaults", "eased", "absvalue", "pointbuffer", "simplex"});
		result.append("\r\n");
		result.append(String.format("    lacunarity  = %s\r\n", MapgenParams.formatFloat(lacunarity)));
		result.append(String.format("    offset      = %s\r\n", MapgenParams.formatFloat(offset)));
		result.append(String.format("    scale       = %s\r\n", MapgenParams.formatFloat(scale)));
		result.append(String.format("    spread      = (%s, %s, %s)\r\n", MapgenParams.formatFloat(spread.x), MapgenParams.formatFloat(spread.y), MapgenParams.formatFloat(spread.z)));
		result.append(String.format("    seed        = %s\r\n", MapgenParams.formatFloat(seed)));
		result.append(String.format("    octaves     = %s\r\n", MapgenParams.formatFloat(octaves)));
		result.append(String.format("    persistence = %s\r\n", MapgenParams.formatFloat(persist)));
		result.append("}\r\n");
        return result.toString();		
		
	}
	
	public NoiseParams(float offset_, float scale_, Vector3f spread_, int seed_,
		short octaves_, float persist_, float lacunarity_,
		int flags_)
	{
		offset     = offset_;
		scale      = scale_;
		spread     = spread_;
		seed       = seed_;
		octaves    = octaves_;
		persist    = persist_;
		lacunarity = lacunarity_;
		flags      = flags_;
	}
}
