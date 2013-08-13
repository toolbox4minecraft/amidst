package MoF;



import amidst.Log;
import amidst.foreign.VersionInfo;
import amidst.map.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class ChunkManager
{
	/*
	private Object b;
	public float[] a;
	public long seed;
	public MapObjectStronghold[] strongholds;
	private Stack<Fragment> queue;
	private MapGenVillage villageFinder;
	private MapGenNetherhold netherholdFinder;
	private MapGenPyramid pyramidFinder;
	private boolean active;
	private static Class<?> iBiome, iCache, s12w03a;
	private Method getData, clearCache;
	private List<MapObjectPlayer> players;
	private static boolean firstRun = true;
	public ChunkManager(long seed) {
		try {
			if (firstRun) {
				iBiome = ClassLoader.getSystemClassLoader().loadClass(ReflectionInfo.instance.chunkName);
				iCache = ClassLoader.getSystemClassLoader().loadClass(ReflectionInfo.instance.version.intCacheName);
				Type t = null;
				if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V12w03a)) {
					Method cs[] = iBiome.getDeclaredMethods();
					for (Method c : cs) {
						Class<?>[] types = c.getParameterTypes();
						
						if ((types.length == 2) && (types[1] != long.class)) {
							t = types[1];
						}
					}
					Log.debug("Err:", t);
					assert t != null;
					s12w03a = ClassLoader.getSystemClassLoader().loadClass(t.toString().split(" ")[1]);
				}
				
				firstRun = false;
			}
			Object[] ret;
			Method init;
			clearCache = iCache.getDeclaredMethod("a");
			if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V12w03a)) {
				init = iBiome.getDeclaredMethod("a", Long.TYPE, s12w03a);
				String genString = "b";
				if (SaveLoader.genType == SaveLoader.Type.FLAT)
					genString = "c";
				else if (SaveLoader.genType == SaveLoader.Type.LARGE_BIOMES)
					genString = "d";
				Log.debug("GenString:", genString);
				ret = (Object[])init.invoke(null, seed, s12w03a.getField(genString).get(null));
			} else {
				init = iBiome.getDeclaredMethod("a", Long.TYPE);
				ret = (Object[])init.invoke(null, seed);
			}
			this.b = ret[0];
			
			getData = iBiome.getMethod("a", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.seed = seed;
		strongholds = new MapGenStronghold().a(seed, this);
		queue = new Stack<Fragment>();
		players = new ArrayList<MapObjectPlayer>();
		active = true;
		villageFinder = new MapGenVillage();
		netherholdFinder = new MapGenNetherhold(seed);
		pyramidFinder = new MapGenPyramid();
	}
	
	public void dispose() {
		Log.debug("DISPOSING OF CHUNKMANAGER");
		this.active = false;
		this.b = null;
		this.strongholds = null;
		this.queue.clear();
		this.queue = null;
		this.villageFinder = null;
		this.getData = null;
		this.clearCache = null;
		this.players.clear();
		this.players = null;
		this.netherholdFinder = null;
	}
	
	public int[] ba(int x, int y, int c, int d) {
		try {
			clearCache.invoke(iCache);
			
			return (int[]) getData.invoke(this.b, x,y,c,d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public byte[] getBiomeForArea(int x, int y, int range) {
		byte[] barr = new byte[range*range];
		int[] temp = ba(x*Project.FRAGMENT_SIZE, y*Project.FRAGMENT_SIZE, range, range);
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte)temp[i];
		}
		
		return barr;
	}
	
	private void getPlayerData(Fragment frag) {
		for (MapObjectPlayer p : players) {
			if (frag.isInside(p.x, p.y)) {
				frag.addMapObject(p);
			}
		}
	}
	public void addPlayer(MapObjectPlayer p) { 
		players.add(p);
	}
	public void setPlayerData(List<MapObjectPlayer> ar) {
		players = ar;
	}
	
	private void getSlimeData(Fragment frag) {
		Graphics2D g2d = frag.newLayer();
		int fs = Project.FRAGMENT_SIZE >> 2;
		for (int y = 0; y < fs; y++) {
			for (int x = 0; x < fs; x++) {
				int xPosition = (frag.x*fs) + (x);
				int zPosition = (frag.y*fs) + (y);
				Random rnd = new Random(seed + (long) (xPosition * xPosition * 0x4c1906) + (long) (xPosition * 0x5ac0db) +
			             (long) (zPosition * zPosition) * 0x4307a7L + (long) (zPosition * 0x5f24f) ^ 0x3ad8025f);
				if (rnd.nextInt(10) == 0) {
					g2d.drawImage(MapMarkers.SLIME.image,
							x << 2,
							y << 2,
							4, 4, null);
				}
			}
		}
		g2d.dispose();
	}
	
	private void getNetherholdData(Fragment frag) {
		int fs = Project.FRAGMENT_SIZE >> 2;
		int ls = Project.FRAGMENT_SIZE << 2;
		for (int y = 0; y < fs; y++) {
			for (int x = 0; x < fs; x++) {
				if (netherholdFinder.checkChunk((frag.x*fs) + (x), (frag.y*fs) + (y))) {
					frag.addMapObject(new MapObjectNether((frag.x*ls) + (x << 4), (frag.y*ls) + (y << 4)));
				}
			}
		}
	}
	private void getPyramidData(Fragment frag) {
		int fs = Project.FRAGMENT_SIZE >> 2;
		int ls = Project.FRAGMENT_SIZE << 2;
		for (int y = 0; y < fs; y++) {
			for (int x = 0; x < fs; x++) {
				if (pyramidFinder.checkChunk((frag.x*fs) + (x), (frag.y*fs) + (y),seed,this)) {
					int biome = frag.getBiomeAt((x << 2) + 2, (y << 2) + 2);
					MapMarkers type = (biome == 6) ? MapMarkers.WITCH : MapMarkers.TEMPLE;
					
					frag.addMapObject(new MapObject(type, (frag.x*ls) + (x << 4), (frag.y*ls) + (y << 4)));
				}
			}
		}
	}
	
	private void getStrongholdData(Fragment frag) {
		for (int i = 0; i < 3; i++) {
			Point t = strongholds[i];
			if (frag.isInside(t.x, t.y)) {
				frag.addMapObject(new MapObjectStronghold(t.x, t.y));
			}
		}
	}
	
	private void getVillageData(Fragment frag) {
		int fs = Project.FRAGMENT_SIZE >> 2;
		int ls = Project.FRAGMENT_SIZE << 2;
		for (int y = 0; y < fs; y++) {
			for (int x = 0; x < fs; x++) {
				if (villageFinder.checkChunk((frag.x*fs) + (x), (frag.y*fs) + (y), seed, this)) {
					frag.addMapObject(new MapObject(MapMarkers.VILLAGE, (frag.x*ls) + (x << 4), (frag.y*ls) + (y << 4)));
				}
			}
		}
	}
	
	public void drawBiomeData(Fragment frag) {
		int x = frag.x;
		int y = frag.y;
		int range = frag.range;
		byte[] data = getBiomeForArea(x, y, range);
		frag.data = data;
		Graphics2D g2d = frag.createGraphics();
		int i = 0;
		long[] stat = new long[Biome.colors.length];
		for (int i1 = 0; i1 < stat.length; i1++) {
			stat[i1] = 0;
		}
		for (int ey = 0; ey < range; ey++) {
			for (int ex = 0; ex < range; ex++) {
				stat[data[i]]++;
				g2d.setColor(Biome.colors[data[i]]);
				g2d.fillRect(ex, ey, 1, 1);
				i++;
			}
		}
		for (int i1 = 0; i1 < stat.length; i1++) {
			frag.stat[i1] = stat[i1]/(float)i;
		}
		
		Graphics2D g2d2 = frag.newLayer();
		g2d2.setColor(Color.black);
		g2d2.drawRect(0, 0, Project.FRAGMENT_SIZE, Project.FRAGMENT_SIZE);
		g2d2.drawString(((x*Project.FRAGMENT_SIZE) << 2) + ", " + ((y*Project.FRAGMENT_SIZE) << 2), 5, 15);
		g2d.dispose();
		g2d2.dispose();
	}
	
	public void requestChunk(Fragment frag) {
		queue.add(frag);
	}
	
	public boolean a(int x, int y, int size, List<Biome> paramList) {
		int i = x - size >> 2;
		int j = y - size >> 2;
		int k = x + size >> 2;
		int m = y + size >> 2;
		
		int n = k - i + 1;
		int i1 = m - j + 1;
		
		int[] arrayOfInt = ba(i, j, n, i1);
		for (int i2 = 0; i2 < n * i1; i2++) {
			Biome localBiome = Biome.a[arrayOfInt[i2]];
			if (!paramList.contains(localBiome)) return false;
		}
		
		return true;
	}
	
	
	public Point a(int x, int y, int size, List<Biome> paramList, Random paramRandom) {
		int i = x - size >> 2;
		int j = y - size >> 2;
		int k = x + size >> 2;
		int m = y + size >> 2;
		
		int n = k - i + 1;
		int i1 = m - j + 1;
		int[] arrayOfInt = ba(i, j, n, i1);
		Point localPoint = null;
		int i2 = 0;
		for (int i3 = 0; i3 < n*i1; i3++) {
			int i4 = i + i3 % n << 2;
			int i5 = j + i3 / n << 2;
			if (arrayOfInt[i3] > Biome.a.length) {
				Log.debug(arrayOfInt[i3]);
				System.exit(0);
			}
			Biome localBiome = Biome.a[arrayOfInt[i3]];
			if ((!paramList.contains(localBiome)) || (
					(localPoint != null) && (paramRandom.nextInt(i2 + 1) != 0))) continue;
				localPoint = new Point(i4, i5);
				i2++;
		}
		
		return localPoint;
	}*/
}
