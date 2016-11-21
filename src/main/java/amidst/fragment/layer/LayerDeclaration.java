package amidst.fragment.layer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.settings.Setting;

@NotThreadSafe
public class LayerDeclaration {
	private final int layerId;
	private final Dimension dimension;
	private final boolean isDrawUnloaded;
	private final boolean isSupportedInCurrentVersion;
	private final Setting<Boolean> isVisibleSetting;
	private final Setting<Boolean> enableAllLayersSetting;

	private volatile boolean isVisible;

	/**
	 * @param dimension Can be null to enable for all dimensions.
	 */
	public LayerDeclaration(
			int layerId,
			Dimension dimension,
			boolean drawUnloaded,
			boolean isSupportedInCurrentVersion,
			Setting<Boolean> isVisibleSetting,
			Setting<Boolean> enableAllLayersSetting) {
		this.layerId = layerId;
		this.dimension = dimension;
		this.isDrawUnloaded = drawUnloaded;
		this.isSupportedInCurrentVersion = isSupportedInCurrentVersion;
		this.isVisibleSetting = isVisibleSetting;
		this.enableAllLayersSetting = enableAllLayersSetting;
	}

	public int getLayerId() {
		return layerId;
	}

	public boolean isDrawUnloaded() {
		return isDrawUnloaded;
	}

	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Updates the isVisible and isEnabled fields to the current setting values.
	 * Returns whether the layer becomes visible.
	 */
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public boolean update(Dimension dimension) {
		boolean isEnabled = calculateIsEnabled(dimension, enableAllLayersSetting.get());
		boolean isVisible = isEnabled && isVisibleSetting.get();
		boolean reload = isVisible == true && this.isVisible == false;
		this.isVisible = isVisible;
		return reload;
	}

	@CalledByAny
	public boolean calculateIsEnabled(Dimension dimension, boolean enableAllLayers) {
		return isMatchingDimension(dimension) && isMatchingVersion(enableAllLayers);
	}

	@CalledByAny
	private boolean isMatchingDimension(Dimension dimension) {
		return this.dimension == null || this.dimension.equals(dimension);
	}

	@CalledByAny
	private boolean isMatchingVersion(boolean enabledAllLayers) {
		return isSupportedInCurrentVersion || enabledAllLayers;
	}
}
