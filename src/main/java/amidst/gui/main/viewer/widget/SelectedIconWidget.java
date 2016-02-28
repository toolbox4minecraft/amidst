package amidst.gui.main.viewer.widget;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class SelectedIconWidget extends IconTextWidget {
	private final WorldIconSelection worldIconSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public SelectedIconWidget(CornerAnchorPoint anchor,
			WorldIconSelection worldIconSelection) {
		super(anchor);
		this.worldIconSelection = worldIconSelection;
		increaseYMargin(80);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected BufferedImage updateIcon() {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			return selection.getImage();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateMultilineText() {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			return Arrays.asList(selection.toString(true).split("\n"));
		} else {
			return null;
		}
	}
}
