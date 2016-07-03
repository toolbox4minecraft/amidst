package amidst.gui.main.viewer.widget;

import amidst.ResourceLoader;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.viewer.BiomeSelection;

@NotThreadSafe
public class BiomeToggleWidget extends ImmutableIconWidget {
	private final BiomeSelection biomeSelection;
	private final LayerReloader layerReloader;

	@CalledOnlyBy(AmidstThread.EDT)
	public BiomeToggleWidget(CornerAnchorPoint anchor, BiomeSelection biomeSelection, LayerReloader layerReloader) {
		super(anchor, ResourceLoader.getImage("/amidst/gui/main/highlighter.png"));
		this.biomeSelection = biomeSelection;
		this.layerReloader = layerReloader;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMousePressed(int x, int y) {
		biomeSelection.toggleHighlightMode();
		layerReloader.reloadBackgroundLayer();
		return true;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
