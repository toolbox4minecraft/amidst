package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ImmutableTextWidget extends TextWidget {
	private final List<String> textLines;

	@CalledOnlyBy(AmidstThread.EDT)
	public ImmutableTextWidget(CornerAnchorPoint anchor, String... textLines) {
		super(anchor);
		this.textLines = Arrays.asList(textLines);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		return textLines;
	}
}
