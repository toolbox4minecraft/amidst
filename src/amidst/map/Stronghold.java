package amidst.map;

import java.awt.*;

/** Used mainly to be override its toString method for use in choices
 */
public class Stronghold extends Point {
	public Stronghold(int x, int y) {
		super(x, y);
	}
	
	@Override
	public String toString() {
		return "Stronghold at (" + x + ", " + y + ")";
	}
}
