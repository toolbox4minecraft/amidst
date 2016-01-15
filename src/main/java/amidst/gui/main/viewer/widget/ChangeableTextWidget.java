package amidst.gui.main.viewer.widget;

import java.util.function.Supplier;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ChangeableTextWidget extends TextWidget {
	private final Supplier<String> text;

	@CalledOnlyBy(AmidstThread.EDT)
	protected ChangeableTextWidget(CornerAnchorPoint anchor,
			Supplier<String> text) {
		super(anchor);
		this.text = text;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		return text.get();
	}
}
