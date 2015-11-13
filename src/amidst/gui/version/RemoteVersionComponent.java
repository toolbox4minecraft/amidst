package amidst.gui.version;

import amidst.Application;
import amidst.minecraft.remote.RemoteMinecraft;

public class RemoteVersionComponent extends VersionComponent {
	private static final String DEFAULT_ADDRESS = "127.0.0.1";

	private String address;

	public RemoteVersionComponent(Application application) {
		this(application, DEFAULT_ADDRESS);
	}

	public RemoteVersionComponent(Application application, String address) {
		super(application);
		this.address = address;
	}

	@Override
	public void doLoad() {
		versionSelected(new RemoteMinecraft(address));
	}

	@Override
	public boolean isReadyToLoad() {
		return true;
	}

	@Override
	protected String getLoadingStatus() {
		return "";
	}

	@Override
	public String getVersionName() {
		return "remote:" + address;
	}

	@Override
	public String getDisplayName() {
		return "remote:" + address;
	}
}
