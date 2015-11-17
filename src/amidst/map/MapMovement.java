package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;

import amidst.Options;

public class MapMovement {
	private Point2D.Double speed = new Point2D.Double();

	public void update(Map map, Point lastMouse, Point currentMouse) {
		updateMapMovementSpeed(lastMouse, currentMouse);
		moveMap(map);
		throttleMapMovementSpeed();
	}

	private void updateMapMovementSpeed(Point lastMouse, Point currentMouse) {
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
}
