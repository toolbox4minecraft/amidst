package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.gui.widget.Widget;

public class MapViewer {
	@SuppressWarnings("serial")
	private class ViewerComponent extends JComponent {
		private final FontMetrics widgetFontMetrics = getFontMetrics(Widget.TEXT_FONT);

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			drawer.draw(g2d, getWidth(), getHeight(), getMousePosition(),
					widgetFontMetrics);
		}

		public void drawScreenshot(Graphics2D g2d) {
			drawer.drawScreenshot(g2d, getWidth(), getHeight(),
					getMousePosition(), widgetFontMetrics);
		}
	}

	private final ViewerMouseListener mouseListener;
	private final Drawer drawer;
	private final ViewerComponent component;

	public MapViewer(ViewerMouseListener mouseListener, Drawer drawer) {
		this.mouseListener = mouseListener;
		this.drawer = drawer;
		this.component = createComponent();
	}

	private ViewerComponent createComponent() {
		ViewerComponent result = new ViewerComponent();
		result.addMouseListener(mouseListener);
		result.addMouseWheelListener(mouseListener);
		result.setFocusable(true);
		return result;
	}

	public BufferedImage createCaptureImage() {
		BufferedImage image = new BufferedImage(component.getWidth(),
				component.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		component.drawScreenshot(g2d);
		g2d.dispose();
		return image;
	}

	public Point getMousePositionOrCenter() {
		Point result = component.getMousePosition();
		if (result == null) {
			result = new Point(component.getWidth() >> 1,
					component.getHeight() >> 1);
		}
		return result;
	}

	public void repaint() {
		component.repaint();
	}

	public Component getComponent() {
		return component;
	}
}
