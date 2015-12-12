package amidst.gui.widget;

import java.util.Arrays;
import java.util.List;

import amidst.Options;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.widget.Widget.CornerAnchorPoint;
import amidst.gui.worldsurroundings.BiomeSelection;
import amidst.gui.worldsurroundings.FragmentGraphToScreenTranslator;
import amidst.gui.worldsurroundings.WorldIconSelection;
import amidst.gui.worldsurroundings.Zoom;
import amidst.mojangapi.world.World;

public class WidgetBuilder {
	private final World world;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final BiomeSelection biomeSelection;
	private final WorldIconSelection worldIconSelection;
	private final LayerReloader layerReloader;
	private final FragmentManager fragmentManager;
	private final Options options;

	public WidgetBuilder(World world, FragmentGraph graph,
			FragmentGraphToScreenTranslator translator, Zoom zoom,
			BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection, LayerReloader layerReloader,
			FragmentManager fragmentManager, Options options) {
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.biomeSelection = biomeSelection;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.fragmentManager = fragmentManager;
		this.options = options;
	}

	public List<Widget> create() {
		// @formatter:off
		return Arrays.asList(
				new FpsWidget(              CornerAnchorPoint.BOTTOM_LEFT,   new FramerateTimer(2),              options.showFPS),
				new ScaleWidget(            CornerAnchorPoint.BOTTOM_CENTER, zoom,                               options.showScale),
				new SeedWidget(             CornerAnchorPoint.TOP_LEFT,      world.getWorldSeed()),
				new DebugWidget(            CornerAnchorPoint.BOTTOM_RIGHT,  graph,             fragmentManager, options.showDebug),
				new SelectedIconWidget(     CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				new CursorInformationWidget(CornerAnchorPoint.TOP_RIGHT,     graph,             translator),
				new BiomeToggleWidget(      CornerAnchorPoint.BOTTOM_RIGHT,  biomeSelection,    layerReloader),
				new BiomeWidget(            CornerAnchorPoint.NONE,          biomeSelection,    layerReloader,   options.biomeColorProfileSelection)
		);
		// @formatter:on
	}
}
