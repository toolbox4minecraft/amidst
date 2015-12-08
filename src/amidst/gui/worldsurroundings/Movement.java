package amidst.gui.worldsurroundings;

import java.awt.Point;

import amidst.preferences.PrefModel;

public class Movement {
	private double speedX = 0;
	private double speedY = 0;
	private Point lastMouse;

	private final PrefModel<Boolean> smoothScrollingPreference;

	public Movement(PrefModel<Boolean> smoothScrollingPreference) {
		this.smoothScrollingPreference = smoothScrollingPreference;
	}

	public void update(FragmentGraphToScreenTranslator translator,
			Point currentMouse) {
		updateMovementSpeed(currentMouse);
		adjustTranslator(translator);
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

	private void adjustTranslator(FragmentGraphToScreenTranslator translator) {
		translator.adjustToMovement((int) speedX, (int) speedY);
	}

	private void throttleMovementSpeed() {
		if (smoothScrollingPreference.get()) {
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
