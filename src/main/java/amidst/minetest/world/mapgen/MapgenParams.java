package amidst.minetest.world.mapgen;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
	
   	public String toString() {   		
   		String prefix = "mg_biome_";
		StringBuilder result = new StringBuilder();
		result.append(String.format("water_level = %d\r\n", water_level));
		result.append(np_heat.toString(          prefix + "np_heat"));
		result.append(np_humidity.toString(      prefix + "np_humidity"));
		result.append(np_heat_blend.toString(    prefix + "np_heat_blend"));
		result.append(np_humidity_blend.toString(prefix + "np_humidity_blend"));
        return result.toString();		
   	}	
   	
	public static void appendFlags(StringBuilder stringBuilder, int flagBits, String[] flags) {
		
		if (flagBits == 0) {
			stringBuilder.append("none");
		} else {		
			boolean commaRequired = false;
			int flagMask = 1;
			for(String flagname : flags) {
				if ((flagBits & flagMask) > 0) {
					if (commaRequired) stringBuilder.append(", ");
					stringBuilder.append(flagname);
					commaRequired = true;
				}
				flagMask <<= 1;
			}
		}
	}

	private static String decimalSeparator;
	private static String formatFloatRegEx = null;
	
	/**
	 * Formats a float value into the way it would be naturally written, e.g. 2.0f -> "2"
	 * 
	 * Used for [subclasses].toString(), but perhaps should be moved to a string library?
	 */
	public static String formatFloat(float value) {
		String result = String.format("%f", value);
	    
		if (formatFloatRegEx == null) {
			decimalSeparator = new DecimalFormatSymbols(Locale.getDefault(Locale.Category.FORMAT)).getDecimalSeparator() + "";
			formatFloatRegEx = "\\Q" + decimalSeparator + "\\E?0*$";
		}
	    return result.contains(decimalSeparator) ? result.replaceAll(formatFloatRegEx, "") : result;
	}
	
}
