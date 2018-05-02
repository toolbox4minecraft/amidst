package amidst.mojangapi.world.biome;

import java.awt.Color;

import amidst.documentation.Immutable;
import amidst.settings.biomeprofile.BiomeColorJson;

@Immutable
public class BiomeColor {
	public static BiomeColor from(int r, int g, int b) {
		return new BiomeColor(r, g, b);
	}

	public static BiomeColor fromBiomeColorJson(BiomeColorJson json_color) {
		return new BiomeColor(json_color.getR(), json_color.getG(), json_color.getB());
	}
	
	public static BiomeColor unknown() {
		return UNKNOWN_BIOME_COLOR;
	}

	public static BiomeColor error() {
		return ERROR_BIOME_COLOR;
	}

	public static BiomeColor transparent() {
		return TRANSPARENT_BIOME_COLOR;
	}	
	
	private static final BiomeColor UNKNOWN_BIOME_COLOR     = new BiomeColor(0, 0, 0);
	private static final BiomeColor ERROR_BIOME_COLOR       = new BiomeColor(255, 0, 255);
	private static final BiomeColor TRANSPARENT_BIOME_COLOR = new BiomeColor(0, 0, 0, 0);

	private static final int DESELECT_NUMBER = 30;
	private static final int LIGHTEN_BRIGHTNESS = 40;

	private final int r;
	private final int g;
	private final int b;
	private final int rgb;
	private final int deselectRGB;
	private final Color color;

	
	private BiomeColor(int r, int g, int b) {
		this(r, g, b, 255);
	}	
	
	private BiomeColor(int r, int g, int b, int alpha) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.rgb = createRGB(r, g, b, alpha);
		this.deselectRGB = createDeselectRGB(r, g, b, alpha);
		this.color = new Color(r, g, b, alpha);
	}

	private int createRGB(int r, int g, int b, int alpha) {
		int result = 0xFF000000 & (alpha << 24);
		result |= 0xFF0000 & (r << 16);
		result |= 0xFF00 & (g << 8);
		result |= 0xFF & b;
		return result;
	}

	private int createDeselectRGB(int r, int g, int b, int alpha) {
		int sum = r + g + b;
		return createRGB(deselect(r, sum), deselect(g, sum), deselect(b, sum), alpha);
	}

	private int deselect(int x, int average) {
		return (x + average) / DESELECT_NUMBER;
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

	public int getDeselectRGB() {
		return deselectRGB;
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

	public BiomeColor blend(float secondColorWeight, BiomeColor secondColor) {
		secondColorWeight = Math.max(0, Math.min(1, secondColorWeight));
		float firstColorWeight = 1.0f - secondColorWeight;
		return BiomeColor.from(
			Math.round(r * firstColorWeight + secondColor.r * secondColorWeight),
			Math.round(g * firstColorWeight + secondColor.g * secondColorWeight),
			Math.round(b * firstColorWeight + secondColor.b * secondColorWeight)
		);
	}

	private int lighten(int x) {
		return Math.min(x + LIGHTEN_BRIGHTNESS, 0xFF);
	}
}
