package amidst.gui.main.worldsurroundings.widget;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.WorldSeed;

@NotThreadSafe
public class SeedWidget extends TextWidget {
	private final String text;

	@CalledOnlyBy(AmidstThread.EDT)
	public SeedWidget(CornerAnchorPoint anchor, WorldSeed seed) {
		super(anchor);
		this.text = seed.getLabel();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		return text;
	}
}
