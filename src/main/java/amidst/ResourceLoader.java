package amidst;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import amidst.documentation.Immutable;

@Immutable
public enum ResourceLoader {
	;

	public static URL getResourceURL(String name) {
		return ResourceLoader.class.getResource(name);
	}

	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(getResourceURL(name));
		} catch (IOException e) {
			// This is always a developer error, because a resource was not
			// included in the jar file.
			throw new IllegalArgumentException(e);
		}
	}

	public static String getResourceAsString(String name) throws IOException,
			URISyntaxException {
		return new String(Files.readAllBytes(getResourceAsPath(name)),
				StandardCharsets.UTF_8);
	}

	private static Path getResourceAsPath(String name)
			throws URISyntaxException {
		return new File(getResourceURL(name).toURI()).toPath();
	}
}
