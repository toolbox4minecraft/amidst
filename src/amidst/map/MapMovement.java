package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;

import amidst.Options;

public class MapMovement {
	private Point2D.Double speed = new Point2D.Double();
	private Point lastMouse;

	public void update(Map map, Point currentMouse) {
		updateMapMovementSpeed(currentMouse);
		moveMap(map);
		throttleMapMovementSpeed();
	}

	private void updateMapMovementSpeed(Point currentMouse) {
		if (lastMouse != null) {
			if (currentMouse != null) {
				double dX = currentMouse.x - lastMouse.x;
				double dY = currentMouse.y - lastMouse.y;
				// TODO : Scale with time
				speed.setLocation(dX * 0.2, dY * 0.2);
			}
			lastMouse.translate((int) speed.x, (int) speed.y);
		}
	}

	private void moveMap(Map map) {
		map.moveBy((int) speed.x, (int) speed.y);
	}

	private void throttleMapMovementSpeed() {
		if (Options.instance.mapFlicking.get()) {
			speed.x *= 0.95f;
			speed.y *= 0.95f;
		} else {
			speed.x = 0;
			speed.y = 0;
		}
	}

	public void setLastMouse(Point lastMouse) {
		this.lastMouse = lastMouse;
	}

	public void reset() {
		speed = new Point2D.Double();
		lastMouse = null;
	}
}
