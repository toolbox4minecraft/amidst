package amidst.map.layers;

import java.awt.Color;
import java.awt.Font;
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
		g.setColor(Color.black);
		g.setTransform(mat);
		g.drawRect(0, 0, size, size);
		double invZoom = 1.0 / map.getZoom();
		mat.scale(invZoom, invZoom);
		g.setTransform(mat);
		g.setFont(drawFont);
		g.drawString((fragment.getChunkX() << 4) + ", " +(fragment.getChunkY() << 4), 10, 20);
	}

}
