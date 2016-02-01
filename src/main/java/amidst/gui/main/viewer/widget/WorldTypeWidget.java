package amidst.gui.main.viewer.widget;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.WorldType;

@NotThreadSafe
public class WorldTypeWidget extends ImmutableTextWidget {
	public WorldTypeWidget(CornerAnchorPoint anchor, WorldType worldType) {
		super(anchor, "World Type: " + worldType.getName());
		increaseYMargin(40);
	}
}
