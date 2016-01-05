package amidst.gui.main.viewer;

import java.awt.Point;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@NotThreadSafe
public class Zoom {
	private int remainingTicks = 0;
	private int level = 0;
	private double target = 0.25f;
	private double current = 0.25f;

	private Point mousePosition = new Point();

	private final Setting<Boolean> maxZoomSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public Zoom(Setting<Boolean> maxZoomSetting) {
		this.maxZoomSetting = maxZoomSetting;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void update(FragmentGraphToScreenTranslator translator) {
		remainingTicks--;
		if (remainingTicks >= 0) {
			double previous = updateCurrent();
			translator.adjustToZoom(previous, current, mousePosition);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private double updateCurrent() {
		double previous = current;
		current = (target + current) * 0.5;
		return previous;
	}

	@CalledOnlyBy(AmidstThread.EDT)
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

	@CalledOnlyBy(AmidstThread.EDT)
	private int getMaxZoomLevel() {
		if (maxZoomSetting.get()) {
			return 10;
		} else {
			return 10000;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public double getCurrentValue() {
		return current;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void skipFading() {
		remainingTicks = 0;
		current = target;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reset() {
		mousePosition = new Point();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public double screenToWorld(double coordinate) {
		return coordinate / current;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public double worldToScreen(double coordinate) {
		return coordinate * current;
	}
}
