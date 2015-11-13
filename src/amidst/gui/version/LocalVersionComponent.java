package amidst.gui.version;

import amidst.Application;
import amidst.version.IProfileUpdateListener;
import amidst.version.MinecraftProfile;
import amidst.version.MinecraftProfile.Status;
import amidst.version.ProfileUpdateEvent;

public class LocalVersionComponent extends VersionComponent {
	private MinecraftProfile profile;

	public LocalVersionComponent(Application application,
			MinecraftProfile profile) {
		super(application);
		this.profile = profile;
		initProfile();
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
		versionSelected(profile);
	}

	@Override
	protected String getLoadingStatus() {
		return profile.getStatus().toString();
	}

	@Override
	public String getVersionName() {
		return "local:" + profile.getProfileName();
	}

	@Override
	public String getDisplayName() {
		return profile.getProfileName();
	}
}
