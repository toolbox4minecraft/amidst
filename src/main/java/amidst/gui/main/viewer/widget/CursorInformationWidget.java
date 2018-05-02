package amidst.gui.main.viewer.widget;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.gameengineabstraction.CoordinateSystem;
import amidst.gui.main.viewer.FragmentGraphToScreenTranslator;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.Setting;
import amidst.settings.biomeprofile.BiomeAuthority;

@NotThreadSafe
public class CursorInformationWidget extends TextWidget {
	private static final String UNKNOWN_BIOME_NAME = "Unknown";

	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Setting<Dimension> dimensionSetting;
	private final BiomeAuthority biomeAuthority;
	private CoordinateSystem displayCoordSystem;

	@CalledOnlyBy(AmidstThread.EDT)
	public CursorInformationWidget(
			CornerAnchorPoint anchor,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Setting<Dimension> dimensionSetting,
			BiomeAuthority biomeAuthority) {
		super(anchor);
		this.graph = graph;
		this.translator = translator;
		this.dimensionSetting = dimensionSetting;
		this.biomeAuthority = biomeAuthority;
		
		displayCoordSystem = CoordinateSystem.RIGHT_HANDED; // Until we know better
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		Point mousePosition = getMousePosition();
		if (mousePosition != null) {
			CoordinatesInWorld coordinates = translator.screenToWorld(mousePosition);
			String biomeName = getBiomeNameAt(coordinates);
			return Arrays.asList(biomeName + " " + coordinates.toString(displayCoordSystem));
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getBiomeNameAt(CoordinatesInWorld coordinates) {
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
	private String getOverworldBiomeNameAt(CoordinatesInWorld coordinates) {
		Fragment fragment = graph.getFragmentAt(coordinates);
		if (fragment != null && fragment.isLoaded()) {		
			long x = coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
			long y = coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
			short biome = fragment.getBiomeIndexAt((int) x, (int) y);
			displayCoordSystem = fragment.getBiomeDataCoordinateSystem();
			try {
				return biomeAuthority.getBiomeByIndex(biome).getName();
			} catch (UnknownBiomeIndexException e) {
				if (biome == MinetestBiome.VOID.getIndex()) {
					// Special case
					return MinetestBiome.VOID.getName();
				}
				
				// This can happen legitimately now, as changing the biomeprofile in minetest (not minecraft)
				// causes the biome data to be recalculated with the new biomes, which might now be fewer than 
				// the index of the soon-to-be-updated biome your mouse is currently hovering over.
				AmidstLogger.warn("Cursor over unknown biome: biome " + biome + " currently doesn't exist");
				//AmidstLogger.error(e.getMessage());
				//AmidstMessageBox.displayError("Error", e);
				return UNKNOWN_BIOME_NAME;
			}
		} else {
			return UNKNOWN_BIOME_NAME;
		}
	}
}
