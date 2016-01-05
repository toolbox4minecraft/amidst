package amidst.gui.main.viewer.widget;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@NotThreadSafe
public class FpsWidget extends TextWidget {
	private final FramerateTimer fpsTimer;
	private final Setting<Boolean> isVisibleSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer,
			Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.isVisibleSetting = isVisibleSetting;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		fpsTimer.tick();
		if (isVisibleSetting.get()) {
			return "FPS: " + String.format("%.1f", fpsTimer.getCurrentFPS());
		} else {
			return null;
		}
	}
}
