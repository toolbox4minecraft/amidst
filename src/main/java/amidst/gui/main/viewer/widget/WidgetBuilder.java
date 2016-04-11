package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;

import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.FragmentGraphToScreenTranslator;
import amidst.gui.main.viewer.Graphics2DAccelerationCounter;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.gui.main.viewer.widget.Widget.CornerAnchorPoint;
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
	private final Graphics2DAccelerationCounter accelerationCounter;
	private final AmidstSettings settings;

	@CalledOnlyBy(AmidstThread.EDT)
	public WidgetBuilder(
			World world,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Zoom zoom,
			BiomeSelection biomeSelection,
			WorldIconSelection worldIconSelection,
			LayerReloader layerReloader,
			FragmentManager fragmentManager,
			Graphics2DAccelerationCounter accelerationCounter,
			AmidstSettings settings) {
		this.world = world;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.biomeSelection = biomeSelection;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.fragmentManager = fragmentManager;
		this.accelerationCounter = accelerationCounter;
		this.settings = settings;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public List<Widget> create() {
		// @formatter:off
		return Arrays.asList(
				new FpsWidget(              CornerAnchorPoint.BOTTOM_LEFT,   new FramerateTimer(2),              settings.showFPS),
				new ScaleWidget(            CornerAnchorPoint.BOTTOM_CENTER, zoom,                               settings.showScale),
				new SeedAndWorldTypeWidget( CornerAnchorPoint.TOP_LEFT,      world.getWorldSeed(), world.getWorldType()),
				new SelectedIconWidget(     CornerAnchorPoint.TOP_LEFT,      worldIconSelection),
				new DebugWidget(            CornerAnchorPoint.BOTTOM_RIGHT,  graph,             fragmentManager, settings.showDebug, accelerationCounter),
				new CursorInformationWidget(CornerAnchorPoint.TOP_RIGHT,     graph,             translator,      settings.dimension),
				new BiomeToggleWidget(      CornerAnchorPoint.BOTTOM_RIGHT,  biomeSelection,    layerReloader),
				new BiomeWidget(            CornerAnchorPoint.NONE,          biomeSelection,    layerReloader,   settings.biomeProfileSelection)
		);
		// @formatter:on
	}
}
