package amidst.version;

import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.utilties.ProgressMeter;

public class VersionFactory {
	private boolean hasScanedRemoteVersionList = false;
	public VersionFactory() {
		
		
	}
	
	public void scanForInstalled() {
		
	}
	
	public ProgressMeter scanRemoteVersionList() {
		Log.i("Downloading remote version list.");

		ProgressMeter progress = new ProgressMeter();
		
		return progress;
	}
	
	public Minecraft produceByVersion(String version) {
		
		return null;
	}
}
