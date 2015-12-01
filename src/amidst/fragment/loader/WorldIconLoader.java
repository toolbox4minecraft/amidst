package amidst.fragment.loader;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.minecraft.world.icon.WorldIconProducer;

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
		fragment.putWorldObjects(declaration.getLayerId(),
				producer.getAt(fragment.getCorner()));
	}
}
