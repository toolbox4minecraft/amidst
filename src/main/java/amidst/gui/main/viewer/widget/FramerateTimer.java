package amidst.gui.main.viewer.widget;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class FramerateTimer {
	private int tickCounter;
	private long lastTime;
	private long msPerUpdate;

	private float currentFPS = 0.0f;

	@CalledOnlyBy(AmidstThread.EDT)
	public FramerateTimer(int updatesPerSecond) {
		msPerUpdate = (long) (1000f * (1f / updatesPerSecond));
		reset();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reset() {
		tickCounter = 0;
		lastTime = System.currentTimeMillis();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void tick() {
		tickCounter++;
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime > msPerUpdate) {
			currentFPS = calculateCurrentFPS(currentTime);
			tickCounter = 0;
			lastTime = currentTime;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private float calculateCurrentFPS(long currentTime) {
		float timeDifference = currentTime - lastTime;
		timeDifference /= 1000f;
		timeDifference = tickCounter / timeDifference;
		return timeDifference;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public float getCurrentFPS() {
		return currentFPS;
	}
}
