package amidst.gui.widget;

import java.util.Arrays;
import java.util.List;

import amidst.Settings;
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
	private final Settings settings;

	public WidgetBuilder(World world, FragmentGraph graph,
			FragmentGraphToScreenTranslator translator, Zoom zoom,
			BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection, LayerReloader layerReloader,
			FragmentManager fragmentManager, Settings settings) {
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.biomeSelection = biomeSelection;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.fragmentManager = fragmentManager;
		this.settings = settings;
	}

	public List<Widget> create() {
		// @formatter:off
		return Arrays.asList(
				new FpsWidget(              CornerAnchorPoint.BOTTOM_LEFT,   new FramerateTimer(2),              settings.showFPS),
				new ScaleWidget(            CornerAnchorPoint.BOTTOM_CENTER, zoom,                               settings.showScale),
				new SeedWidget(             CornerAnchorPoint.TOP_LEFT,      world.getWorldSeed()),
				new DebugWidget(            CornerAnchorPoint.BOTTOM_RIGHT,  graph,             fragmentManager, settings.showDebug),
				new SelectedIconWidget(     CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				new CursorInformationWidget(CornerAnchorPoint.TOP_RIGHT,     graph,             translator),
				new BiomeToggleWidget(      CornerAnchorPoint.BOTTOM_RIGHT,  biomeSelection,    layerReloader),
				new BiomeWidget(            CornerAnchorPoint.NONE,          biomeSelection,    layerReloader,   settings.biomeColorProfileSelection)
		);
		// @formatter:on
	}
}
