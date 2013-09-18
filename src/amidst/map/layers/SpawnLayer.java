package amidst.map.layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import amidst.Log;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.Layer;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class SpawnLayer extends Layer {

	public static final ArrayList<Biome> validBiomes = new ArrayList<Biome>(Arrays.asList(
			Biome.forest, 
			Biome.plains, 
			Biome.taiga, 
			Biome.taigaHills, 
			Biome.forestHills, 
			Biome.jungle, 
			Biome.jungleHills
		));
	private static final Color drawColor = new Color(100, 120, 240, 100);
	private int drawFragX, drawFragY;
	private Point drawCorner;
	
	public SpawnLayer() {
		super("spawnLayer", null, 1.1f);
		setVisibilityPref(Options.instance.showGrid);
		
		Point spawnCenter = getSpawnPosition();
		drawFragX = spawnCenter.x >> Fragment.SIZE_SHIFT;
		drawFragY = spawnCenter.y >> Fragment.SIZE_SHIFT;
		int drawX = ((spawnCenter.x < 0)?Fragment.SIZE:0) + spawnCenter.x % Fragment.SIZE;
		int drawY = ((spawnCenter.y < 0)?Fragment.SIZE:0) + spawnCenter.y % Fragment.SIZE;
		drawCorner = new Point(drawX - 10, drawY - 10);
	}
	
	public void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat) {
		if ((fragment.getFragmentX() == drawFragX) && (fragment.getFragmentY() == drawFragY)) {

			g.setColor(drawColor);
			g.setTransform(mat);
			g.fillRect(drawCorner.x, drawCorner.y, 20, 20);
		}
	}
	
	private Point getSpawnPosition() {
		Random random = new Random(Options.instance.seed);
		Point location = MinecraftUtil.findValidLocation(0, 0, 256, validBiomes, random);
		int x = 0;
		int y = 0;
		if (location != null) {
			x = location.x;
			y = location.y;
		} else {
			Log.debug("Unable to find spawn biome.");
		}

		return new Point(x, y);
	}
	
}
