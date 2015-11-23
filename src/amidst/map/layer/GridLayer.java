package amidst.map.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import amidst.Options;
import amidst.map.Fragment;
import amidst.minecraft.world.Resolution;

public class GridLayer extends LiveLayer {
	private static final Font DRAW_FONT = new Font("arial", Font.BOLD, 16);

	private StringBuffer textBuffer = new StringBuffer(128);
	private char[] textCache = new char[128];

	@Override
	public boolean isVisible() {
		return Options.instance.showGrid.get();
	}

	@Override
	public void drawLive(Fragment fragment, Graphics2D g2d,
			AffineTransform inMat) {
		AffineTransform mat = new AffineTransform(inMat);
		initGraphics(g2d, mat);
		int stride = getStride();
		int gridX = getGridX(fragment, stride);
		int gridY = getGridY(fragment, stride);
		drawGridLines(g2d, stride, gridX, gridY);
		if (isGrid00(gridX, gridY)) {
			scaleGraphics(g2d, mat);
			updateText(fragment);
			drawText(g2d);
			// drawThickTextOutline(g2d);
			drawTextOutline(g2d);
			resetTransformation(g2d, inMat);
		}
	}

	private void initGraphics(Graphics2D g2d, AffineTransform mat) {
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(DRAW_FONT);
		g2d.setColor(Color.black);
		g2d.setTransform(mat);
	}

	private int getStride() {
		return (int) (.25 / getMap().getZoom());
	}

	// TODO: use longs?
	private int getGridX(Fragment fragment, int stride) {
		return (int) fragment.getCorner().getXAs(Resolution.FRAGMENT)
				% (stride + 1);
	}

	// TODO: use longs?
	private int getGridY(Fragment fragment, int stride) {
		return (int) fragment.getCorner().getYAs(Resolution.FRAGMENT)
				% (stride + 1);
	}

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

	private boolean isGrid00(int gridX, int gridY) {
		return gridX == 0 && gridY == 0;
	}

	private void scaleGraphics(Graphics2D g2d, AffineTransform mat) {
		double invZoom = 1.0 / getMap().getZoom();
		mat.scale(invZoom, invZoom);
		g2d.setTransform(mat);
	}

	private void updateText(Fragment fragment) {
		textBuffer.setLength(0);
		textBuffer.append(fragment.getCorner().getX());
		textBuffer.append(", ");
		textBuffer.append(fragment.getCorner().getY());
		textBuffer.getChars(0, textBuffer.length(), textCache, 0);
	}

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

	private void drawTextOutline(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.drawChars(textCache, 0, textBuffer.length(), 10, 17);
	}

	private void resetTransformation(Graphics2D g2d, AffineTransform inMat) {
		g2d.setTransform(inMat);
	}
}
