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

import amidst.Options;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.Layer;
import amidst.map.MapObjectSpawn;
import amidst.map.MapObjectStronghold;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class SpawnLayer extends IconLayer {
	private MapObjectSpawn spawnObject;
	public static final ArrayList<Biome> validBiomes = new ArrayList<Biome>(Arrays.asList(
			Biome.forest, 
			Biome.plains, 
			Biome.taiga, 
			Biome.taigaHills, 
			Biome.forestHills, 
			Biome.jungle, 
			Biome.jungleHills
		));
	
	public SpawnLayer() {
		super("spawnPoint");
		setVisibilityPref(Options.instance.showSpawn);
		
	}
	
	public void generateMapObjects(Fragment frag) {
		if ((spawnObject.globalX >= frag.blockX) &&
			(spawnObject.globalX < frag.blockX + Fragment.SIZE) &&
			(spawnObject.globalY >= frag.blockY) &&
			(spawnObject.globalY < frag.blockY + Fragment.SIZE)) {
			spawnObject.parentLayer = this;
			frag.addObject(spawnObject);
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
	
	public void reload() {
		Point spawnCenter = getSpawnPosition();
		spawnObject = new MapObjectSpawn(spawnCenter.x, spawnCenter.y);
	}
	
}
