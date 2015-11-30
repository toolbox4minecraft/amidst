package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;

public interface FragmentDrawer {
	void draw(Fragment fragment, Graphics2D g2d, AffineTransform layerMatrix);
}
