package amidst.fragment.colorprovider;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;

public interface ColorProvider {
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y);
}
