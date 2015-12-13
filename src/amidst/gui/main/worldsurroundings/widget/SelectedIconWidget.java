package amidst.gui.main.worldsurroundings.widget;

import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.worldsurroundings.WorldIconSelection;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class SelectedIconWidget extends IconTextWidget {
	private final WorldIconSelection worldIconSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public SelectedIconWidget(CornerAnchorPoint anchor,
			WorldIconSelection worldIconSelection) {
		super(anchor);
		this.worldIconSelection = worldIconSelection;
		increaseYMargin(40);
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
	protected String updateText() {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			return selection.toString();
		} else {
			return null;
		}
	}
}
