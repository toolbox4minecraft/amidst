package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;

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
			double previous = current;
			current = (target + current) * 0.5;
			Point2D.Double targetZoom = map.getScaled(previous, current,
					zoomMouse);
			map.moveBy(targetZoom);
		}
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

	public int screenToWorld(int coordinate) {
		// TODO: int -> double -> int = bad?
		return (int) (coordinate / current);
	}

	public int worldToScreen(int coordinate) {
		// TODO: int -> double -> int = bad?
		return (int) (coordinate * current);
	}

	public double screenToWorld(long coordinate) {
		return coordinate / current;
	}

	public double worldToScreen(long coordinate) {
		return coordinate * current;
	}
}
