package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class ChangeableTextWidget extends TextWidget {
	private final Supplier<String> text;

	@CalledOnlyBy(AmidstThread.EDT)
	public ChangeableTextWidget(CornerAnchorPoint anchor, Supplier<String> text) {
		super(anchor);
		this.text = text;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		String currentText = text.get();
		if (currentText == null) {
			return Collections.emptyList();
		} else {
			return Arrays.asList(text.get());
		}
	}
}
