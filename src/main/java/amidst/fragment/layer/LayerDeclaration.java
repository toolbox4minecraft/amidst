package amidst.fragment.layer;

import amidst.documentation.Immutable;
import amidst.settings.Setting;

@Immutable
public class LayerDeclaration {
	private final int layerId;
	private final boolean drawUnloaded;
	private final Setting<Boolean> isVisibleSetting;

	public LayerDeclaration(int layerId, boolean drawUnloaded,
			Setting<Boolean> isVisibleSetting) {
		this.layerId = layerId;
		this.drawUnloaded = drawUnloaded;
		this.isVisibleSetting = isVisibleSetting;
	}

	public int getLayerId() {
		return layerId;
	}

	public boolean isDrawUnloaded() {
		return drawUnloaded;
	}

	public boolean isVisible() {
		return isVisibleSetting.get();
	}
}
