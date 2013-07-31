package MoF;

import amidst.Amidst;

import com.boxysystems.jgoogleanalytics.*;


public class Google {
	private static JGoogleAnalyticsTracker tracker;
	public static void startTracking() {
		  tracker = new JGoogleAnalyticsTracker("AMIDST", Amidst.version(), "UA-27092717-1");

	}
	
	public static void track(String s) {
		  FocusPoint focusPoint = new FocusPoint(s);
		  tracker.trackAsynchronously(focusPoint);
	}
	
}
