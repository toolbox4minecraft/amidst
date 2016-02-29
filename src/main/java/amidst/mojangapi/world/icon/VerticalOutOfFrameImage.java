package amidst.mojangapi.world.icon;

import java.awt.image.BufferedImage;

/**
 * A BufferedImage which provides OutOfFrameImage information, based off a crude
 * analysis of the image. See constructor comment.
 */
public class VerticalOutOfFrameImage extends BufferedImage implements
		OutOfFrameImage {
	private int frameOffsetX;
	private int frameOffsetY;
	private int frameWidth;
	private int frameHeight;

	public VerticalOutOfFrameImage(BufferedImage image, int frameOffsetX,
			int frameOffsetY, int frameWidth, int frameHeight) {
		super(image.getColorModel(), image.copyData(null), image
				.getColorModel().isAlphaPremultiplied(), null);
		this.frameOffsetX = frameOffsetX;
		this.frameOffsetY = frameOffsetY;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
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
	public VerticalOutOfFrameImage(BufferedImage image) {
		super(image.getColorModel(), image.copyData(null), image
				.getColorModel().isAlphaPremultiplied(), null);

		this.frameOffsetX = 0;
		this.frameWidth = getWidth();

		int imageMarginTop = findFirstSolidPixel(this, true);
		int imageMarginBottom = findFirstSolidPixel(this, false);

		this.frameOffsetY = imageMarginTop;
		this.frameHeight = Math.max(0, getHeight()
				- (imageMarginTop + imageMarginBottom));
	}

	private int findFirstSolidPixel(BufferedImage image, boolean fromTop) {
		int result = 0;
		if (image != null) {
			int height = image.getHeight();
			int y = fromTop ? 0 : height - 1;
			int yInc = fromTop ? 1 : -1;

			while (result < height) {
				int rgba = image.getRGB(0, y);
				if ((rgba >>> 24) > 0) {
					// found a non-transparent pixel
					break;
				}
				y += yInc;
				result++;
			}
			if (result == height) {
				// icon is not full width, it may be using a
				// padded image size to indicate its effective size instead.
				result = 0;
			}
		}
		return result;
	}

	@Override
	public int getFrameWidth() {
		return frameWidth;
	}

	@Override
	public int getFrameHeight() {
		return frameHeight;
	}

	@Override
	public int getFrameOffsetX() {
		return frameOffsetX;
	}

	@Override
	public int getFrameOffsetY() {
		return frameOffsetY;
	}
}
