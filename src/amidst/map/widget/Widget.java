package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.map.Map;
import amidst.map.MapViewer;

public abstract class Widget {
	protected final MapViewer mapViewer;
	protected final Map map;

	private int x;
	private int y;
	private int width;
	private int height;

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

	protected void setX(int x) {
		this.x = x;
	}

	protected void setY(int y) {
		this.y = y;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	public abstract float getAlpha();

	public abstract boolean isVisible();

	public abstract void draw(Graphics2D g2d, float time);
}
