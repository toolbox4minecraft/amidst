package amidst.mojangapi.mocking.json;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class BiomeRequestRecordJson {
	
	public volatile int x;
	public volatile int y;
	public volatile int width;
	public volatile int height;
	public volatile boolean isQuarterResolution;
	public volatile long startTime;
	public volatile long duration;
	public volatile String threadName;
	
	@GsonConstructor
	public BiomeRequestRecordJson() {
	}
	
	public BiomeRequestRecordJson(int x, int y, int width, int height,
			boolean isQuarterResolution, long startTime, long duration, String threadName) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.isQuarterResolution = isQuarterResolution;
		this.startTime = startTime;
		this.duration = duration;
		this.threadName = threadName;
	}
}
