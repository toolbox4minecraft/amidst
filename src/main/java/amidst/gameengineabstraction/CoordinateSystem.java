package amidst.gameengineabstraction;

import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

/**
 * Minecraft and Amidst use a right-handed coordinate system, this means
 * coordinates need to be converted if you want to use them in a
 * left-handed coordinate system.
 */
public enum CoordinateSystem {
	/**
	 * Minecraft and Amidst use a right-handed coordinate system
	 */
	RIGHT_HANDED,
	LEFT_HANDED;
	
	/**
	 * Converts the z value from a right-handed 3D coordinate system (i.e. what Amidst/Minecraft use)
	 * to whichever system the enum represents. x and y values are the same in both
	 * 3D coordinate systems and don't need to be converted.  
	 */
	public long ConvertFromRightHanded(long z) {
		// ConvertTo and ConvertFrom are identical because the conversion is symmetric, but
		// providing both makes the intent of the call clearer.
		return (this == RIGHT_HANDED) ? z : -z;
	}
	
	/**
	 * Converts the z value from whichever system the enum represents into a 
	 * right-handed 3D coordinate system (i.e. what Amidst/Minecraft use). x and y values 
	 * are the same in both 3D coordinate systems and don't need to be converted.  
	 */
	public long ConvertToRightHanded(long z) {
		// ConvertTo and ConvertFrom are identical because the conversion is symmetric, but
		// providing both makes the intent of the call clearer.
		return (this == RIGHT_HANDED) ? z : -z;
	}
	
	/**
	 * Converts the coords from a right-handed coordinate system (i.e. what Amidst/Minecraft use)
	 * to whichever system the enum represents. If both systems are the same, then the coords 
	 * argument is just returned.
	 */
	public CoordinatesInWorld ConvertFromRightHanded(CoordinatesInWorld coords) {
		// "In World" coords refer to the z axis as y
		// ConvertTo and ConvertFrom are identical because the conversion is symmetric, but
		// providing both makes the intent of the call clearer.
		return (this == RIGHT_HANDED) ? coords : new CoordinatesInWorld(coords.getX(), -coords.getY());
	}
	
	/**
	 * Converts the coords from whichever system the enum represents into a 
	 * right-handed coordinate system (i.e. what Amidst/Minecraft use). If both 
	 * systems are the same, then the coords argument is just returned.
	 */
	public CoordinatesInWorld ConvertToRightHanded(CoordinatesInWorld coords) {
		// "In World" coords refer to the z axis as y
		// ConvertTo and ConvertFrom are identical because the conversion is symmetric, but
		// providing both makes the intent of the call clearer.
		return (this == RIGHT_HANDED) ? coords : new CoordinatesInWorld(coords.getX(), -coords.getY());
	}
}
