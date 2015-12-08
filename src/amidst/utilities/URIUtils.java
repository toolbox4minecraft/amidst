package amidst.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public enum URIUtils {
	;

	public static URI newURI(String location) {
		return URI.create(location);
	}

	public static URL newURL(String location) throws MalformedURLException {
		return URI.create(location).toURL();
	}

	public static Reader newReader(String location) throws IOException {
		return newReader(newURL(location));
	}

	public static Reader newReader(URL url) throws IOException {
		return new InputStreamReader(newInputStream(url));
	}

	public static Reader newReader(File file) throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}

	public static boolean exists(String location) {
		try {
			return exists(newURL(location));
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public static boolean exists(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("HEAD");
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		}
	}

	public static void download(String from, String to)
			throws MalformedURLException, IOException {
		download(newURL(from), newURI(to));
	}

	public static void download(URL from, URI to) throws IOException {
		Path toPath = Paths.get(to);
		toPath.getParent().toFile().mkdirs();
		if (toPath.toFile().exists()) {
			return;
		}
		Path part = Paths.get(toPath.toString() + ".part");
		InputStream in = newInputStream(from);
		Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
		Files.move(part, toPath, StandardCopyOption.REPLACE_EXISTING);
	}

	private static BufferedInputStream newInputStream(URL url)
			throws IOException {
		return new BufferedInputStream(url.openStream());
	}
}
