package amidst.version;

import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.utilties.ProgressMeter;

public class VersionFactory {
	private boolean hasScanedRemoteVersionList = false;
	public VersionFactory() {
		
		
	}
	
	public void scan() {
		scanForInstalled();
		scanRemoteVersionList();
	}
	
	private void scanForInstalled() {
		Log.i("Scanning for local versions.");
	}
	
	private void scanRemoteVersionList() {
		Log.i("Downloading remote version list.");
		
	}
	
	public Minecraft produceByVersion(String version) {
		
		return null;
	}
}
