package amidst.map.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.LiveLayer;


public class GridLayer extends LiveLayer {
	private static Font drawFont = new Font("arial", Font.BOLD, 16);
	private static StringBuffer textBuffer = new StringBuffer(128);
	private static char[] textCache = new char[128];
	
	public GridLayer() {
	}
	
	@Override
	public boolean isVisible() {
		return Options.instance.showGrid.get();
	}
	@Override
	public void drawLive(Fragment fragment, Graphics2D g, AffineTransform inMat) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		AffineTransform mat = new AffineTransform(inMat);
		
		textBuffer.setLength(0);
		textBuffer.append(fragment.getChunkX() << 4);
		textBuffer.append(", ");
		textBuffer.append(fragment.getChunkY() << 4);
		
		textBuffer.getChars(0, textBuffer.length(), textCache, 0);
		
		int stride = (int)(.25/map.getZoom());
		


		g.setColor(Color.black);
		g.setTransform(mat);
		int gridX = (fragment.getFragmentX() % (stride + 1));
		int gridY = (fragment.getFragmentY() % (stride + 1));
		if (gridY == 0)
			g.drawLine(0, 0, Fragment.SIZE, 0);
		if (gridY == stride)
			g.drawLine(0, Fragment.SIZE, Fragment.SIZE, Fragment.SIZE);
		if (gridX == 0)
			g.drawLine(0, 0, 0, Fragment.SIZE);
		if (gridX == stride)
			g.drawLine(Fragment.SIZE, 0, Fragment.SIZE, Fragment.SIZE);
		
		if (gridX != 0)
			return;
		if (gridY != 0)
			return;
		double invZoom = 1.0 / map.getZoom();
		mat.scale(invZoom, invZoom);
		g.setTransform(mat);
		g.setFont(drawFont);
		g.drawChars(textCache, 0, textBuffer.length(), 12, 17);
		g.drawChars(textCache, 0, textBuffer.length(),  8, 17);
		g.drawChars(textCache, 0, textBuffer.length(), 10, 19);
		g.drawChars(textCache, 0, textBuffer.length(), 10, 15);
		
		// This makes the text outline a bit thicker, but seems unneeded.
		//g.drawChars(textCache, 0, textBuffer.length(), 12, 15);
		//g.drawChars(textCache, 0, textBuffer.length(), 12, 19);
		//g.drawChars(textCache, 0, textBuffer.length(),  8, 15);
		//g.drawChars(textCache, 0, textBuffer.length(),  8, 19);
		
		g.setColor(Color.white);
		g.drawChars(textCache, 0, textBuffer.length(), 10, 17);
		
		g.setTransform(inMat);
	}

}
