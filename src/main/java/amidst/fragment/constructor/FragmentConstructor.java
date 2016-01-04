package amidst.fragment.constructor;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;

@Immutable
public interface FragmentConstructor {
	@CalledOnlyBy(AmidstThread.EDT)
	void construct(Fragment fragment);
}
