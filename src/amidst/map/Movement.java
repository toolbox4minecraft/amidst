package amidst.map;

import java.awt.Point;

import amidst.Options;

public class Movement {
	private double speedX = 0;
	private double speedY = 0;
	private Point lastMouse;

	public void update(Map map, Point currentMouse) {
		updateMovementSpeed(currentMouse);
		moveMap(map);
		throttleMovementSpeed();
	}

	private void updateMovementSpeed(Point currentMouse) {
		if (lastMouse != null) {
			if (currentMouse != null) {
				double dX = currentMouse.x - lastMouse.x;
				double dY = currentMouse.y - lastMouse.y;
				// TODO: Scale with time
				speedX = dX * 0.2;
				speedY = dY * 0.2;
			}
			lastMouse.translate((int) speedX, (int) speedY);
		}
	}

	private void moveMap(Map map) {
		map.adjustStartOnScreenToMovement((int) speedX, (int) speedY);
	}

	private void throttleMovementSpeed() {
		if (Options.instance.mapFlicking.get()) {
			speedX *= 0.95f;
			speedY *= 0.95f;
		} else {
			speedX = 0;
			speedY = 0;
		}
	}

	public void setLastMouse(Point lastMouse) {
		this.lastMouse = lastMouse;
	}
}
