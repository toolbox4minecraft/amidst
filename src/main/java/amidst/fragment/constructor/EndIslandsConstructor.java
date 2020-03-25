package amidst.fragment.constructor;

import java.util.Collections;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;

@Immutable
public class EndIslandsConstructor implements FragmentConstructor {
	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void construct(Fragment fragment) {
		long stamp = fragment.writeLock();
		try {
			fragment.setEndIslands(stamp, Collections.emptyList());
		} finally {
			fragment.unlock(stamp);
		}
	}
}
