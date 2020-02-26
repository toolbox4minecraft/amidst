package amidst.gui.main.viewer.widget;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@SuppressWarnings("restriction")
@NotThreadSafe
public class UsageWidget extends TextWidget {
	private final OperatingSystemMXBean operatingSystemMXBean;
	private final Setting<Boolean> isVisibleSetting;
	private final Timer usageTimer = new Timer("UsageTimer", true);
	private volatile double cpuLoad;

	@CalledOnlyBy(AmidstThread.EDT)
	public UsageWidget(CornerAnchorPoint anchor, int xOffset, int yOffset, Setting<Boolean> isVisibleSetting) {
		super(anchor, xOffset, yOffset);
		this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		this.isVisibleSetting = isVisibleSetting;
		
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
		if (isVisibleSetting.get()) {
			return Arrays.asList("CPU: " + String.format("%.1f", cpuLoad) + "%");
		} else {
			return null;
		}
	}
}
