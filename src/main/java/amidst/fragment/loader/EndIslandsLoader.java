package amidst.fragment.loader;

import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;

//TODO: use longs?
@NotThreadSafe
public class EndIslandsLoader extends FragmentLoader {
	private final EndIslandOracle endIslandOracle;

	@CalledByAny
	public EndIslandsLoader(LayerDeclaration declaration, EndIslandOracle endIslandOracle) {
		super(declaration);
		this.endIslandOracle = endIslandOracle;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Dimension dimension, Fragment fragment) {
		doLoad(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void reload(Dimension dimension, Fragment fragment) {
		doLoad(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void doLoad(Fragment fragment) {
		fragment.setEndIslands(getEndIslands(fragment.getCorner()));
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private List<EndIsland> getEndIslands(CoordinatesInWorld corner) {
		return endIslandOracle.getAt(corner);
	}
}
