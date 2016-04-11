package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.WorldIconImage;

@NotThreadSafe
public class SelectedIconWidget extends IconTextWidget {
	private final WorldIconSelection worldIconSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public SelectedIconWidget(CornerAnchorPoint anchor, WorldIconSelection worldIconSelection) {
		super(anchor);
		this.worldIconSelection = worldIconSelection;
		increaseYMargin(65);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected WorldIconImage updateIcon() {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			return selection.getImage();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			return Arrays.asList(selection.toString(true).split("\n"));
		} else {
			return null;
		}
	}
}
