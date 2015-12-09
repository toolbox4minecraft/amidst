package amidst.fragment.colorprovider;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.Fragment;

public interface ColorProvider {
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	int getColorAt(Fragment fragment, long cornerX, long cornerY, int x, int y);
}
