package amidst.map.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.Layer;


public class GridLayer extends Layer {
	private static Font drawFont = new Font("arial", Font.BOLD, 16);
	public GridLayer() {
		super("grid", null, 1.1f);
		setLive(true);
		setVisibilityPref(Options.instance.showGrid);
	}
	
	public void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat) {
		String text = (fragment.getChunkX() << 4) + ", " +(fragment.getChunkY() << 4);
		int stride = (int)(.25/map.getZoom());
		


		g.setColor(Color.black);
		g.setTransform(mat);
		int gridX = (fragment.getFragmentX() % (stride + 1));
		int gridY = (fragment.getFragmentY() % (stride + 1));
		if (gridY == 0)
			g.drawLine(0, 0, size, 0);
		if (gridY == stride)
			g.drawLine(0, size, size, size);
		if (gridX == 0)
			g.drawLine(0, 0, 0, size);
		if (gridX == stride)
			g.drawLine(size, 0, size, size);
		
		if (gridX != 0)
			return;
		if (gridY != 0)
			return;
		double invZoom = 1.0 / map.getZoom();
		mat.scale(invZoom, invZoom);
		g.setTransform(mat);
		g.setFont(drawFont);
		g.drawString(text, 10, 20);
	}

}
