package amidst.gui.main.viewer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ProgressMessageHolder {
	private volatile String progressMessage;

	@CalledByAny
	public void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String getProgressMessage() {
		return progressMessage;
	}
}
