package amidst.minecraft;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import amidst.Log;

import MoF.Biome;

public class MinecraftUtil {
	public static int[] getBiomeData(int x, int y, int width, int height) {
		Minecraft.getActiveMinecraft().getClassByName("IntCache").callFunction("resetIntCache");
		return (int[])Minecraft.getActiveMinecraft().getGlobal("biomeGen").callFunction("getInts", x, y, width, height);
	}
	public static Point findValidLocation(int x, int y, int size, List<Biome> paramList, Random random) {
		// TODO: Clean up this code
		int i = x - size >> 2;
		int j = y - size >> 2;
		int k = x + size >> 2;
		int m = y + size >> 2;
		
		int n = k - i + 1;
		int i1 = m - j + 1;
		int[] arrayOfInt = getBiomeData(i, j, n, i1);
		Point localPoint = null;
		int i2 = 0;
		for (int i3 = 0; i3 < n*i1; i3++) {
			int i4 = i + i3 % n << 2;
			int i5 = j + i3 / n << 2;
			if (arrayOfInt[i3] > Biome.a.length) {
				Log.kill("Unsupported biome type detected");
			}
			Biome localBiome = Biome.a[arrayOfInt[i3]];
			if ((!paramList.contains(localBiome)) || (
					(localPoint != null) && (random.nextInt(i2 + 1) != 0))) continue;
				localPoint = new Point(i4, i5);
				i2++;
		}
		
		return localPoint;
	}
	public static boolean isValidBiome(int x, int y, int size, List<Biome> validBiomes) {
		int i = x - size >> 2;
		int j = y - size >> 2;
		int k = x + size >> 2;
		int m = y + size >> 2;
		
		int n = k - i + 1;
		int i1 = m - j + 1;
		
		int[] arrayOfInt = getBiomeData(i, j, n, i1);
		for (int i2 = 0; i2 < n * i1; i2++) {
			Biome localBiome = Biome.a[arrayOfInt[i2]];
			if (!validBiomes.contains(localBiome)) return false;
		}
		
		return true;
	}
}
