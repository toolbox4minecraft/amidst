package amidst.gui.version;

import MoF.FinderWindow;
import amidst.Options;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.remote.RemoteMinecraft;

public class RemoteVersionComponent extends VersionComponent {
	private String remoteAddress;
	private String name;
	
	public RemoteVersionComponent(String address) {
		remoteAddress = address;
		name = "remote:" + address;
	}
	public RemoteVersionComponent() {
		this("127.0.0.1");
	}
	
	@Override
	public void load() {
		isLoading = true;
		repaint();
		Options.instance.lastProfile.set(name);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				MinecraftUtil.setBiomeInterface(new RemoteMinecraft(remoteAddress));
				new FinderWindow();
				VersionSelectWindow.get().dispose();
			}
		})).start();
	}

	@Override
	public boolean isReadyToLoad() {
		return true;
	}
	@Override
	public String getVersionName() {
		return name;
	}
}
