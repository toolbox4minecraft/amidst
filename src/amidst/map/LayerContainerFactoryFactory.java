package amidst.map;

import amidst.Options;
import amidst.minecraft.world.World;

public class LayerContainerFactoryFactory {
	private final Options options;

	public LayerContainerFactoryFactory(Options options) {
		this.options = options;
	}

	public LayerContainerFactory create(World world, Map map) {
		return new LayerContainerFactory(options, world, map);
	}
}
