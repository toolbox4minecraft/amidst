package amidst;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.WorldSeed;

import com.boxysystems.jgoogleanalytics.FocusPoint;
import com.boxysystems.jgoogleanalytics.JGoogleAnalyticsTracker;

@ThreadSafe
public class GoogleTracker {
	private final JGoogleAnalyticsTracker tracker;

	public GoogleTracker(JGoogleAnalyticsTracker tracker) {
		this.tracker = tracker;
	}

	public GoogleTracker() {
		this.tracker = null;
	}

	public void trackApplicationRunning() {
		track("Run");
	}

	public void trackSeed(WorldSeed seed) {
		if (seed.hasTrackingMessage()) {
			track(seed.getTrackingMessage());
		}
	}

	private void track(String name) {
		if (tracker != null) {
			doTrack(name);
		}
	}

	private synchronized void doTrack(String name) {
		tracker.trackAsynchronously(new FocusPoint(name));
	}
}
