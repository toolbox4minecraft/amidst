package amidst.utilities;

import amidst.Amidst;

import com.boxysystems.jgoogleanalytics.FocusPoint;
import com.boxysystems.jgoogleanalytics.JGoogleAnalyticsTracker;

public class Google {
	private static final String APP_NAME = "AMIDST";
	private static final String TRACKING_CODE = "UA-27092717-1";

	private static JGoogleAnalyticsTracker tracker = createTracker();

	private static JGoogleAnalyticsTracker createTracker() {
		return new JGoogleAnalyticsTracker(APP_NAME, Amidst.version(),
				TRACKING_CODE);
	}

	public static void track(String name) {
		tracker.trackAsynchronously(new FocusPoint(name));
	}
}
