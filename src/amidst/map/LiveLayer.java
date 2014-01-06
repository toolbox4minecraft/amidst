package amidst.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.preferences.BooleanPrefModel;

public abstract class LiveLayer {
	protected Map map;
	
	public LiveLayer() {
		
	}
	
	public boolean isVisible() {
		return true;
	}
	
	public abstract void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat);
	
	public void setMap(Map map) {
		this.map = map;
	}
	public Map getMap() {
		return map;
	}
}
