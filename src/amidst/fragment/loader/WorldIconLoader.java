package amidst.fragment.loader;

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

	@Override
	public void load(Fragment fragment) {
		doLoad(fragment);
	}

	@Override
	public void reload(Fragment fragment) {
		doLoad(fragment);
	}

	protected void doLoad(Fragment fragment) {
		fragment.putWorldIcons(declaration.getLayerId(),
				producer.getAt(fragment.getCorner()));
	}
}