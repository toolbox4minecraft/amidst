package amidst.gui.main.viewer.widget;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

public abstract class OffsetWidget extends Widget {
	private volatile int xOffset, yOffset;

	protected OffsetWidget(CornerAnchorPoint anchor, int xOffset, int yOffset) {
		super(anchor);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void setX(int x) {
		super.setX(x + xOffset);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void setY(int y) {
		super.setY(y + yOffset);
	}
}
