package amidst.version;

import java.io.File;
import java.util.ArrayList;

import amidst.json.InstallInformation;

public class MinecraftProfile implements ILatestVersionListListener {
	public enum Status {
		IDLE("scanning"),
		MISSING("missing"),
		FAILED("failed"),
		FOUND("found");
		
		private String name;
		private Status(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	private ArrayList<IProfileUpdateListener> listeners = new ArrayList<IProfileUpdateListener>();
	
	private MinecraftVersion version;
	private InstallInformation profile;
	
	private Status status = Status.IDLE;
	private String versionName = "unknown";
	
	public MinecraftProfile(InstallInformation profile) {
		this.profile = profile;
		if (profile.lastVersionId.equals("latest")) {
			LatestVersionList.get().addAndNotifyLoadListener(this);
		} else {
			version = MinecraftVersion.fromVersionId(profile.lastVersionId);
			if (version == null) {
				status = Status.MISSING;
				return;
			}
			status = Status.FOUND;
			versionName = version.getName();
		}
	}
	
	public String getProfileName() {
		return profile.name;
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
			for (int i = 0; i < profile.allowedReleaseTypes.length; i++)
				if (profile.allowedReleaseTypes[i].equals("snapshot"))
					usingSnapshots = true;
			if (usingSnapshots)
				version = MinecraftVersion.fromLatestSnapshot();
			else
				version = MinecraftVersion.fromLatestRelease();
			if (version == null)
				status = Status.FAILED;
			else
				versionName = version.getName();
			break;
		case LOADING:
			status = Status.IDLE;
			break;
		}
		for (IProfileUpdateListener listener: listeners)
			listener.onProfileUpdate(new ProfileUpdateEvent(this));
	}

	public File getJarFile() {
		return version.getJarFile();
	}
}
