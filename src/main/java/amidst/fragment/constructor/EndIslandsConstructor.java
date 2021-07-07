package amidst.fragment.constructor;

import java.util.Collections;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.oracle.end.EndIslandList;

@Immutable
public class EndIslandsConstructor implements FragmentConstructor {
	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void construct(Fragment fragment) {
		fragment.setEndIslands(new EndIslandList(Collections.emptyList(), Collections.emptyList()));
	}
}
