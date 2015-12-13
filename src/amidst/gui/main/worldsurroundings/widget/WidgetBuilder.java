package amidst.gui.main.worldsurroundings.widget;

import java.util.Arrays;
import java.util.List;

import amidst.Settings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.worldsurroundings.BiomeSelection;
import amidst.gui.main.worldsurroundings.FragmentGraphToScreenTranslator;
import amidst.gui.main.worldsurroundings.WorldIconSelection;
import amidst.gui.main.worldsurroundings.Zoom;
import amidst.gui.main.worldsurroundings.widget.Widget.CornerAnchorPoint;
import amidst.mojangapi.world.World;

@NotThreadSafe
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

	@CalledOnlyBy(AmidstThread.EDT)
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

	@CalledOnlyBy(AmidstThread.EDT)
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
