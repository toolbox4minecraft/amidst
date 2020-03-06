package amidst.gui.main.viewer.widget;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.management.OperatingSystemMXBean;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@SuppressWarnings("restriction")
@NotThreadSafe
public class FpsWidget extends TextWidget {
	private final FramerateTimer fpsTimer;
	private final Setting<Boolean> isVisibleSetting;
	private final OperatingSystemMXBean operatingSystemMXBean;
	private final Timer usageTimer = new Timer("UsageTimer", true);
	private volatile double cpuLoad;

	@CalledOnlyBy(AmidstThread.EDT)
	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer, Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.isVisibleSetting = isVisibleSetting;
		this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		
		usageTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				cpuLoad = operatingSystemMXBean.getProcessCpuLoad() * 100;
			}
		}, 0, 500);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		fpsTimer.tick();
		if (isVisibleSetting.get()) {
			return Arrays.asList(
					"CPU: " + String.format("%.1f", cpuLoad) + "%",
					"FPS: " + String.format("%.1f", fpsTimer.getCurrentFPS()));
		} else {
			return null;
		}
	}
}
