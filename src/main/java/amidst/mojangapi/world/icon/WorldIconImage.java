package amidst.mojangapi.world.icon;

import java.awt.image.BufferedImage;

import amidst.documentation.Immutable;

/**
 * Provides the "frame" information for images employing an "Out of Frame"
 * visual effect (see http://www.google.com.au/search?q=out+of+frame&tbm=isch)
 * such as the question mark in the Possible End City icon, or the chin of the
 * Village icon.
 */
@Immutable
public class WorldIconImage {
	public static WorldIconImage from(BufferedImage image) {
		return new WorldIconImage(image, 0, 0, image.getWidth(), image.getHeight());
	}

	/**
	 * Use pixel transparency along the left edge of the icon to automatically
	 * determine the vertical frame position. Assumes the image has no
	 * horizontal out of frame effects.
	 * 
	 * If you want transparency on the left side of the image without adjusting
	 * its frame, or if you want to control where the frame starts, use a pixel
	 * that is mostly transparent. (Spawn.png and Witch.png do this)
	 */
	public static WorldIconImage fromPixelTransparency(BufferedImage image) {
		int imageMarginTop = findFirstSolidPixelFromTop(image);
		int imageMarginBottom = findFirstSolidPixelFromBottom(image);
		int frameHeight = Math.max(0, image.getHeight() - imageMarginTop - imageMarginBottom);
		return new WorldIconImage(image, 0, imageMarginTop, image.getWidth(), frameHeight);
	}

	private static int findFirstSolidPixelFromTop(BufferedImage image) {
		int height = image.getHeight();
		return findFirstSolidPixel(image, height, 0, 1);
	}

	private static int findFirstSolidPixelFromBottom(BufferedImage image) {
		int height = image.getHeight();
		return findFirstSolidPixel(image, height, height - 1, -1);
	}

	private static int findFirstSolidPixel(BufferedImage image, int height, int initialY, int deltaY) {
		int y = initialY;
		for (int i = 0; i < height; i++) {
			if (isNonTransparent(image, 0, y)) {
				return i;
			}
			y += deltaY;
		}
		// icon is not full width, it may be using a padded image size to
		// indicate its effective size instead.
		return 0;
	}

	private static boolean isNonTransparent(BufferedImage image, int x, int y) {
		return (image.getRGB(x, y) >>> 24) > 0;
	}

	private final BufferedImage image;
	private final int frameOffsetX;
	private final int frameOffsetY;
	private final int frameWidth;
	private final int frameHeight;

	public WorldIconImage(BufferedImage image, int frameOffsetX, int frameOffsetY, int frameWidth, int frameHeight) {
		this.image = image;
		this.frameOffsetX = frameOffsetX;
		this.frameOffsetY = frameOffsetY;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}

	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Returns 0 if nothing is extending left or right of the frame.
	 */
	public int getFrameOffsetX() {
		return frameOffsetX;
	}

	/**
	 * Returns 0 if nothing is extending above or below the frame.
	 */
	public int getFrameOffsetY() {
		return frameOffsetY;
	}

	/**
	 * Returns the width of the icon "frame", ignoring any parts of the icon
	 * which might extend beyond the icon's border for an "Out of Frame" visual
	 * effect. Returns the image width if nothing is extending left or right of
	 * the frame.
	 */
	public int getFrameWidth() {
		return frameWidth;
	}

	/**
	 * Returns the height of the icon "frame", ignoring any parts of the icon
	 * which might extend beyond the icon's border for an "Out of Frame" visual
	 * effect. Returns the image height if nothing is extending above or below
	 * the frame.
	 */
	public int getFrameHeight() {
		return frameHeight;
	}
}
