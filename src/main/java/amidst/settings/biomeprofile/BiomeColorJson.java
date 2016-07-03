package amidst.settings.biomeprofile;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.BiomeColor;

@Immutable
public class BiomeColorJson {
	private int r;
	private int g;
	private int b;

	@GsonConstructor
	public BiomeColorJson() {
	}

	public BiomeColorJson(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
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

	public BiomeColor createBiomeColor() {
		return BiomeColor.from(r, g, b);
	}
}
