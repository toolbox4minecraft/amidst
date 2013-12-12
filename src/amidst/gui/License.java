package amidst.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import amidst.Log;
import amidst.resources.ResourceLoader;

public class License {
	private File file;
	private String name;
	private String contents;
	private boolean loaded = false;
	
	public License(String name, String path) {
		this.name = name;
		try {
			file = new File(ResourceLoader.getResourceURL(path).toURI());
		} catch (URISyntaxException e) {
			Log.w("Error loading license for: " + name + " at path: " + path);
			e.printStackTrace();
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
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			Log.w("Unable to find license: " + name + " with file: " + file);
			e.printStackTrace();
			return;
		}
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
	    	Log.w("Unable to read file: " + name + " with path " + file);
	    	e.printStackTrace();
	    } finally {
	        try {
				bufferedReader.close();
			} catch (IOException e) {
				Log.w("Unable to close BufferedReader for: " + name + " with file: " + file);
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
