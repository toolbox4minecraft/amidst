package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@NotThreadSafe
public class SeedAndWorldTypeWidget extends ImmutableTextWidget {
	public SeedAndWorldTypeWidget(CornerAnchorPoint anchor, WorldSeed worldSeed, WorldType worldType) {
		super(anchor, worldSeed.getLabel(), "World Type: " + worldType.getName());
	}

	/**
	 * Add some extra line spacing because the seed and world type are separate
	 * items, rather than multi-line paragraph text.
	 */
	@Override
	protected int getLineSeparationHeight(FontMetrics fontMetrics) {
		return (int) Math.round(super.getLineSeparationHeight(fontMetrics) * 1.2);
	}
}
