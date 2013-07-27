package amidst.map;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.Log;

public class FragmentManager extends Thread {
	private int cacheSize = 2048;
	
	// TODO : Implement custom cache paths?
	private boolean cacheEnabled = false;
	private File cachePath;
	
	private boolean running = true;
	
	private Fragment[] fragmentCache;
	private ConcurrentLinkedQueue<Fragment> fragmentStack;
	private ConcurrentLinkedQueue<Fragment> requestQueue;
	private ConcurrentLinkedQueue<Fragment> recycleQueue;
	private int sleepTick = 0;
	
	private Stack<Layer> layerList;
	
	private Layer[] layers;
	private IconLayer[] iconLayers;
	
	public FragmentManager(Layer[] layers, IconLayer[] iconLayers) {
		cacheEnabled = false;
		fragmentStack = new ConcurrentLinkedQueue<Fragment>();
		requestQueue = new ConcurrentLinkedQueue<Fragment>();
		recycleQueue = new ConcurrentLinkedQueue<Fragment>();
		layerList = new Stack<Layer>();
		Collections.addAll(layerList, layers);
		
		fragmentCache = new Fragment[cacheSize];
		
		Arrays.sort(layers);
		for (int i = 0; i < cacheSize; i++) {
			fragmentCache[i] = new Fragment(layers, iconLayers);
			fragmentStack.offer(fragmentCache[i]);
		}
		this.layers = layers;
		this.iconLayers = iconLayers;
		start();
	}
	
	public FragmentManager(File cachePath) {
		this(null, null); // TODO: What is this doing here?
		cacheEnabled = true;
		this.cachePath = cachePath;
	}
	
	public void reset() {
		// TODO : Unload all fragments
	}
	
	private void increaseFragmentCache() {
		Fragment[] newFragments = new Fragment[cacheSize << 1];
		for (int i = 0; i < cacheSize; i++) {
			newFragments[i] = fragmentCache[i];
			fragmentCache[i] = null;
		}
		for (int i = cacheSize; i < cacheSize << 1; i++) {
			newFragments[i] = new Fragment(layers, iconLayers);
			fragmentStack.offer(newFragments[i]);
		}
		fragmentCache = newFragments;
		Log.i("FragmentManager cache size increased from " + cacheSize + " to " + (cacheSize << 1));
		cacheSize <<= 1;
		System.gc();
	}
	
	public Fragment requestFragment(int x, int y) {
		if (!running)
			return null;
		Fragment frag = null;
		while ((frag = fragmentStack.poll()) == null)
			increaseFragmentCache();
		
		frag.clear();
		frag.blockX = x;
		frag.blockY = y;
		frag.isActive = true;
		requestQueue.offer(frag);
		return frag;
	}
	
	public void returnFragment(Fragment frag) {
		recycleQueue.offer(frag);
	}
	
	@Override
	public void run() {
		this.setPriority(MIN_PRIORITY);
		while (running) {
			if(!requestQueue.isEmpty() || !recycleQueue.isEmpty()) {
				if (!requestQueue.isEmpty()) {
					Fragment frag = requestQueue.poll();
					if (frag.isActive && !frag.isLoaded) {
						frag.load();
						sleepTick++;
						if (sleepTick == 10) {
							sleepTick = 0;
							try {
								Thread.sleep(1L);
							} catch (InterruptedException ignored) {}
						}
					}
				}
				
				while (!recycleQueue.isEmpty()) {
					Fragment frag = recycleQueue.poll();
					frag.recycle();
					fragmentStack.offer(frag);
				}
			} else {
				sleepTick = 0;
				try {
					Thread.sleep(2L);
				} catch (InterruptedException ignored) {}
			}
		}
	}
	
	public void close() {
		this.running = false;
		for (Fragment f : fragmentCache) f.recycle();
	}
}
