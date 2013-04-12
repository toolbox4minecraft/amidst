package amidst.map;

import java.io.File;

import amidst.Util;

public abstract class CacheManager {
	protected File cachePath;
	
	public CacheManager() {}
	
	public void setCachePath(String name) {
		cachePath = Util.getTempDir(name);
	}
	
	public abstract void save(Fragment frag);
	public abstract void load(Fragment frag, int layerID);
	public abstract void unload(Fragment frag);
}
