package amidst.map;

import java.io.File;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.Log;

public class FragmentManager extends Thread {
	private static final int CACHE_SIZE = 512;
	
	
	// TODO : Implement custom cache paths?
	private boolean cacheEnabled = false;
	private File cachePath;
	
	private boolean running = true;
	private boolean loaded = false;
	
	private Fragment[] fragmentCache;
	private ConcurrentLinkedQueue<Fragment> fragmentStack;
	private ConcurrentLinkedQueue<Fragment> requestQueue;
	private ConcurrentLinkedQueue<Fragment> recycleQueue;
	private int sleepTick = 0;
	
	private Stack<Layer> layerList;
	
	public FragmentManager() {		
		cacheEnabled = false;
		fragmentStack = new ConcurrentLinkedQueue<Fragment>();
		requestQueue = new ConcurrentLinkedQueue<Fragment>();
		recycleQueue = new ConcurrentLinkedQueue<Fragment>();
		layerList = new Stack<Layer>();
	}
	public FragmentManager(File cachePath) {
		this();
		cacheEnabled = true;
		this.cachePath = cachePath;
	}
	
	public void load() {

		if (loaded) {
			reset();
		} else {
			fragmentCache = new Fragment[CACHE_SIZE];
			
		}
		Layer[] layers = new Layer[layerList.size()];
		for (int i = 0; i < layers.length; i++)
			layers[i] = layerList.pop();
		
		Arrays.sort(layers);
		for (int i = 0; i < CACHE_SIZE; i++) {
			fragmentCache[i] = new Fragment(layers);
			fragmentStack.offer(fragmentCache[i]);
		}

		loaded = true;
	}
	public void reset() {
		// TODO : Unload all fragments
		
	}
	
	public void addLayer(Layer layer) {
		layerList.add(layer);
	}
	
	public Fragment requestFragment(int x, int y) {
		if (!running)
			return null;
		Fragment frag = fragmentStack.poll();
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
	
	public void run() {
		this.setPriority(MIN_PRIORITY);
		while (running) {
			boolean needsSleep = false;
			needsSleep = requestQueue.isEmpty();
			if (!requestQueue.isEmpty()) {
				Fragment frag = requestQueue.poll();
				if (frag.isActive&&!frag.isLoaded) {
					frag.load();
					sleepTick++;
					if (sleepTick == 10) {
						sleepTick = 0;
						try {
							Thread.sleep(1L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					/**/
				}
			}
			needsSleep &= recycleQueue.isEmpty();
			while (!recycleQueue.isEmpty()) {
				Fragment frag = recycleQueue.poll();
				frag.recycle();
				fragmentStack.offer(frag);
			}
			
			if (needsSleep) {
				sleepTick = 0;
				try {
					Thread.sleep(2L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	public void close() {
		this.running = false;
		for (int i = 0; i < fragmentCache.length; i++) {
			fragmentCache[i].recycle();
		}
	}
}
