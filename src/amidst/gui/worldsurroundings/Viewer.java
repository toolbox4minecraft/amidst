package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.gui.widget.Widget;

public class Viewer {
	@SuppressWarnings("serial")
	private static class ViewerComponent extends JComponent {
		private final FontMetrics widgetFontMetrics = getFontMetrics(Widget.TEXT_FONT);
		private final Drawer drawer;

		public ViewerComponent(Drawer drawer) {
			this.drawer = drawer;
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			drawer.draw(g2d, getWidth(), getHeight(), getMousePosition(),
					widgetFontMetrics);
		}

		public BufferedImage createCaptureImage() {
			int width = getWidth();
			int height = getHeight();
			Point mousePosition = getMousePosition();
			BufferedImage result = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = result.createGraphics();
			drawer.drawCaptureImage(g2d, width, height, mousePosition,
					widgetFontMetrics);
			g2d.dispose();
			return result;
		}
	}

	private final ViewerMouseListener mouseListener;
	private final ViewerComponent component;

	public Viewer(ViewerMouseListener mouseListener, Drawer drawer) {
		this.mouseListener = mouseListener;
		this.component = createComponent(drawer);
	}

	private ViewerComponent createComponent(Drawer drawer) {
		ViewerComponent result = new ViewerComponent(drawer);
		result.addMouseListener(mouseListener);
		result.addMouseWheelListener(mouseListener);
		result.setFocusable(true);
		return result;
	}

	public BufferedImage createCaptureImage() {
		return component.createCaptureImage();
	}

	public Point getMousePositionOrCenter() {
		Point result = component.getMousePosition();
		if (result == null) {
			result = new Point(component.getWidth() >> 1,
					component.getHeight() >> 1);
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.REPAINTER)
	public void repaintComponent() {
		component.repaint();
	}

	public Component getComponent() {
		return component;
	}
}
