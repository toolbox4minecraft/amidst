package amidst.gui.widget;

public class FramerateTimer {
	private int tickCounter;
	private long lastTime;
	private long msPerUpdate;

	private float currentFPS = 0.0f;

	public FramerateTimer(int updatesPerSecond) {
		msPerUpdate = (long) (1000f * (1f / updatesPerSecond));
		reset();
	}

	public void reset() {
		tickCounter = 0;
		lastTime = System.currentTimeMillis();
	}

	public void tick() {
		tickCounter++;
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime > msPerUpdate) {
			currentFPS = calculateCurrentFPS(currentTime);
			tickCounter = 0;
			lastTime = currentTime;
		}
	}

	private float calculateCurrentFPS(long currentTime) {
		float timeDifference = currentTime - lastTime;
		timeDifference /= 1000f;
		timeDifference = tickCounter / timeDifference;
		return timeDifference;
	}

	@Override
	public String toString() {
		return "FPS: " + String.format("%.1f", currentFPS);
	}
}
