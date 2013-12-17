package amidst.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import amidst.logging.Log;
import amidst.resources.ResourceLoader;

public class License {
	private InputStream fileStream;
	private String name;
	private String contents;
	private boolean loaded = false;
	
	public License(String name, String path) {
		this.name = name;
		try {
			fileStream = ResourceLoader.getResourceStream(path);
		} catch (NullPointerException e) {
			Log.w("Error finding license for: " + name + " at path: " + path);
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void load() {
		if (loaded)
			return;
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream));
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String line = bufferedReader.readLine();

			while (line != null) {
				stringBuilder.append(line);
				stringBuilder.append('\n');
				line = bufferedReader.readLine();
			}
			contents = stringBuilder.toString();
			loaded = true;
		} catch (IOException e) {
			Log.w("Unable to read file: " + name + ".");
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				Log.w("Unable to close BufferedReader for: " + name + ".");
				e.printStackTrace();
			}
		}
		
	}
	
	public String getContents() {
		return contents;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public String toString() {
		return name;
	}
}
