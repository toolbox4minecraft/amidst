package amidst.mojangapi;

import java.io.IOException;
import java.net.URL;

import amidst.logging.Log;
import amidst.mojangapi.versionlist.VersionListJson;

public enum VersionListReader {
	;

	public static VersionListJson readRemoteOrLocalVersionList(String remoteVersionList,
			URL localVersionList) {
		Log.i("Beginning latest version list load.");
		Log.i("Attempting to download remote version list...");
		VersionListJson remote = null;
		try {
			remote = MojangAPI.remoteVersionList();
		} catch (IOException e) {
			Log.w("Unable to read remote version list.");
			Log.printTraceStack(e);
			Log.w("Aborting version list load. URL: " + remoteVersionList);
		}
		if (remote != null) {
			Log.i("Successfully loaded version list. URL: " + remoteVersionList);
			return remote;
		}
		Log.i("Attempting to download local version list...");
		VersionListJson local = null;
		try {
			local = MojangAPI.localVersionListFromResource();
		} catch (IOException e) {
			Log.w("Unable to read local version list.");
			Log.printTraceStack(e);
			Log.w("Aborting version list load. URL: " + localVersionList);
		}
		if (local != null) {
			Log.i("Successfully loaded version list. URL: " + localVersionList);
			return local;
		}
		Log.w("Failed to load both remote and local version list.");
		return null;
	}
}
