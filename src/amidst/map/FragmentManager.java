package amidst.map;

import java.util.Collections;
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
	
	private Object queueLock = new Object();
	
	private Stack<ImageLayer> layerList;
	
	private ImageLayer[] imageLayers;
	private IconLayer[] iconLayers;
	private LiveLayer[] liveLayers;
	
	public FragmentManager(ImageLayer[] imageLayers, LiveLayer[] liveLayers, IconLayer[] iconLayers) {
		fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
		requestQueue = new ConcurrentLinkedQueue<Fragment>();
		recycleQueue = new ConcurrentLinkedQueue<Fragment>();
		layerList = new Stack<ImageLayer>();
		Collections.addAll(layerList, imageLayers);
		
		fragmentCache = new Fragment[cacheSize];
		for (int i = 0; i < imageLayers.length; i++)
			imageLayers[i].setLayerId(i);
		for (int i = 0; i < cacheSize; i++) {
			fragmentCache[i] = new Fragment(imageLayers, liveLayers, iconLayers);
			fragmentQueue.offer(fragmentCache[i]);
		}
		this.imageLayers = imageLayers;
		this.iconLayers = iconLayers;
		this.liveLayers = liveLayers;
	}
	public void updateLayers(float time) {
		for (ImageLayer layer : imageLayers)
			layer.update(time);
		for (LiveLayer layer : liveLayers)
			layer.update(time);
		for (IconLayer layer : iconLayers)
			layer.update(time);
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
			newFragments[i] = new Fragment(imageLayers, liveLayers, iconLayers);
			fragmentQueue.offer(newFragments[i]);
		}
		fragmentCache = newFragments;
		Log.i("FragmentManager cache size increased from " + cacheSize + " to " + (cacheSize << 1));
		cacheSize <<= 1;
		System.gc();
	}
	
	public void repaintFragment(Fragment frag) {
		synchronized (queueLock) {
			frag.repaint();
		}
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
					synchronized (queueLock) {
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
				}
				
				while (!recycleQueue.isEmpty()) {
					synchronized (queueLock) {
						Fragment frag = recycleQueue.poll();
						frag.recycle();
						fragmentQueue.offer(frag);
					}
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
		for (ImageLayer layer : imageLayers) {
			layer.setMap(map);
			layer.reload();
		}
		
		for (LiveLayer layer : liveLayers) {
			layer.setMap(map);
			layer.reload();
		}
		
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
	public void repaintFragmentLayer(Fragment frag, int id) {
		synchronized (queueLock) {
			frag.repaintImageLayer(id);
		}
	}
}
