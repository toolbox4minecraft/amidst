package amidst.gui.widget;

import java.util.Arrays;
import java.util.List;

import amidst.Options;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.widget.Widget.CornerAnchorPoint;
import amidst.gui.worldsurroundings.BiomeSelection;
import amidst.gui.worldsurroundings.Map;
import amidst.gui.worldsurroundings.MapViewer;
import amidst.gui.worldsurroundings.WorldIconSelection;
import amidst.gui.worldsurroundings.Zoom;
import amidst.minecraft.world.World;
import amidst.utilities.FramerateTimer;

public class WidgetBuilder {
	private final World world;
	private final Map map;
	private final BiomeSelection biomeSelection;
	private final WorldIconSelection worldIconSelection;
	private final LayerReloader layerReloader;
	private final FragmentGraph graph;
	private final Zoom zoom;
	private final FragmentManager fragmentManager;
	private final Options options;

	public WidgetBuilder(World world, Map map, BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection, LayerReloader layerReloader,
			FragmentGraph graph, Zoom zoom, FragmentManager fragmentManager,
			Options options) {
		this.world = world;
		this.map = map;
		this.biomeSelection = biomeSelection;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.graph = graph;
		this.zoom = zoom;
		this.fragmentManager = fragmentManager;
		this.options = options;
	}

	public List<Widget> create(MapViewer mapViewer) {
		// @formatter:off
		return Arrays.asList(
				new FpsWidget(              mapViewer, CornerAnchorPoint.BOTTOM_LEFT,   new FramerateTimer(2),              options.showFPS),
				new ScaleWidget(            mapViewer, CornerAnchorPoint.BOTTOM_CENTER, zoom,                               options.showScale),
				new SeedWidget(             mapViewer, CornerAnchorPoint.TOP_LEFT,      world),
				new DebugWidget(            mapViewer, CornerAnchorPoint.BOTTOM_RIGHT,  graph,             fragmentManager, options.showDebug),
				new SelectedIconWidget(     mapViewer, CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				new CursorInformationWidget(mapViewer, CornerAnchorPoint.TOP_RIGHT,     graph,             map),
				new BiomeToggleWidget(      mapViewer, CornerAnchorPoint.BOTTOM_RIGHT,  biomeSelection,    layerReloader),
				new BiomeWidget(            mapViewer, CornerAnchorPoint.NONE,          biomeSelection,    layerReloader,   options.biomeColorProfileSelection)
		);
		// @formatter:on
	}
}
