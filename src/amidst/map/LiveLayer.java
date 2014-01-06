package amidst.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public abstract class LiveLayer extends Layer {
	public LiveLayer() {
		
	}
	public abstract void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat);
}
