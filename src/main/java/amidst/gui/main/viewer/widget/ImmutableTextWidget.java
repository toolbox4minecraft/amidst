package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ImmutableTextWidget extends TextWidget {
	private List<String> textLines = null;

	@CalledOnlyBy(AmidstThread.EDT)
	public ImmutableTextWidget(CornerAnchorPoint anchor, String text) {
		super(anchor);
		this.textLines = Arrays.asList(text.split("\n"));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateMultilineText() {
		return textLines;
	}
}
