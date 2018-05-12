package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

public class NoiseParams {

	/**
	 * 'offset' is the centre value of the noise value, often 0.
     * (https://forum.minetest.net/viewtopic.php?p=227726#p227726)
	 */
	public float offset = 0.0f;
	
	/**
	 * 'scale' is the approximate variation of the noise value either side of the centre value, often 1.
     * Depending on the number of octaves and the persistence the variation is usually 1 to 2 times 
     * the scale value. The exact variation can be calculated from octaves and persistence.
     * (To be exact, scale is the variation of octave 1, additional octaves add extra variation, see below).
     * (https://forum.minetest.net/viewtopic.php?p=227726#p227726)
	 */
	float scale = 1.0f;
	
	/**
	 * 'spread' is the size in nodes of the largest scale variation. If the noise is terrain height 
	 * this would be the approximate size of the largest islands or seas, there will be no structure 
	 * on a larger scale than this.
     * There are 3 values for x, y, z so you can stretch or squash the shape of your structures, 
     * normally you would set all 3 values to be the same, even for 2D noise where the y value 
     * is not used).
     * (https://forum.minetest.net/viewtopic.php?p=227726#p227726)
	 */
	public Vector3f spread = new Vector3f(250, 250, 250);
	
	/**
	 * 'seed' is the magic seed number that determines the entire noise pattern.
     * Just type in any random number, different for each use of noise.
     * This is actually a 'seed difference', the noise actually uses 'seed' + 'world seed', to make any noise pattern world-dependant and repeatable.
     * 'seed' then makes sure each use of noise in a world is using a different pattern.
     * (https://forum.minetest.net/viewtopic.php?p=227726#p227726)
	 */
	int seed = 12345;
	
	/**
	 * 'octaves' is the number of levels of detail in the noise variation.
     * The largest scale variation is 'spread', that is octave 1. Each additional octave adds 
     * variation on a scale one half the size, so here you will have variation on scales 
     * of 2048, 1024, 512 nodes.
     * (https://forum.minetest.net/viewtopic.php?p=227726#p227726)
	 */
	short octaves = 3;
	
	/**
	 * 'persist' is persistence. This is how large the variation of each additional octave is relative to 
	 *  the previous octave.
	 *  Octave 1 always outputs a variation from -1 to 1, the 'amplitude' is 1.
     *  With 3 octaves persist 0.5, a much used standard noise:
     *     Octave 2 outputs a variation from -0.5 to 0.5 (amplitude 0.5).
     *     Octave 3 will output -0.25 to 0.25 (amplitude 0.5 x 0.5).
     *  The 3 octaves are added to result in a noise variation of -1.75 to 1.75.
     *  'persist' is therefore the roughness of the noise pattern, 0.33 is fairly smooth, 0.67 is rough 
     *  and spiky, 0.5 is a medium value.
     *  (https://forum.minetest.net/viewtopic.php?p=227726#p227726)
	 */
	public float persist = 0.6f;
	
	/**
	 *  lacunarity is the ratio of the scales of variation of adjacent octaves.
     *  Standard lacunarity is 2.0, where each additional octave creates variation on a scale 1/2 that 
     *  of the previous octave (which is why they're called octaves, it's analogous to musical octaves 
     *  which is a doubling of frequency).
     *  
     *  Lacunarity 3.0 means that if the 'spread' (the scale of variation of octave 1) is 900 nodes, 
     *  then the 2nd octave creates variation on a scale of 300 nodes, the 3rd octave 100 nodes etc. 
     *  It's a way to get a wider range of detail with the same number of octaves. it also has a 
     *  different character.
     *  (https://forum.minetest.net/viewtopic.php?p=228165#p228165)
	 */
	float lacunarity = 2.0f;
	
	
	int flags = Noise.FLAG_DEFAULTS; // Static methods like compareUnsigned, divideUnsigned etc have been added to the Integer class to support the arithmetic operations for unsigned integers
	
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
	
	public NoiseParams() { }

	public NoiseParams(float offset_, float scale_, Vector3f spread_, int seed_,
			short octaves_, float persist_, float lacunarity_)
	{
		this(offset_, scale_, spread_, seed_, octaves_, persist_, lacunarity_, Noise.FLAG_DEFAULTS);
	}

	/**
	 * 
	 * @param offset_
	 * @param scale_
	 * @param spread_
	 * @param seed_
	 * @param octaves_
	 * @param persist_ see field comment at top of this file for explanation.
	 * @param lacunarity_ see field comment at top of this file for explanation. 
	 * @param flags_ 
	 */
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
