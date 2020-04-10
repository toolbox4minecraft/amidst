package amidst.devtools;

import amidst.settings.biomeprofile.BiomeProfile;

public class BiomeProfileSerializer {
	private final BiomeProfile profile;
	
	public BiomeProfileSerializer(BiomeProfile profile) {
		this.profile = profile;
	}
	
	public void run() {
		profile.validate();
		System.out.println(profile.serialize());
	}
	
}
