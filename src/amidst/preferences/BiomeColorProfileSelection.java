package amidst.preferences;

import java.awt.Color;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.world.Biome;

@ThreadSafe
public class BiomeColorProfileSelection {
	private volatile int[] colorInts;
	private volatile Color[] colors;

	public BiomeColorProfileSelection(BiomeColorProfile defaultProfile) {
		setProfile(defaultProfile);
	}

	public int getColorIntByBiomeIndex(int index) {
		return colorInts[index];
	}

	public Color getColorByBiome(Biome biome) {
		return colors[biome.getIndex()];
	}

	public void setProfile(BiomeColorProfile profile) {
		this.colorInts = profile.createColorArray();
		this.colors = createColorArray(colorInts);
		Log.i("Biome color profile activated.");
	}

	private Color[] createColorArray(int[] colorInts) {
		Color[] result = new Color[colorInts.length];
		for (int i = 0; i < colorInts.length; i++) {
			result[i] = new Color(colorInts[i]);
		}
		return result;
	}
}
