package amidst.version;

public class ProfileUpdateEvent {
	private MinecraftProfile profile;
	public ProfileUpdateEvent(MinecraftProfile profile) {
		this.profile = profile;
	}
	
	public MinecraftProfile getSource() {
		return profile;
	}
}
