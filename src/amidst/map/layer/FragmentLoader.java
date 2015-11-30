package amidst.map.layer;

import amidst.map.Fragment;

public interface FragmentLoader {
	void load(Fragment fragment);

	void reload(Fragment fragment);
}
