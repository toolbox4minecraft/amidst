package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.icon.WorldIconProducer;

public class WorldIconLoader extends FragmentLoader {
	private final WorldIconProducer producer;

	public WorldIconLoader(LayerDeclaration declaration,
			WorldIconProducer producer) {
		super(declaration);
		this.producer = producer;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Fragment fragment) {
		doLoad(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void reload(Fragment fragment) {
		doLoad(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void doLoad(Fragment fragment) {
		fragment.putWorldIcons(declaration.getLayerId(),
				producer.getAt(fragment.getCorner()));
	}
}
