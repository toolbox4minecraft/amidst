package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class TempleProducer extends StructureProducer {
	private final LocationChecker checker;

	public TempleProducer(RecognisedVersion recognisedVersion, long seed,
			BiomeDataOracle biomeDataOracle) {
		super(biomeDataOracle);
		this.checker = new TempleLocationChecker(seed, biomeDataOracle,
				recognisedVersion);
	}

	@Override
	protected boolean isValidLocation() {
		return checker.isValidLocation(chunkX, chunkY);
	}

	@Override
	protected DefaultWorldIconTypes getWorldIconType() {
		try {
			Biome chunkBiome = biomeDataOracle.getBiomeAtMiddleOfChunk(chunkX,
					chunkY);
			if (chunkBiome == Biome.swampland) {
				return DefaultWorldIconTypes.WITCH;
			} else if (chunkBiome.getName().contains("Jungle")) {
				return DefaultWorldIconTypes.JUNGLE;
			} else if (chunkBiome.getName().contains("Desert")) {
				return DefaultWorldIconTypes.DESERT;
			} else {
				Log.e("No known structure for this biome type: "
						+ chunkBiome.getName());
				return null;
			}
		} catch (UnknownBiomeIndexException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (MinecraftInterfaceException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected boolean displayNetherCoordinates() {
		return false;
	}
}
