package amidst.fragment.layer;

import amidst.documentation.Immutable;
import amidst.preferences.PrefModel;

@Immutable
public class LayerDeclaration {
	private final int layerId;
	private final PrefModel<Boolean> isVisiblePreference;

	public LayerDeclaration(int layerId, PrefModel<Boolean> isVisiblePreference) {
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
