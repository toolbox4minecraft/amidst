package amidst.mojangapi.minecraftinterface;

import amidst.mojangapi.world.Dimension;

@SuppressWarnings("serial")
public class UnsupportedDimensionException extends MinecraftInterfaceException {

	public UnsupportedDimensionException(Dimension dimension) {
		super("The dimension " + dimension.getName() + " isn't supported by this MinecraftInterface");
	}
}
