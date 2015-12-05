package amidst.fragment.colorprovider;

import amidst.fragment.Fragment;

public interface ColorProvider {
	int getColorAt(Fragment fragment, long cornerX, long cornerY, int x, int y);
}
