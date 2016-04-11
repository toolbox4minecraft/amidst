package amidst.mojangapi.file;

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
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import amidst.documentation.Immutable;

@Immutable
public enum URIUtils {
	;

	public static URI newURI(String location) throws IOException {
		try {
			return URI.create(location);
		} catch (IllegalArgumentException e) {
			throw new IOException("malformed uri: " + location, e);
		}
	}

	public static URL newURL(String location) throws IOException {
		return newURI(location).toURL();
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
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean exists(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");
			return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		}
	}

	public static void download(String from, String to) throws IOException {
		download(newURL(from), Paths.get(to));
	}

	private static void download(URL from, Path to) throws IOException {
		to.getParent().toFile().mkdirs();
		if (to.toFile().exists()) {
			return;
		}
		Path part = Paths.get(to.toString() + ".part");
		InputStream in = newInputStream(from);
		Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
		Files.move(part, to, StandardCopyOption.REPLACE_EXISTING);
	}

	private static BufferedInputStream newInputStream(URL url) throws IOException {
		return new BufferedInputStream(url.openStream());
	}
}
