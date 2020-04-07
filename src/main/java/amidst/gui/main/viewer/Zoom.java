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
	private double target = zoomFromLevel(0);
	private double current = zoomFromLevel(0);

	private Point mousePosition = new Point();

	private final Setting<Boolean> maxZoomSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public Zoom(Setting<Boolean> maxZoomSetting) {
		this.maxZoomSetting = maxZoomSetting;
	}

	private static float zoomFromLevel(int level) {
		return (float) (0.25 * Math.pow(2, -level / 8.0));
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
		int oldLevel = level;
		int maxLevel = getMaxZoomLevel();
		int minLevel = getMinZoomLevel();

		if (notches > 0) {
			if (level < maxLevel) {
				level = Math.min(level + notches, maxLevel);
			}
		} else if (notches < 0) {
			if (level > minLevel) {
				level = Math.max(level + notches, minLevel);
			}
		}
		if (oldLevel != level) {
			this.mousePosition = mousePosition;
			target = zoomFromLevel(level);
			remainingTicks = 100 * Math.abs(oldLevel - level);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getMaxZoomLevel() {
		return maxZoomSetting.get() ? 12 : 10000;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getMinZoomLevel() {
		return -20;
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
