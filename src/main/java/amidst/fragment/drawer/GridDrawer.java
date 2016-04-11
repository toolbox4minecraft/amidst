package amidst.fragment.drawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.gui.main.viewer.Zoom;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public class GridDrawer extends FragmentDrawer {
	private static final Font DRAW_FONT = new Font("arial", Font.BOLD, 16);

	private final StringBuffer textBuffer = new StringBuffer(128);
	private final char[] textCache = new char[128];
	private final Zoom zoom;

	public GridDrawer(LayerDeclaration declaration, Zoom zoom) {
		super(declaration);
		this.zoom = zoom;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		int stride = getStride();
		int gridX = getGridX(fragment, stride);
		int gridY = getGridY(fragment, stride);
		initGraphics(g2d);
		drawGridLines(g2d, stride, gridX, gridY);
		if (isGrid00(gridX, gridY)) {
			double invZoom = 1.0 / zoom.getCurrentValue();
			g2d.scale(invZoom, invZoom);
			updateText(fragment);
			drawText(g2d);
			// drawThickTextOutline(g2d);
			drawTextOutline(g2d);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getStride() {
		return (int) (.25 / zoom.getCurrentValue());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getGridX(Fragment fragment, int stride) {
		return (int) fragment.getCorner().getXAs(Resolution.FRAGMENT) % (stride + 1);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getGridY(Fragment fragment, int stride) {
		return (int) fragment.getCorner().getYAs(Resolution.FRAGMENT) % (stride + 1);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void initGraphics(Graphics2D g2d) {
		g2d.setFont(DRAW_FONT);
		g2d.setColor(Color.black);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawGridLines(Graphics2D g2d, int stride, int gridX, int gridY) {
		if (gridY == 0) {
			g2d.drawLine(0, 0, Fragment.SIZE, 0);
		}
		if (gridY == stride) {
			g2d.drawLine(0, Fragment.SIZE, Fragment.SIZE, Fragment.SIZE);
		}
		if (gridX == 0) {
			g2d.drawLine(0, 0, 0, Fragment.SIZE);
		}
		if (gridX == stride) {
			g2d.drawLine(Fragment.SIZE, 0, Fragment.SIZE, Fragment.SIZE);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isGrid00(int gridX, int gridY) {
		return gridX == 0 && gridY == 0;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateText(Fragment fragment) {
		textBuffer.setLength(0);
		textBuffer.append(fragment.getCorner().getX());
		textBuffer.append(", ");
		textBuffer.append(fragment.getCorner().getY());
		textBuffer.getChars(0, textBuffer.length(), textCache, 0);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawText(Graphics2D g2d) {
		g2d.drawChars(textCache, 0, textBuffer.length(), 12, 17);
		g2d.drawChars(textCache, 0, textBuffer.length(), 8, 17);
		g2d.drawChars(textCache, 0, textBuffer.length(), 10, 19);
		g2d.drawChars(textCache, 0, textBuffer.length(), 10, 15);
	}

	// This makes the text outline a bit thicker, but seems unneeded.
	@SuppressWarnings("unused")
	private void drawThickTextOutline(Graphics2D g2d) {
		g2d.drawChars(textCache, 0, textBuffer.length(), 12, 15);
		g2d.drawChars(textCache, 0, textBuffer.length(), 12, 19);
		g2d.drawChars(textCache, 0, textBuffer.length(), 8, 15);
		g2d.drawChars(textCache, 0, textBuffer.length(), 8, 19);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawTextOutline(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.drawChars(textCache, 0, textBuffer.length(), 10, 17);
	}
}
