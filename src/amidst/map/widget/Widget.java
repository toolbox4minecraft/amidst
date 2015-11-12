package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.map.Map;
import amidst.map.MapViewer;

public abstract class Widget {
	protected MapViewer mapViewer;
	protected Map map;

	protected int x;
	protected int y;
	protected int width;
	protected int height;

	protected boolean visible = true;

	public Widget(MapViewer mapViewer) {
		this.mapViewer = mapViewer;
		this.map = mapViewer.getMap();
	}

	public boolean onClick(int x, int y) {
		return true;
	}

	public boolean onMouseWheelMoved(int x, int y, int rotation) {
		return false;
	}

	public boolean onMousePressed(int x, int y) {
		return true;
	}

	public void onMouseReleased() {
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

	public void setVisibility(boolean visible) {
		this.visible = visible;
	}

	public float getAlpha() {
		return 1.0f;
	}

	public abstract void draw(Graphics2D g2d, float time);
}
