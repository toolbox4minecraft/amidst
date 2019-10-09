package amidst.gui.main.viewer;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComponent;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.widget.Widget;

import org.apache.batik.dom.GenericDOMImplementation;

import org.apache.batik.svggen.SVGGraphics2D;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

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

            public void writeSvgScreenshot(final File file) throws IOException {
                int width = getWidth();
                int height = getHeight();
                Point mousePosition = getMousePositionOrNull();
                try (FileWriter writer = new FileWriter(file)) {
                    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                    Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
                    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
                    drawer.drawScreenshot(svgGenerator, width, height, mousePosition, widgetFontMetrics);
                    svgGenerator.stream(writer, true);
                }
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
    public void writeSvgScreenshot(final File file) throws IOException {
        component.writeSvgScreenshot(file);
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
