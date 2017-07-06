package amidst.settings.biomeprofile;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.BiomeColor;

@Immutable
@GsonObject
public class BiomeColorJson {
	private int r;
	private int g;
	private int b;

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
