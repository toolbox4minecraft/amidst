package amidst.fragment.layer;

import amidst.documentation.Immutable;
import amidst.settings.Setting;

@Immutable
public class LayerDeclaration {
	private final int layerId;
	private final Setting<Boolean> isVisiblePreference;

	public LayerDeclaration(int layerId, Setting<Boolean> isVisiblePreference) {
		this.layerId = layerId;
		this.isVisiblePreference = isVisiblePreference;
	}

	public int getLayerId() {
		return layerId;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}
}
