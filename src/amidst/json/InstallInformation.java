package amidst.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import amidst.Amidst;
import amidst.Util;
import amidst.logging.Log;

public class InstallInformation {
	public String name;
	public String lastVersionId;
	public String javaDir;
	public String javaArgs;
	public Resolution resolution;
	public String[] allowedReleaseTypes = new String[] { "release" };
	public boolean isOld;
	
	public InstallInformation() {
		this(false);
	}
	
	public InstallInformation(boolean old) {
		if (old) {
			name = "Minecraft";
			lastVersionId = "None";
		}
		isOld = old;
	}
	public InstallInformation(String name, String version) {
		this.name = name;
		lastVersionId = version;
		isOld = false;
	}
	public boolean validate() {
		Log.i("Validating version: " + lastVersionId);
		if (lastVersionId != null) {
			Log.i("Version valid without further testing.");
			return true;
		}
		URL versionUrl = null;
		try {
			versionUrl = new URL("https://s3.amazonaws.com/Minecraft.Download/versions/versions.json");
		} catch (MalformedURLException e) {
			Log.i("MalformedURLException on version list loader.");
			e.printStackTrace();
			return false;
		}
		URLConnection urlConnection = null;
		try {
			urlConnection = versionUrl.openConnection();
		} catch (IOException e) {
			Log.i("IOException on version list loader.");
			e.printStackTrace();
			return false;
		}
		int contentLength = urlConnection.getContentLength();
		if (contentLength == -1) {
			Log.i("Version list returned content length of -1.");
			return false;
		}
		InputStream inputStream = null;
		try {
			inputStream = urlConnection.getInputStream();
		} catch (IOException e) {
			Log.i("IOException on opening input stream to version list.");
			e.printStackTrace();
			return false;
		}
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		VersionList versionList = null;
		try {
			versionList = Util.readObject(bufferedReader, VersionList.class);
		} catch (FileNotFoundException e) {
			Log.i("FileNotFoundException when parsing the version list.");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < versionList.versions.length; i++) {
			for (int q = 0; q < allowedReleaseTypes.length; q++) {
				if (versionList.versions[i].get("type").equals(allowedReleaseTypes[q])) {
					lastVersionId = versionList.versions[i].get("id");
					if (doesJarExist()) {
						Log.i("Found compatable version. Version ID: " + lastVersionId);
						return true;
					}
				}
			}
		}
		Log.i("Unable to find compatable version with release types.");
		return false;
		
	}
	public String toString() {
		return name;
	}
	public boolean doesJarExist() {
		if (isOld)
			return (new File(Util.minecraftDirectory + "/bin/minecraft.jar")).exists();
		else
			return (new File(Util.minecraftDirectory + "/versions/" + lastVersionId + "/" + lastVersionId + ".jar")).exists();
	}
	
	public File getJarFile() {
		File returnFile;
		if (!isOld) {
			returnFile = new File(Util.minecraftDirectory + "/versions/" + lastVersionId + "/" + lastVersionId + ".jar");
			if (returnFile.exists())
				return returnFile;
			Log.i("Attempt to get jar failed. Path: " + returnFile);
			File versionsPath = new File(Util.minecraftDirectory + "/versions/");
			if (versionsPath.exists()) { // https://s3.amazonaws.com/Minecraft.Download/versions/versions.json
				File[] files = versionsPath.listFiles();
				for (int i = 0; i < files.length; i++) {
					File jar = new File(files[i] + "/" + files[i].getName() + ".jar");
					if (jar.exists())
						return jar;
				}
				Log.i("Attempt to use alternative version failed. Path: " + versionsPath);
			} else {
				Log.i("Attempt to browse versions folder failed. Path: " + versionsPath);
			}
		}
		returnFile = new File(Util.minecraftDirectory + "/bin/minecraft.jar");
		if (returnFile.exists())
			return returnFile;
		
		Log.crash("Found profile selection, but unable to locate minecraft.jar.");
		System.exit(0);
		return null;
	}

	private class VersionList {
		public HashMap<String, String> latest;
		public HashMap<String, String>[] versions;
	}
}