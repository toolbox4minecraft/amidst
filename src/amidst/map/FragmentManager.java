package amidst.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.Log;

public class FragmentManager extends Thread {
	private int cacheSize = 1024;
	
	
	private boolean running = true;
	
	private Fragment[] fragmentCache;
	private ConcurrentLinkedQueue<Fragment> fragmentStack;
	private ConcurrentLinkedQueue<Fragment> recycleQueue;
	private FragmentThread[] threads;
	private int lastThreadUsed;
	
	private Stack<Layer> layerList;
	
	private Layer[] layers;
	private IconLayer[] iconLayers;
	private Layer[] liveLayers;
	
	public FragmentManager(Layer[] layers, Layer[] liveLayers, IconLayer[] iconLayers) {
		fragmentStack = new ConcurrentLinkedQueue<Fragment>();
		recycleQueue = new ConcurrentLinkedQueue<Fragment>();
		layerList = new Stack<Layer>();
		Collections.addAll(layerList, layers);
		
		fragmentCache = new Fragment[cacheSize];
		
		Arrays.sort(layers);
		for (int i = 0; i < cacheSize; i++) {
			fragmentCache[i] = new Fragment(layers, liveLayers, iconLayers);
			fragmentStack.offer(fragmentCache[i]);
		}
		this.layers = layers;
		this.iconLayers = iconLayers;
		this.liveLayers = liveLayers;
		
		Log.i("FragmentManager running with " + Runtime.getRuntime().availableProcessors() + " threads.");
		threads = new FragmentThread[Runtime.getRuntime().availableProcessors()];
		lastThreadUsed = 0;
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new FragmentThread(i);
			threads[i].start();
		}
		
		start();
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
			newFragments[i] = new Fragment(layers, liveLayers, iconLayers);
			fragmentStack.offer(newFragments[i]);
		}
		fragmentCache = newFragments;
		Log.i("FragmentManager cache size increased from " + cacheSize + " to " + (cacheSize << 1));
		cacheSize <<= 1;
		System.gc();
	}
	public void repaintFragment(Fragment frag) {
		frag.clearData();
		lastThreadUsed = (lastThreadUsed + 1) % threads.length;
		threads[lastThreadUsed].offer(frag);
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
		lastThreadUsed = (lastThreadUsed + 1) % threads.length;
		threads[lastThreadUsed].offer(frag);
		return frag;
	}
	
	public void returnFragment(Fragment frag) {
		recycleQueue.offer(frag);
	}
	
	@Override
	public void run() {
		this.setPriority(MIN_PRIORITY);
		Fragment frag;
		while (running) {
			while ((frag = recycleQueue.poll()) != null) {
				synchronized (frag) {
					frag.recycle();
					fragmentStack.offer(frag);
				}
			}
			try {
				Thread.sleep(1L);
			} catch (InterruptedException ignored) {}
		}
	}
	
	public void close() {
		this.running = false;
		for (int i = 0; i < threads.length; i++)
			threads[i].shutdown();
		for (Fragment f : fragmentCache) {
			f.recycle();
			f.destroy();
		}
	}
}
