package amidst.version;

import java.util.ArrayList;

import amidst.json.InstallInformation;

public class MinecraftProfile {
	private ArrayList<IProfileUpdateListener> listeners = new ArrayList<IProfileUpdateListener>();
	
	private MinecraftVersion version;
	private InstallInformation profile;
	
	private String profileName;
	
	public MinecraftProfile(InstallInformation profile) {
		this.profile = profile;
		
		profileName = profile.name;
		if (profileName.length() > 10)
			profileName = profileName.substring(0, 7) + "...";
	}
	
	public String getProfileName() {
		return profileName;
	}
	
	public void addUpdateListener(IProfileUpdateListener listener) {
		listeners.add(listener);
	}
	public void removeUpdateListener(IProfileUpdateListener listener) {
		listeners.remove(listener);
	}
}
