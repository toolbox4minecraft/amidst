package amidst.fragment.loader;

import java.util.function.Function;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;

@NotThreadSafe
public class WorldIconLoader<T> extends FragmentLoader {
	private final WorldIconProducer<T> producer;
	private final Function<Fragment, T> additionalDataExtractor;

	public WorldIconLoader(LayerDeclaration declaration, WorldIconProducer<T> producer) {
		this(declaration, producer, fragment -> null);
	}

	public WorldIconLoader(
			LayerDeclaration declaration,
			WorldIconProducer<T> producer,
			Function<Fragment, T> additionalDataExtractor) {
		super(declaration);
		this.producer = producer;
		this.additionalDataExtractor = additionalDataExtractor;
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
		fragment.putWorldIcons(
				declaration.getLayerId(),
				producer.getAt(fragment.getCorner(), additionalDataExtractor.apply(fragment)));
	}
}
