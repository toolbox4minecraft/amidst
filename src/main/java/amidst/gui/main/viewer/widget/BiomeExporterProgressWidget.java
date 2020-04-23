package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;

import amidst.settings.Setting;

public class BiomeExporterProgressWidget extends ProgressWidget {
	private volatile int xOffset;
	private final Setting<Boolean> showDebug;
	private final DebugWidget debugWidget;
	private final int iconWidth;
	
	public BiomeExporterProgressWidget(CornerAnchorPoint anchor, int xOffset, Setting<Boolean> showDebug, DebugWidget debugWidget, int iconWidth) {
		super(anchor, xOffset, 0, 0, 100, -1, 150, 25);
		this.xOffset = xOffset;
		this.showDebug = showDebug;
		this.debugWidget = debugWidget;
		this.iconWidth = iconWidth;
	}

	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		setXOffset(showDebug.get() ? xOffset - debugWidget.getWidth() : xOffset - iconWidth);
	}
	
}
