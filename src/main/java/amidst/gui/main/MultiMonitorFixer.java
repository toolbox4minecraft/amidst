package amidst.gui.main;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import amidst.logging.AmidstLogger;

/**
 * Listener that patches AWT bugs with multiple monitors.
 */
public class MultiMonitorFixer implements ComponentListener {
	private final JFrame frame;
	private MethodHandle setGCHandle = null;
	private boolean errorPrinted = false;

	public MultiMonitorFixer(JFrame frame) {
		this.frame = frame;
	}

	// This doesn't work on Java 16+, because of module encapsulation.
	// TODO: find another way to do this, or simply remove it?
	private static MethodHandle getGCHandle() throws NoSuchMethodException, SecurityException, IllegalAccessException {
		Method m1 = Window.class.getDeclaredMethod("setGraphicsConfiguration", GraphicsConfiguration.class);
		m1.setAccessible(true);
		MethodHandle mh1 = MethodHandles.lookup().unreflect(m1);
		return mh1.asType(MethodType.methodType(void.class, JFrame.class, GraphicsConfiguration.class)); // change to allow invokeExact
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateGC();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		updateGC();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		updateGC();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		updateGC();
	}

	private void updateGC() {
		if (errorPrinted) {
			return;
		}

		try {
			if (setGCHandle == null) {
				setGCHandle = getGCHandle();
			}
			for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				GraphicsConfiguration defaultConfig = gd.getDefaultConfiguration();
				if (!frame.getGraphicsConfiguration().equals(defaultConfig)) {
					if (frame.getLocation().getX() >= defaultConfig.getBounds().getMinX()
							&& frame.getLocation().getX() < defaultConfig.getBounds().getMaxX()
							&& frame.getLocation().getY() >= defaultConfig.getBounds().getMinY()
							&& frame.getLocation().getY() < defaultConfig.getBounds().getMaxY()) {
						setGCHandle.invokeExact(frame, defaultConfig);
					}
				}
			}
		} catch (Throwable t) {
			AmidstLogger.error("Unable to set GraphicsConfiguration; issues may arise with multi-monitors setups");
			errorPrinted = true;
		}
	}

}
