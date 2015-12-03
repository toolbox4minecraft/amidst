package amidst.fragment.colorprovider;

import amidst.map.FragmentGraphItem;

public interface ColorProvider {
	int getColorAt(FragmentGraphItem fragment, long cornerX, long cornerY, int x, int y);
}
