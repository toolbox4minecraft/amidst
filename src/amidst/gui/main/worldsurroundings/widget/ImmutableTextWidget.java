package amidst.gui.main.worldsurroundings.widget;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ImmutableTextWidget extends TextWidget {
	private final String text;

	@CalledOnlyBy(AmidstThread.EDT)
	public ImmutableTextWidget(CornerAnchorPoint anchor, String text) {
		super(anchor);
		this.text = text;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		return text;
	}
}
