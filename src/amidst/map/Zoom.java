package amidst.map;

import java.awt.Point;

import amidst.preferences.PrefModel;

public class Zoom {
	private int remainingTicks = 0;
	private int level = 0;
	private double target = 0.25f;
	private double current = 0.25f;

	private Point mousePosition = new Point();

	private final PrefModel<Boolean> maxZoomPreference;

	public Zoom(PrefModel<Boolean> maxZoomPreference) {
		this.maxZoomPreference = maxZoomPreference;
	}

	public void update(Map map) {
		remainingTicks--;
		if (remainingTicks >= 0) {
			double previous = updateCurrent();
			map.adjustStartOnScreenToZoom(previous, current, mousePosition);
		}
	}

	private double updateCurrent() {
		double previous = current;
		current = (target + current) * 0.5;
		return previous;
	}

	public void adjustZoom(Point mousePosition, int notches) {
		this.mousePosition = mousePosition;
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
		if (maxZoomPreference.get()) {
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

	public void reset() {
		mousePosition = new Point();
	}

	public double screenToWorld(double coordinate) {
		return coordinate / current;
	}

	public double worldToScreen(double coordinate) {
		return coordinate * current;
	}
}
