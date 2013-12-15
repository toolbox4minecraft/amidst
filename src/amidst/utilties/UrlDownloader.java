package amidst.utilties;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import amidst.Util;
import amidst.logging.Log;

public class UrlDownloader implements Runnable {
	public enum DownloadState {
		IDLE,
		CONNECTED,
		DOWNLOADING,
		COMPLETE,
		FAILED
	};
	// TODO: Add better error management inside download thread!
	// This won't matter for the version list (likely) but it will for other files if they're added!
	private ProgressMeter progress = new ProgressMeter();
	private URL url;
	private URLConnection urlConnection;
	private String urlString;
	private int contentLength = -1;
	private DownloadState state = DownloadState.IDLE;
	
	public UrlDownloader(String urlString) {
	}
	
	public boolean initializeConnection() {
		try {
			url = new URL(Util.REMOTE_VERSION_LIST_URL);
		} catch (MalformedURLException e) {
			Log.w("MalformedURLException on version list url: " + urlString);
			return false;
		}
		URLConnection urlConnection = null;
		try {
			urlConnection = url.openConnection();
		} catch (IOException e) {
			Log.i("IOException when opening URL: " + urlString);
			e.printStackTrace();
			return false;
		}
		contentLength = urlConnection.getContentLength();
		if (contentLength == -1) {
			Log.i("URL content length returned -1 on URL: " + urlString);
			return false;
		}
		return true;
	}
	
	public void startDownload() {
		(new Thread(this)).start();
	}
	
	public ProgressMeter getProgressMeter() {
		return progress;
	}

	public void run() {
		InputStream inputStream = null;
		try {
			inputStream = urlConnection.getInputStream();
		} catch (IOException e) {
			Log.i("IOException on opening input stream to URL: " + urlString);
			e.printStackTrace();
			return;
		}
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		byte[] data = new byte[contentLength];
		StringBuilder builder = new StringBuilder();
		String aux = "";

		//while ((aux = reader.readLine()) != null) {
		 //   builder.append(aux);
		//}

		String text = builder.toString();
		Log.i("Unable to find compatable version with release types.");
	}
}
