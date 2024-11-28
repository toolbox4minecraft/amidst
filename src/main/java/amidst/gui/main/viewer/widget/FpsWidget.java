package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@NotThreadSafe
public class FpsWidget extends TextWidget {
	private final FramerateTimer fpsTimer;
	private final CpuUsageTimer usageTimer;
	private final Setting<Boolean> isVisibleSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer, CpuUsageTimer usageTimer, Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.usageTimer = usageTimer;
		this.isVisibleSetting = isVisibleSetting;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		fpsTimer.tick();
		usageTimer.tick();
		if (isVisibleSetting.get()) {
			return Arrays.asList(
					"CPU: " + String.format("%.1f", usageTimer.getCurrentUsage()) + "%",
					"FPS: " + String.format("%.1f", fpsTimer.getCurrentFPS()));
		} else {
			return null;
		}
	}
}
