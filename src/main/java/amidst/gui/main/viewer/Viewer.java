package amidst.gui.main.viewer;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.widget.Widget;

@NotThreadSafe
public class Viewer {
	@SuppressWarnings("serial")
	private static class ViewerComponent extends JComponent {
		private final FontMetrics widgetFontMetrics = getFontMetrics(Widget.TEXT_FONT);
		private final Drawer drawer;

		@CalledOnlyBy(AmidstThread.EDT)
		public ViewerComponent(Drawer drawer) {
			this.drawer = drawer;
		}

		@CalledOnlyBy(AmidstThread.EDT)
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			drawer.draw(g2d, getWidth(), getHeight(), getMousePositionOrNull(), widgetFontMetrics);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		public BufferedImage createScreenshot() {
			int width = getWidth();
			int height = getHeight();
			Point mousePosition = getMousePositionOrNull();
			BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = result.createGraphics();
			drawer.drawScreenshot(g2d, width, height, mousePosition, widgetFontMetrics);
			g2d.dispose();
			return result;
		}

		/**
		 * The method getMousePosition() might throw a null pointer exception in
		 * a multi-monitor setup, as soon as the window is dragged to the other
		 * monitor.
		 */
		@CalledOnlyBy(AmidstThread.EDT)
		private Point getMousePositionOrNull() {
			try {
				return getMousePosition();
			} catch (NullPointerException e) {
				return null;
			}
		}
	}

	private final ViewerMouseListener mouseListener;
	private final ViewerComponent component;

	@CalledOnlyBy(AmidstThread.EDT)
	public Viewer(ViewerMouseListener mouseListener, Drawer drawer) {
		this.mouseListener = mouseListener;
		this.component = createComponent(drawer);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private ViewerComponent createComponent(Drawer drawer) {
		ViewerComponent result = new ViewerComponent(drawer);
		result.addMouseListener(mouseListener);
		result.addMouseWheelListener(mouseListener);
		result.setFocusable(true);
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public BufferedImage createScreenshot() {
		return component.createScreenshot();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Point getMousePositionOrCenter() {
		Point result = component.getMousePositionOrNull();
		if (result == null) {
			result = new Point(component.getWidth() >> 1, component.getHeight() >> 1);
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.REPAINTER)
	public void repaintComponent() {
		component.repaint();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Component getComponent() {
		return component;
	}
}
