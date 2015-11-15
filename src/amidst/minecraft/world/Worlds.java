package amidst.minecraft.world;

import java.io.File;

public enum Worlds {
	;

	public static World random() {
		throw new UnsupportedOperationException("implement me!");
	}

	public static World fromSeedRandom() {
		throw new UnsupportedOperationException("implement me!");
	}

	public static World fromFile(File worldFile) throws Exception {
		WorldLoader worldLoader = new WorldLoader(worldFile);
		if (worldLoader.isLoadedSuccessfully()) {
			return worldLoader.get();
		} else {
			throw worldLoader.getException();
		}
	}
}
