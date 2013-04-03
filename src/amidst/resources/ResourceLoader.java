package amidst.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Allows to retrieve images, URLs, …
 */
public class ResourceLoader {
	private ResourceLoader() {}
	
	public static URL getResourceURL(String name) {
		return ResourceLoader.class.getResource(name);
	}
	
	public static BufferedImage getImage(String name) throws IOException {
		return ImageIO.read(getResourceURL(name));
	}
}
