package amidst.version;

import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.utilties.ProgressListener;

public class VersionFactory {
	private boolean hasScanedRemoteVersionList = false;
	public VersionFactory() {
		
		
	}
	
	public void scanForInstalled() {
		
	}
	
	public ProgressListener scanRemoteVersionList() {
		
		hasScanedRemoteVersionList = true;
		return null;
	}
	
	public Minecraft produceByVersion(String version) {
		
		return null;
	}
}
