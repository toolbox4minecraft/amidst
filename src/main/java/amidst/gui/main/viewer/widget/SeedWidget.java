package amidst.gui.main.viewer.widget;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.WorldSeed;

@NotThreadSafe
public class SeedWidget extends ImmutableTextWidget {
	public SeedWidget(CornerAnchorPoint anchor, WorldSeed worldSeed) {
		super(anchor, worldSeed.getLabel());
	}
}
