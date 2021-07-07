package amidst.gui.main.viewer.widget;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@SuppressWarnings("restriction")
@NotThreadSafe
public class CpuUsageTimer {
	private final OperatingSystemMXBean operatingSystemMXBean;
	
	private long lastTime;
	private long msPerUpdate;

	private float currentUsage = 0.0f;

	@CalledOnlyBy(AmidstThread.EDT)
	public CpuUsageTimer(int updatesPerSecond) {
		this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		msPerUpdate = (long) (1000d / updatesPerSecond);
		reset();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void reset() {
		lastTime = System.currentTimeMillis();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void tick() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime > msPerUpdate) {
			currentUsage = (float) (operatingSystemMXBean.getProcessCpuLoad() * 100);
			lastTime = currentTime;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public float getCurrentUsage() {
		return currentUsage;
	}
}
