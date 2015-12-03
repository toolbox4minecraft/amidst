package amidst.map;

import java.util.Arrays;
import java.util.List;

import amidst.fragment.layer.LayerReloader;
import amidst.map.widget.BiomeToggleWidget;
import amidst.map.widget.BiomeWidget;
import amidst.map.widget.CursorInformationWidget;
import amidst.map.widget.DebugWidget;
import amidst.map.widget.FpsWidget;
import amidst.map.widget.ScaleWidget;
import amidst.map.widget.SeedWidget;
import amidst.map.widget.SelectedIconWidget;
import amidst.map.widget.Widget;
import amidst.map.widget.Widget.CornerAnchorPoint;
import amidst.minecraft.world.World;

public class WidgetBuilder {
	private final World world;
	private final Map map;
	private final BiomeSelection biomeSelection;
	private final WorldIconSelection worldIconSelection;
	private final LayerReloader layerReloader;
	private final FragmentGraph graph;
	private final Zoom zoom;

	public WidgetBuilder(World world, Map map, BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection, LayerReloader layerReloader,
			FragmentGraph graph, Zoom zoom) {
		this.world = world;
		this.map = map;
		this.biomeSelection = biomeSelection;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.graph = graph;
		this.zoom = zoom;
	}

	public List<Widget> create(MapViewer mapViewer) {
		// @formatter:off
		return Arrays.asList(
				new FpsWidget(              mapViewer, CornerAnchorPoint.BOTTOM_LEFT),
				new ScaleWidget(            mapViewer, CornerAnchorPoint.BOTTOM_CENTER, zoom),
				new SeedWidget(             mapViewer, CornerAnchorPoint.TOP_LEFT,      world),
				new DebugWidget(            mapViewer, CornerAnchorPoint.BOTTOM_RIGHT,  graph),
				new SelectedIconWidget(     mapViewer, CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				new CursorInformationWidget(mapViewer, CornerAnchorPoint.TOP_RIGHT,     map),
				new BiomeToggleWidget(      mapViewer, CornerAnchorPoint.BOTTOM_RIGHT,  biomeSelection, layerReloader),
				new BiomeWidget(            mapViewer, CornerAnchorPoint.NONE,          biomeSelection, layerReloader)
		);
		// @formatter:on
	}
}
