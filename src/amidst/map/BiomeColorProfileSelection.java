package amidst.map;

import java.awt.Color;

import amidst.logging.Log;
import amidst.minecraft.Biome;
import amidst.preferences.BiomeColorProfile;

public class BiomeColorProfileSelection {
	private volatile int[] colorInts;
	private volatile Color[] colors;

	public int getColorIntByBiomeIndex(int index) {
		return colorInts[index];
	}

	public Color getColorByBiome(Biome biome) {
		return colors[biome.getIndex()];
	}

	public void setProfile(BiomeColorProfile profile) {
		this.colorInts = profile.createColorArray();
		this.colors = createColorsArray(colorInts);
		Log.i("Biome color profile activated.");
	}

	private Color[] createColorsArray(int[] colorInts) {
		Color[] result = new Color[colorInts.length];
		for (int i = 0; i < colorInts.length; i++) {
			result[i] = new Color(colorInts[i]);
		}
		return result;
	}
}
