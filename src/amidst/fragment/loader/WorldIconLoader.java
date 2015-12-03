package amidst.fragment.loader;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.FragmentGraphItem;
import amidst.minecraft.world.icon.WorldIconProducer;

public class WorldIconLoader extends FragmentLoader {
	private final WorldIconProducer producer;

	public WorldIconLoader(LayerDeclaration declaration,
			WorldIconProducer producer) {
		super(declaration);
		this.producer = producer;
	}

	@Override
	public void load(FragmentGraphItem fragment) {
		doLoad(fragment);
	}

	@Override
	public void reload(FragmentGraphItem fragment) {
		doLoad(fragment);
	}

	protected void doLoad(FragmentGraphItem fragment) {
		fragment.putWorldIcons(declaration.getLayerId(),
				producer.getAt(fragment.getCorner()));
	}
}
