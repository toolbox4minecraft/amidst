package amidst.mojangapi.world.icon.type;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class TempleWorldIconTypeProvider implements WorldIconTypeProvider<Void> {
	private final BiomeDataOracle biomeDataOracle;

	public TempleWorldIconTypeProvider(BiomeDataOracle biomeDataOracle) {
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	public DefaultWorldIconTypes get(int chunkX, int chunkY, Void additionalData) {
		try {
			Biome biome = biomeDataOracle.getBiomeAtMiddleOfChunk(chunkX, chunkY);
			if (biome == Biome.swampland) {
				return DefaultWorldIconTypes.WITCH;
			} else if (biome == Biome.jungle || biome == Biome.jungleHills) {
				return DefaultWorldIconTypes.JUNGLE;
			} else if (biome == Biome.desert || biome == Biome.desertHills) {
				return DefaultWorldIconTypes.DESERT;
			} else if (biome == Biome.icePlains || biome == Biome.coldTaiga) {
				return DefaultWorldIconTypes.IGLOO;
			} else {
				Log.e("No known structure for this biome type: " + biome.getName());
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
