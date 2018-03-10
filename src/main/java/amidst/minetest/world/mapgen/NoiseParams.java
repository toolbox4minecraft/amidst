package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

public class NoiseParams {

	float offset = 0.0f;
	float scale = 1.0f;
	Vector3f spread = new Vector3f(250, 250, 250);
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
