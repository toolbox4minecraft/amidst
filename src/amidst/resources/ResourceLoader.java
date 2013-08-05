package amidst.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
<<<<<<< HEAD
 * Allows to retrieve images, URLs, :
=======
 * Allows to retrieve images, URLs, 
>>>>>>> Map-Overhaul
 */
public class ResourceLoader {
	private ResourceLoader() {}
	
	public static URL getResourceURL(String name) {
		// This is also a valid way to load resources, although I'm not sure which is better.
		//return ClassLoader.getSystemClassLoader().getResource("amidst/resources/" + name);
		return ResourceLoader.class.getResource(name);
	}
	
	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(getResourceURL(name));
		} catch (IOException e) { //Don’t forget to run the tests :)
			throw new RuntimeException(e);
		}
	}
}
