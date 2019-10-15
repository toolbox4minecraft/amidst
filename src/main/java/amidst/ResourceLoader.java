package amidst;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.imageio.ImageIO;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;

@Immutable
public enum ResourceLoader {
	;

	@NotNull
	public static URL getResourceURL(String name) throws IllegalArgumentException {
		URL result = ResourceLoader.class.getResource(name);
		if (result == null) {
			// This is always a developer error, because a resource was not
			// included in the jar file.
			throw new IllegalArgumentException("unable to load resource: '" + name + "'");
		}
		return result;
	}

	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(getResourceURL(name));
		} catch (IOException e) {
			// This is always a developer error, because a resource was not
			// included in the jar file.
			throw new IllegalArgumentException("unable to load resource: '" + name + "'", e);
		}
	}

	public static Properties getProperties(String name) {
		try (InputStream stream = getResourceAsStream(name)) {
			Properties properties = new Properties();
			properties.load(stream);
			return properties;
		} catch (IOException e) {
			// This is always a developer error, because a resource was not
			// included in the jar file.
			throw new IllegalArgumentException("unable to load resource: '" + name + "'", e);
		}
	}

	public static String getResourceAsString(String name) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(getResourceAsStream(name), StandardCharsets.UTF_8)) {
			char[] buffer = new char[1024];
			int length;
			StringBuilder result = new StringBuilder();
			while ((length = reader.read(buffer)) != -1) {
				result.append(buffer, 0, length);
			}
			return result.toString();
		}
	}

	public static InputStream getResourceAsStream(String filename) {
		return new BufferedInputStream(ResourceLoader.class.getResourceAsStream(filename));
	}
}
