package amidst.map;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FragmentLoader {
	private ConcurrentLinkedQueue<Fragment> loadingQueue;

	private int[] imageCache = new int[Fragment.SIZE * Fragment.SIZE];

	public FragmentLoader(ConcurrentLinkedQueue<Fragment> loadingQueue) {
		this.loadingQueue = loadingQueue;
	}

	public void processRequestQueue() {
		while (!loadingQueue.isEmpty()) {
			loadingQueue.poll().load(imageCache);
		}
	}
}
