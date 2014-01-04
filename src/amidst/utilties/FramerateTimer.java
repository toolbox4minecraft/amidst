package amidst.utilties;

public class FramerateTimer {
	private int tickCounter;
	private long lastUpdate;
	private long msPerUpdate;
	
	private float currentFPS = 0.0f;
	
	public FramerateTimer(int updatesPerSecond) {
		msPerUpdate = (long)(1000f * (1f/updatesPerSecond));
		reset();
	}
	
	public void reset() {
		tickCounter = 0;
		lastUpdate = System.currentTimeMillis();
	}
	
	public void tick() {
		tickCounter++;
		long curTime = System.currentTimeMillis();
		
		if (curTime - lastUpdate > msPerUpdate) {
			float timeDifference = curTime - lastUpdate;
			
			timeDifference /= 1000f;
			timeDifference = tickCounter/timeDifference;
			
			currentFPS = timeDifference;
			
			tickCounter = 0;
			lastUpdate = curTime;
			
		}
	}
	
	public float getFramerate() {
		return currentFPS;
	}
	
	@Override
	public String toString() {
		return "FPS: " + String.format("%.1f", currentFPS);
	}
}
