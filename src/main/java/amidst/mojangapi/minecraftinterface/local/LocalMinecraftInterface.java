package amidst.mojangapi.minecraftinterface.local;

import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

public class LocalMinecraftInterface implements MinecraftInterface {
	
	private final RecognisedVersion recognisedVersion;
	
	public LocalMinecraftInterface(RecognisedVersion recognisedVersion) {
		this.recognisedVersion = recognisedVersion;
	}

	@Override
	public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	@Override
	public void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

}
