package amidst.gui.version;

import amidst.Application;
import amidst.minecraft.remote.RemoteMinecraft;

public class RemoteVersionComponent extends VersionComponent {
	private static final String DEFAULT_ADDRESS = "127.0.0.1";

	private Application application;
	private String address;

	public RemoteVersionComponent(Application application) {
		this(application, DEFAULT_ADDRESS);
	}

	public RemoteVersionComponent(Application application, String address) {
		this.application = application;
		this.address = address;
		initComponent();
	}

	@Override
	public void doLoad() {
		application.displayMainWindow(new RemoteMinecraft(address));
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
		return address;
	}

	@Override
	public String getVersionPrefix() {
		return "remote";
	}
}
