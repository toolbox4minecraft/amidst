package amidst.gui.main.viewer.widget;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.gui.main.viewer.FragmentGraphToScreenTranslator;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.Setting;

@NotThreadSafe
public class CursorInformationWidget extends TextWidget {
	private static final String UNKNOWN_BIOME_NAME = "Unknown";

	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Setting<Dimension> dimensionSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public CursorInformationWidget(
			CornerAnchorPoint anchor,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Setting<Dimension> dimensionSetting) {
		super(anchor);
		this.graph = graph;
		this.translator = translator;
		this.dimensionSetting = dimensionSetting;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		Point mousePosition = getMousePosition();
		if (mousePosition != null) {
			Coordinates coordinates = translator.screenToWorld(mousePosition);
			String biomeName = getBiomeNameAt(coordinates);
			return Arrays.asList(biomeName + " " + coordinates.toString());
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getBiomeNameAt(Coordinates coordinates) {
		Dimension dimension = dimensionSetting.get();
		if (dimension.equals(Dimension.OVERWORLD)) {
			return getOverworldBiomeNameAt(coordinates);
		} else if (dimension.equals(Dimension.END)) {
			return Biome.theEnd.getName();
		} else {
			AmidstLogger.warn("unsupported dimension");
			return UNKNOWN_BIOME_NAME;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getOverworldBiomeNameAt(Coordinates coordinates) {
		Fragment fragment = graph.getFragmentAt(coordinates);
		if (fragment != null && fragment.isLoaded()) {
			Coordinates c = coordinates.getRelativeTo(Resolution.FRAGMENT).getAs(Resolution.QUARTER);
			short biome = fragment.getBiomeDataAt(c.getX(), c.getY());
			try {
				return Biome.getByIndex(biome).getName();
			} catch (UnknownBiomeIndexException e) {
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
				return UNKNOWN_BIOME_NAME;
			}
		} else {
			return UNKNOWN_BIOME_NAME;
		}
	}
}
