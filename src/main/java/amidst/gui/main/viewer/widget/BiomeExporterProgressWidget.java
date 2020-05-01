package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.util.Map.Entry;
import java.util.function.Supplier;

import amidst.settings.Setting;

public class BiomeExporterProgressWidget extends ProgressWidget {
	private volatile int xOffset;
	private final Setting<Boolean> showDebug;
	private final DebugWidget debugWidget;
	private final int iconWidth;
	
	public BiomeExporterProgressWidget(CornerAnchorPoint anchor, Supplier<Entry<ProgressEntryType, Integer>> progressEntrySupplier, int xOffset, Setting<Boolean> showDebug, DebugWidget debugWidget, int iconWidth) {
		super(anchor, progressEntrySupplier, xOffset, 0, 0, 100, -1, 150, 25);
		this.xOffset = xOffset;
		this.showDebug = showDebug;
		this.debugWidget = debugWidget;
		this.iconWidth = iconWidth;
	}

	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		super.doUpdate(fontMetrics, time);
		setXOffset(showDebug.get() ? xOffset - debugWidget.getWidth() : xOffset - iconWidth);
	}
	
}
