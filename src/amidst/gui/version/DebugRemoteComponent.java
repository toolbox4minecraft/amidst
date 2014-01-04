package amidst.gui.version;

import MoF.FinderWindow;
import amidst.Options;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.remote.RemoteMinecraft;
import amidst.version.MinecraftProfile;

public class DebugRemoteComponent extends VersionComponent {

	public DebugRemoteComponent(MinecraftProfile profile) {
		super(profile);
	}
	
	@Override
	public void load() {
		isLoading = true;
		repaint();
		Options.instance.lastProfile.set(profile.getProfileName());
		(new Thread(new Runnable() {
			@Override
			public void run() {
				MinecraftUtil.setBiomeInterface(new RemoteMinecraft());
				new FinderWindow();
				VersionSelectWindow.get().dispose();
			}
		})).start();
	}
}
