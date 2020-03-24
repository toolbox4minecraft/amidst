package amidst.parsing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

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

	public static BufferedInputStream newInputStream(URL url) throws IOException {
		return new BufferedInputStream(url.openStream());
	}
}
