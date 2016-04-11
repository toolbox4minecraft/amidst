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
	private final Setting<Boolean> isVisibleSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer, Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.isVisibleSetting = isVisibleSetting;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		fpsTimer.tick();
		if (isVisibleSetting.get()) {
			return Arrays.asList("FPS: " + String.format("%.1f", fpsTimer.getCurrentFPS()));
		} else {
			return null;
		}
	}
}
