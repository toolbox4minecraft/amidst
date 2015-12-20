package amidst;

import java.awt.image.BufferedImage;
import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstMetaData {
	public static AmidstMetaData from(Properties properties, BufferedImage icon) {
		// @formatter:off
		return new AmidstMetaData(
				icon,
				Integer.parseInt(properties.getProperty("amidst.version.major")),
				Integer.parseInt(properties.getProperty("amidst.version.minor")),
				properties.getProperty("amidst.gui.mainWindow.title"),
				properties.getProperty("amidst.googleanalytics.appName"),
				properties.getProperty("amidst.googleanalytics.appVersion"),
				properties.getProperty("amidst.googleanalytics.trackingCode"));
		// @formatter:on
	}

	private final BufferedImage icon;
	private final int majorVersion;
	private final int minorVersion;
	private final String mainWindowTitle;
	private final String googleAnalyticsAppName;
	private final String googleAnalyticsAppVersion;
	private final String googleAnalyticsTrackingCode;

	private AmidstMetaData(BufferedImage icon, int majorVersion,
			int minorVersion, String mainWindowTitle,
			String googleAnalyticsAppName, String googleAnalyticsAppVersion,
			String googleAnalyticsTrackingCode) {
		this.icon = icon;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.mainWindowTitle = mainWindowTitle;
		this.googleAnalyticsAppName = googleAnalyticsAppName;
		this.googleAnalyticsAppVersion = googleAnalyticsAppVersion;
		this.googleAnalyticsTrackingCode = googleAnalyticsTrackingCode;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public String getMainWindowTitle() {
		return mainWindowTitle;
	}

	public String getGoogleAnalyticsAppName() {
		return googleAnalyticsAppName;
	}

	public String getGoogleAnalyticsAppVersion() {
		return googleAnalyticsAppVersion;
	}

	public String getGoogleAnalyticsTrackingCode() {
		return googleAnalyticsTrackingCode;
	}
}
