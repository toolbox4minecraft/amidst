package amidst.mojangapi.world.biome;

import java.awt.Color;

import amidst.documentation.Immutable;
import amidst.settings.biomeprofile.BiomeColorJson;

@Immutable
public class BiomeColor {
	public static BiomeColor from(int r, int g, int b) {
		return new BiomeColor(r, g, b);
	}

	public static BiomeColor unknown() {
		return UNKNOWN_BIOME_COLOR;
	}

	private static final BiomeColor UNKNOWN_BIOME_COLOR = new BiomeColor(0, 0, 0);

	private static final int HIDDEN_NUMBER = 30;
	private static final int LIGHTEN_BRIGHTNESS = 40;

	private final int r;
	private final int g;
	private final int b;
	private final int rgb;
	private final int hiddenRGB;
	private final Color color;

	private BiomeColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.rgb = createRGB(r, g, b);
		this.hiddenRGB = createHiddenRGB(r, g, b);
		this.color = new Color(r, g, b);
	}

	private int createRGB(int r, int g, int b) {
		int result = 0xFF000000;
		result |= 0xFF0000 & (r << 16);
		result |= 0xFF00 & (g << 8);
		result |= 0xFF & b;
		return result;
	}

	private int createHiddenRGB(int r, int g, int b) {
		int sum = r + g + b;
		return createRGB(hide(r, sum), hide(g, sum), hide(b, sum));
	}

	private int hide(int x, int average) {
		return (x + average) / HIDDEN_NUMBER;
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

	public int getRGB() {
		return rgb;
	}

	public int getHiddenRGB() {
		return hiddenRGB;
	}

	public Color getColor() {
		return color;
	}

	public BiomeColorJson createBiomeColorJson() {
		return new BiomeColorJson(r, g, b);
	}

	public BiomeColor createLightenedBiomeColor() {
		return BiomeColor.from(lighten(r), lighten(g), lighten(b));
	}

	private int lighten(int x) {
		return Math.min(x + LIGHTEN_BRIGHTNESS, 0xFF);
	}
}
