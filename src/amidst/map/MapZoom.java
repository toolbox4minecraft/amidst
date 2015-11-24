package amidst.map;

import java.awt.Point;

import amidst.Options;

public class MapZoom {
	private int remainingTicks = 0;
	private int level = 0;
	private double target = 0.25f;
	private double current = 0.25f;

	private Point zoomMouse = new Point();

	public void update(Map map) {
		remainingTicks--;
		if (remainingTicks >= 0) {
			moveMap(map, updateCurrent());
		}
	}

	private double updateCurrent() {
		double previous = current;
		current = (target + current) * 0.5;
		return previous;
	}

	private void moveMap(Map map, double previous) {
		map.moveBy(map.getDeltaOnScreenForSamePointInWorld(previous, current,
				zoomMouse));
	}

	public void adjustZoom(Point position, int notches) {
		zoomMouse = position;
		if (notches > 0) {
			if (level < getMaxZoomLevel()) {
				target /= 1.1;
				level++;
				remainingTicks = 100;
			}
		} else if (level > -20) {
			target *= 1.1;
			level--;
			remainingTicks = 100;
		}
	}

	private int getMaxZoomLevel() {
		if (Options.instance.maxZoom.get()) {
			return 10;
		} else {
			return 10000;
		}
	}

	public double getCurrentValue() {
		return current;
	}

	public void skipFading() {
		remainingTicks = 0;
		current = target;
	}

	public double screenToWorld(double coordinate) {
		return coordinate / current;
	}

	public double worldToScreen(double coordinate) {
		return coordinate * current;
	}
}
