package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;

import amidst.settings.Setting;

public class StaticImageProgressWidget extends ProgressWidget {
	private static volatile int min = 0, max = 100, progress = -1;
	private volatile int xOffset;
	private final Setting<Boolean> showDebug;
	private final DebugWidget debugWidget;
	private final int iconWidth;
	
	public StaticImageProgressWidget(CornerAnchorPoint anchor, int xOffset, Setting<Boolean> showDebug, DebugWidget debugWidget, int iconWidth) {
		super(anchor, xOffset, 0, min, max, progress, 150, 25);
		this.xOffset = xOffset;
		this.showDebug = showDebug;
		this.debugWidget = debugWidget;
		this.iconWidth = iconWidth;
	}

	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		setXOffset(showDebug.get() ? xOffset - debugWidget.getWidth() : xOffset - iconWidth);
		super.setMin(min);
		super.setMax(max);
		super.setProgress(progress);
	}

	public static void setStaticMin(int min) {
		StaticImageProgressWidget.min = min;
	}

	public static void setStaticMax(int max) {
		StaticImageProgressWidget.max = max;
	}

	public static void setStaticProgress(int progress) {
		StaticImageProgressWidget.progress = progress;
	}
	
	
	
}
