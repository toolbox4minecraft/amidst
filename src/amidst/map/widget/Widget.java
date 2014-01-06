package amidst.map.widget;

import java.awt.Graphics2D;

import MoF.MapViewer;
import amidst.map.Map;

public class Widget {
	protected MapViewer mapViewer;
	protected Map map;
	
	protected int x, y, width, height;
	protected boolean visible = true;
	
	public Widget(MapViewer mapViewer) {
		this.mapViewer = mapViewer;
		this.map = mapViewer.getMap();
	}
	
	public void draw(Graphics2D g2d, float time) {
		
	}
	public void onClick(int x, int y) {
		
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public boolean isVisible() {
		return visible;
	}
	public void setVisibility(boolean value) {
		visible = value;
	}
	
	public float getAlpha() {
		return 1.0f;
	}
}
