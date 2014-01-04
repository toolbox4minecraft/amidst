package amidst.map;

import java.util.Vector;

import amidst.logging.Log;

public class ByteArrayCache extends CacheManager {
	public static final int CACHE_SIZE = 32, CACHE_SHIFT = 5;
	public static final int HEADER_SIZE = (CACHE_SIZE*CACHE_SIZE) >> 3;
	public static final int CACHE_MAX_SIZE = CACHE_SIZE*CACHE_SIZE; // TODO : Change name to CACHE_LENGTH ?
	
	private int maxUnits;
	private byte unitSize;
	
	private Vector<ByteArrayHub> cacheMap;
	
	private byte[] byteCache; 
	
	public ByteArrayCache(byte unitSize, int maxUnits) {
		cacheMap = new Vector<ByteArrayHub>();
		this.unitSize = unitSize;
		this.maxUnits = maxUnits;
		byteCache = new byte[unitSize*maxUnits];
	}
	
	@Override
	public void save(Fragment frag) {
		
	}
	
	@Override
	public void load(Fragment frag, int layerID) {
		long keyX = frag.getFragmentX() >> CACHE_SHIFT;
		long keyY = frag.getFragmentY() >> CACHE_SHIFT;
		long key = (keyX << 32) | (keyY & 0xFFFFFFFFL);
		
		ByteArrayHub hub = getHub(key);
		if (hub == null) {
			hub = new ByteArrayHub(key, unitSize, maxUnits, cachePath);
			Log.i("Loading [X:" + keyX + " Y:" + keyY + " KEY:"  + key + "]"); // TODO : Remove
			cacheMap.add(hub);
		}
		
		int subKeyX = Math.abs(frag.getFragmentX()) % CACHE_SIZE;
		int subKeyY = Math.abs(frag.getFragmentY()) % CACHE_SIZE;
		int subKey = (subKeyX << CACHE_SHIFT) + subKeyY;
		//Log.i("FragX:" + frag.getFragmentX() + " FragY:" + frag.getFragmentY() + " |keyX:" + keyX + " keyY:" + keyY + " key:" + key + "| X:" + subKeyX + " Y:" + subKeyY + " Key:" + subKey + " TKey:" + hub.getKey());
		byte[] tempData = null;
		if (hub.exists(subKey)) {
			tempData = hub.get(subKey);
		} else {
			//tempData = (byte[]) ((NativeJavaArray)PluginManager.call(funcSave, frag)).unwrap();
			hub.put(subKey, tempData);
		}
		//PluginManager.call(funcLoad, frag, tempData, layerID);
		
		hub.activeFragments++;
	}
	
	@Override
	public void unload(Fragment frag) {
		long keyX = frag.getFragmentX() >> CACHE_SHIFT;
		long keyY = frag.getFragmentY() >> CACHE_SHIFT;
		long key = (keyX << 32) | (keyY & 0xFFFFFFFFL);
		ByteArrayHub hub = getHub(key);
		
		hub.activeFragments--;
		//Log.i(masterCount);
		if (hub.activeFragments == 0) {
			Log.i("Unloading [X:" + keyX + " Y:" + keyY + " KEY:"  + key + "]"); // TODO : Remove
			hub.unload();
			cacheMap.remove(hub);
		}
		
	}
	private ByteArrayHub getHub(long key) {
		for (ByteArrayHub hub : cacheMap) {
			if (hub.getKey() == key) 
				return hub;
		}
		return null;
	}

	// JS Shortcut
	// TODO : Add more?
	
	public byte[] intToCachedBytes(int[] data) {
		for (int i = 0; i < byteCache.length; i++) {
			byteCache[i] = (byte) data[i];
		}
		return byteCache;
	}
	
}
