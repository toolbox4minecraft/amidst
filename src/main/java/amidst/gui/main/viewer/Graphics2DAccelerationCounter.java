package amidst.gui.main.viewer;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

/**
 * Maintains performance-counting totals so that 2D hardware acceleration
 * metrics can be calculated.
 */
@NotThreadSafe
public class Graphics2DAccelerationCounter {
	private static final int UPDATE_PERCENTAGE_AFTER = 1000;

	private volatile int accelerated = 0;
	private volatile int total = 0;
	private volatile float acceleratedPercentage = 0;

	private static final GraphicsConfiguration GC = GraphicsEnvironment
			.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration();

	/**
	 * Records that a graphics operation was performed, in this case with an
	 * image, so that 2D hardware acceleration metrics can be calculated
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public void log(BufferedImage image) {
		if (image.getCapabilities(GC).isAccelerated()) {
			accelerated++;
		}
		total++;
		if (total == UPDATE_PERCENTAGE_AFTER) {
			acceleratedPercentage = 100f * accelerated / total;
			accelerated = 0;
			total = 0;
		}
	}

	/**
	 * Returns a value between 0 and 1, 0 being not accelerated, and 1 meaning
	 * all operations were accelerated.
	 */
	@CalledByAny
	public float getAcceleratedPercentage() {
		return acceleratedPercentage;
	}
}
