package amidst.fragment.layer;

import amidst.documentation.Immutable;
import amidst.settings.Setting;

@Immutable
public class LayerDeclaration {
	private final int layerId;
	private final Setting<Boolean> isVisibleSetting;

	public LayerDeclaration(int layerId, Setting<Boolean> isVisibleSetting) {
		this.layerId = layerId;
		this.isVisibleSetting = isVisibleSetting;
	}

	public int getLayerId() {
		return layerId;
	}

	public boolean isVisible() {
		return isVisibleSetting.get();
	}
}
