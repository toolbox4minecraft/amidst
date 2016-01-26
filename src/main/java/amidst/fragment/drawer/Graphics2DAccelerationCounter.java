package amidst.fragment.drawer;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 * Maintains performance-counting totals so that 2D hardware 
 * acceleration metrics can be calculated.
 */
public class Graphics2DAccelerationCounter {

	private volatile int acceleratedOperations = 0;
	private volatile int totalOperations = 0;
	
	private static final GraphicsConfiguration gc = GraphicsEnvironment
			.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration();
			
	/**
	 * Records that a graphics operation was performed, in this case with an image, so
	 * that 2D hardware acceleration metrics can be calculated
	 * @return Returns the image that was passed to it.
	 */
	public final BufferedImage LogOperationPerformed(BufferedImage imageUsed) {
		// Method is final only to make it easier for optimizer to inline, remove if you want to.
		totalOperations++;		
		if (imageUsed.getCapabilities(gc).isAccelerated()) acceleratedOperations++;
		
		return imageUsed;
	}

	/**
	 * Clears the performance counting totals.
	 */
	public void Clear() {
		acceleratedOperations = 0;
		totalOperations       = 0;
	}

	/**
	 * Adds the totals from the provided Graphics2DAccelerationCounter 
	 * to the totals held by this instance. 
	 */
	public void AddFrom(Graphics2DAccelerationCounter counter) {
		acceleratedOperations += counter.acceleratedOperations;
		totalOperations       += counter.totalOperations;
	}

	/** 
	 * Returns a value between 0 and 1, 0 being not accelerated, and 
	 * 1 meaning all operations were accelerated.
	 */
	public float AcceleratedRatio() {
		return totalOperations == 0 ? 0.0f : (float)acceleratedOperations / totalOperations;
	}	
	
	/**
	 * @return The number of operations that have been logged 
	 * since the last time Clear() was invoked. 
	 */
	public int getOperationCount() {
		return totalOperations;
	}
}
