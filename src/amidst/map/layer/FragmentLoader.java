package amidst.map.layer;

import amidst.map.Fragment;

public interface FragmentLoader {
	void construct(Fragment fragment);

	void load(Fragment fragment);

	void reload(Fragment fragment);
}
