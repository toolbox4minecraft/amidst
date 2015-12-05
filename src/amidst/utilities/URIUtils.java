package amidst.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public static BufferedReader newReader(String location) throws IOException {
		return newReader(newURL(location));
	}

	public static BufferedReader newReader(URL url) throws IOException {
		return new BufferedReader(new InputStreamReader(url.openStream()));
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

	public static void download(URL from, Path to) throws IOException {
		to.getParent().toFile().mkdirs();
		if (to.toFile().exists()) {
			return;
		}
		Path part = Paths.get(to.toString() + ".part");
		InputStream in = from.openStream();
		Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
		Files.move(part, to, StandardCopyOption.REPLACE_EXISTING);
	}
}
