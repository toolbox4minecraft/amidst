package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;

public abstract class LiveLayer extends Layer {
	public abstract void drawLive(Fragment fragment, Graphics2D g,
			AffineTransform mat);
}
