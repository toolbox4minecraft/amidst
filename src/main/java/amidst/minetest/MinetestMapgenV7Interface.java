package amidst.minetest;

import amidst.gameengineabstraction.GameEngineDetails;
import amidst.gameengineabstraction.GameEngineType;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;

public class MinetestMapgenV7Interface implements MinecraftInterface {

	private final GameEngineDetails engineDetails;
	
	public final MapgenV7Params params;	
	
	public MinetestMapgenV7Interface(MapgenV7Params params) {
		
		this.params = params;
		
		engineDetails = new GameEngineDetails(
				GameEngineType.MINETEST,
				new amidst.minetest.world.DefaultVersionFeatures()
		);
	}	
	
	@Override
	public int[] getBiomeData(int x, int y, int width, int height,
			boolean useQuarterResolution) throws MinecraftInterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createWorld(long seed, WorldType worldType,
			String generatorOptions) throws MinecraftInterfaceException {
		// TODO Auto-generated method stub

	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return RecognisedVersion.Minetest_0_5;
	}

	@Override
	public GameEngineDetails getGameEngineDetails() {
		return engineDetails;
	}			
}
