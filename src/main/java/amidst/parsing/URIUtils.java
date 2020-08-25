package amidst.parsing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

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

	public static BufferedReader newReader(String location) throws IOException {
		return newReader(newURL(location));
	}

	public static BufferedReader newReader(URL url) throws IOException {
		return new BufferedReader(new InputStreamReader(url.openStream()));
	}

	public static BufferedInputStream newInputStream(URL url) throws IOException {
		return new BufferedInputStream(url.openStream());
	}

	public static byte[] readBytes(URL url) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte buf[] = new byte[4096];
		try (InputStream stream = url.openStream()) {
			int read;
			while((read = stream.read(buf)) >= 0) {
				bytes.write(buf, 0, read);
			}
		}
		return bytes.toByteArray();
	}

	public static FileSystem openZipFile(URI uri) throws URISyntaxException, IOException {
		URI zipUri = new URI("jar:" + uri.getScheme(), uri.getPath(), null);
		return FileSystems.newFileSystem(zipUri, new HashMap<>());
	}
}
