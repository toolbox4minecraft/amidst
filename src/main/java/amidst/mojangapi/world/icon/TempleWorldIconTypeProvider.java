package amidst.mojangapi.world.icon;

import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

public class TempleWorldIconTypeProvider implements WorldIconTypeProvider {
	private final BiomeDataOracle biomeDataOracle;

	public TempleWorldIconTypeProvider(BiomeDataOracle biomeDataOracle) {
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	public DefaultWorldIconTypes get(int chunkX, int chunkY) {
		try {
			Biome biome = biomeDataOracle.getBiomeAtMiddleOfChunk(chunkX,
					chunkY);
			if (biome == Biome.swampland) {
				return DefaultWorldIconTypes.WITCH;
			} else if (biome.getName().contains("Jungle")) {
				return DefaultWorldIconTypes.JUNGLE;
			} else if (biome.getName().contains("Desert")) {
				return DefaultWorldIconTypes.DESERT;
			} else {
				Log.e("No known structure for this biome type: "
						+ biome.getName());
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
}
