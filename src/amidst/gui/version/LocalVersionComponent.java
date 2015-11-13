package amidst.gui.version;

import amidst.Application;
import amidst.version.IProfileUpdateListener;
import amidst.version.MinecraftProfile;
import amidst.version.MinecraftProfile.Status;
import amidst.version.ProfileUpdateEvent;

public class LocalVersionComponent extends VersionComponent {
	private Application application;
	private MinecraftProfile profile;

	public LocalVersionComponent(Application application,
			MinecraftProfile profile) {
		this.application = application;
		this.profile = profile;
		initProfile();
		initComponent();
	}

	private void initProfile() {
		profile.addUpdateListener(new IProfileUpdateListener() {
			@Override
			public void onProfileUpdate(ProfileUpdateEvent event) {
				repaintComponent();
			}
		});
	}

	public String getProfileName() {
		return profile.getProfileName();
	}

	@Override
	public boolean isReadyToLoad() {
		return profile.getStatus() == Status.FOUND;
	}

	@Override
	public void doLoad() {
		application.displayMapWindow(profile);
	}

	@Override
	protected String getLoadingStatus() {
		return profile.getStatus().toString();
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
