package amidst.gui.version;

import amidst.Application;
import amidst.version.MinecraftProfile;

public class LocalVersionComponent extends VersionComponent {
	private Application application;
	private MinecraftProfile profile;

	public LocalVersionComponent(Application application,
			MinecraftProfile profile) {
		this.application = application;
		this.profile = profile;
		initComponent();
	}

	public String getProfileName() {
		return profile.getProfileName();
	}

	@Override
	public boolean isReadyToLoad() {
		return profile.getStatus();
	}

	@Override
	public void doLoad() {
		application.displayMainWindow(profile);
	}

	@Override
	protected String getLoadingStatus() {
		if (profile.getStatus()) {
			return "found";
		} else {
			return "failed";
		}
	}

	@Override
	public String getVersionName() {
		return profile.getProfileName();
	}

	@Override
	public String getVersionPrefix() {
		return "local";
	}
}
