package amidst.gui.main.viewer.widget;

import java.awt.Point;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.gui.main.viewer.DimensionSelection;
import amidst.gui.main.viewer.FragmentGraphToScreenTranslator;
import amidst.logging.Log;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public class CursorInformationWidget extends TextWidget {
	private static final String UNKNOWN_BIOME_NAME = "Unknown";

	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final DimensionSelection dimensionSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public CursorInformationWidget(CornerAnchorPoint anchor,
			FragmentGraph graph, FragmentGraphToScreenTranslator translator,
			DimensionSelection dimensionSelection) {
		super(anchor);
		this.graph = graph;
		this.translator = translator;
		this.dimensionSelection = dimensionSelection;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		Point mousePosition = getMousePosition();
		if (mousePosition != null) {
			CoordinatesInWorld coordinates = translator
					.screenToWorld(mousePosition);
			String biomeName = getBiomeNameAt(coordinates);
			return biomeName + " " + coordinates.toString();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getBiomeNameAt(CoordinatesInWorld coordinates) {
		if (dimensionSelection.isDimension(Dimension.OVERWORLD)) {
			return getOverworldBiomeNameAt(coordinates);
		} else if (dimensionSelection.isDimension(Dimension.END)) {
			return Biome.theEnd.getName();
		} else {
			Log.w("unsupported dimension");
			return UNKNOWN_BIOME_NAME;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getOverworldBiomeNameAt(CoordinatesInWorld coordinates) {
		Fragment fragment = graph.getFragmentAt(coordinates);
		if (fragment != null && fragment.isLoaded()) {
			long x = coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
			long y = coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
			short biome = fragment.getBiomeDataAt((int) x, (int) y);
			try {
				return Biome.getByIndex(biome).getName();
			} catch (UnknownBiomeIndexException e) {
				Log.e(e.getMessage());
				e.printStackTrace();
				return UNKNOWN_BIOME_NAME;
			}
		} else {
			return UNKNOWN_BIOME_NAME;
		}
	}
}
