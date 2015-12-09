package amidst.preferences;

import amidst.documentation.GsonConstructor;
import amidst.utilities.ColorUtils;

public class BiomeColor {
	private int r;
	private int g;
	private int b;

	@GsonConstructor
	public BiomeColor() {
	}

	public BiomeColor(int rgb) {
		r = (rgb >> 16) & 0xFF;
		g = (rgb >> 8) & 0xFF;
		b = (rgb) & 0xFF;
	}

	public int toColorInt() {
		return ColorUtils.makeColor(r, g, b);
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}
}
