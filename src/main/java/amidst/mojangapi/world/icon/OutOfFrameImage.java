package amidst.mojangapi.world.icon;

/**
 * Provides the "frame" information for images employing an "Out of Frame"
 * visual effect (see http://www.google.com.au/search?q=out+of+frame&tbm=isch)
 * such as the question mark in the Possible End City icon, or the chin of the
 * Village icon.
 */
public interface OutOfFrameImage {

	/**
	 * Returns the width of the icon "frame", ignoring any parts of the icon
	 * which might extend beyond the icon's border for an "Out of Frame" visual
	 * effect. Returns the image width if nothing is extending left or right of
	 * the frame.
	 */
	public int getFrameWidth();

	/**
	 * Returns the height of the icon "frame", ignoring any parts of the icon
	 * which might extend beyond the icon's border for an "Out of Frame" visual
	 * effect. Returns the image height if nothing is extending above or below
	 * the frame.
	 */
	public int getFrameHeight();

	/**
	 * Returns 0 if nothing is extending left or right of the frame.
	 */
	public int getFrameOffsetX();

	/**
	 * Returns 0 if nothing is extending above or below the frame.
	 */
	public int getFrameOffsetY();
}
