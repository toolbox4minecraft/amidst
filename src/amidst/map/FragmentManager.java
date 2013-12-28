package amidst.map;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.logging.Log;

public class FragmentManager implements Runnable {
	private int cacheSize = 1024;
	
	private Thread currentThread;
	private boolean running = true;
	
	private Fragment[] fragmentCache;
	private ConcurrentLinkedQueue<Fragment> fragmentQueue;
	private ConcurrentLinkedQueue<Fragment> requestQueue;
	private ConcurrentLinkedQueue<Fragment> recycleQueue;
	private int sleepTick = 0;
	
	private Stack<Layer> layerList;
	
	private Layer[] layers;
	private IconLayer[] iconLayers;
	private Layer[] liveLayers;
	
	public FragmentManager(Layer[] layers, Layer[] liveLayers, IconLayer[] iconLayers) {
		fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
		requestQueue = new ConcurrentLinkedQueue<Fragment>();
		recycleQueue = new ConcurrentLinkedQueue<Fragment>();
		layerList = new Stack<Layer>();
		Collections.addAll(layerList, layers);
		
		fragmentCache = new Fragment[cacheSize];
		
		Arrays.sort(layers);
		for (int i = 0; i < cacheSize; i++) {
			fragmentCache[i] = new Fragment(layers, liveLayers, iconLayers);
			fragmentQueue.offer(fragmentCache[i]);
		}
		this.layers = layers;
		this.iconLayers = iconLayers;
		this.liveLayers = liveLayers;
	}
	
	public void reset() {
		running = false;
		try {
			currentThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		recycleQueue.clear();
		requestQueue.clear();
		fragmentQueue.clear();
		for (int i = 0; i < cacheSize; i++) {
			fragmentCache[i].reset();
			fragmentQueue.offer(fragmentCache[i]);
		}
	}
	
	private void increaseFragmentCache() {
		Fragment[] newFragments = new Fragment[cacheSize << 1];
		for (int i = 0; i < cacheSize; i++) {
			newFragments[i] = fragmentCache[i];
			fragmentCache[i] = null;
		}
		for (int i = cacheSize; i < cacheSize << 1; i++) {
			newFragments[i] = new Fragment(layers, liveLayers, iconLayers);
			fragmentQueue.offer(newFragments[i]);
		}
		fragmentCache = newFragments;
		Log.i("FragmentManager cache size increased from " + cacheSize + " to " + (cacheSize << 1));
		cacheSize <<= 1;
		System.gc();
	}
	public void repaintFragment(Fragment frag) {
		frag.repaint();
	}
	public Fragment requestFragment(int x, int y) {
		if (!running)
			return null;
		Fragment frag = null;
		while ((frag = fragmentQueue.poll()) == null)
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
		currentThread.setPriority(Thread.MIN_PRIORITY);

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
					fragmentQueue.offer(frag);
				}
			} else {
				sleepTick = 0;
				try {
					Thread.sleep(2L);
				} catch (InterruptedException ignored) {}
			}
		}
		
	}
	
	public void setMap(Map map) {
		for (Layer layer : layers)
			layer.setMap(map);
		
		for (Layer layer : liveLayers)
			layer.setMap(map);
		
		for (IconLayer layer : iconLayers) {
			layer.setMap(map);
			layer.reload();
		}
		
		currentThread = new Thread(this);

		running = true;
		currentThread.start();
	}

	public int getCacheSize() {
		return cacheSize;
	}
	public int getFreeFragmentQueueSize() {
		return fragmentQueue.size();
	}
	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}
	public int getRequestQueueSize() {
		return requestQueue.size();
	}
}
