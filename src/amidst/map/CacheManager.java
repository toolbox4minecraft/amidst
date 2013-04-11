package amidst.map;

import java.io.File;

import amidst.Amidst;

public abstract class CacheManager {
	protected String cachePath;
	
	public CacheManager() {
		
	}
	
	public void setCachePath(String path) {
		cachePath = Amidst.getPath() + "cache/" + path;
		(new File(cachePath)).mkdirs();
	}
	
	
	public abstract void save(Fragment frag);
	public abstract void load(Fragment frag, int layerID);
	public abstract void unload(Fragment frag);
}
