package amidst.version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import amidst.json.InstallInformation;

public class MinecraftProfile implements ILatestVersionListListener {
	public enum Status {
		IDLE("scanning"), MISSING("missing"), FAILED("failed"), FOUND("found");

		private final String name;

		private Status(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private final List<IProfileUpdateListener> listeners = new ArrayList<IProfileUpdateListener>();

	private MinecraftVersion version;
	private final InstallInformation profile;
	private final LatestVersionList latestVersionList;

	private Status status = Status.IDLE;
	private String versionName = "unknown";

	public MinecraftProfile(InstallInformation profile,
			LatestVersionList latestVersionList) {
		this.profile = profile;
		this.latestVersionList = latestVersionList;
		if (profile.getLastVersionId().equals("latest")) {
			latestVersionList.addAndNotifyLoadListener(this);
		} else {
			version = MinecraftVersion.fromVersionId(profile.getLastVersionId());
			if (version == null) {
				status = Status.MISSING;
				return;
			}
			status = Status.FOUND;
			versionName = version.getName();
		}
	}

	public String getProfileName() {
		return profile.getName();
	}

	public String getGameDir() {
		return profile.getGameDir();
	}

	public String getVersionName() {
		return versionName;
	}

	public void addUpdateListener(IProfileUpdateListener listener) {
		listeners.add(listener);
	}

	public void removeUpdateListener(IProfileUpdateListener listener) {
		listeners.remove(listener);
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public void onLoadStateChange(LatestVersionListEvent event) {
		switch (event.getSource().getLoadState()) {
		case FAILED:
			status = Status.FAILED;
			break;
		case IDLE:
			status = Status.IDLE;
			break;
		case LOADED:
			status = Status.FOUND;
			boolean usingSnapshots = false;
			for (int i = 0; i < profile.getAllowedReleaseTypes().length; i++) {
				if (profile.getAllowedReleaseTypes()[i].equals("snapshot")) {
					usingSnapshots = true;
				}
			}
			if (usingSnapshots) {
				version = MinecraftVersion.fromLatestSnapshot(latestVersionList
						.getVersions());
			} else {
				version = MinecraftVersion.fromLatestRelease(latestVersionList
						.getVersions());
			}
			if (version == null) {
				status = Status.FAILED;
			} else {
				versionName = version.getName();
			}
			break;
		case LOADING:
			status = Status.IDLE;
			break;
		}
		for (IProfileUpdateListener listener : listeners)
			listener.onProfileUpdate(new ProfileUpdateEvent(this));
	}

	public File getJarFile() {
		return version.getJarFile();
	}
}
