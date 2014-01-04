package amidst.minecraft.remote;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import amidst.minecraft.Biome;
import amidst.minecraft.IMinecraftInterface;
import amidst.version.VersionInfo;

public class RemoteMinecraft implements IMinecraftInterface {
	Client client;
	static NetGetBiomeDataResult currentResults = null;
	
	public RemoteMinecraft(String address) {
		client = new Client(65536, 65536);
		Kryo kryo = client.getKryo();
		kryo.register(NetCreateWorldRequest.class);
		kryo.register(NetGetBiomeDataRequest.class);
		kryo.register(NetGetBiomeDataResult.class);
		kryo.register(NetBiome.class);
		kryo.register(NetBiome[].class);
		kryo.register(NetInfoRequest.class);
		kryo.register(int[].class);
		
		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof NetGetBiomeDataResult) {
					currentResults = (NetGetBiomeDataResult)object;
					//Log.i("Received NetGetBiomeDataResult: " + currentResults);
				} else if (object instanceof NetBiome[]) {
					NetBiome[] biomes = (NetBiome[])object;
					for (int i = 0; i < biomes.length; i++) {
						if (biomes[i] != null) {
							new Biome(biomes[i].name, biomes[i].id, biomes[i].color | 0xFF000000, true);
						}
					}
				}
			}
		});
		
		client.start();
		try {
			client.connect(5000, address, 54580, 54580);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		client.sendTCP(new NetInfoRequest());
		
	}
	
	@Override
	public int[] getBiomeData(int x, int y, int width, int height) {
		//Log.i("Send NetGetBiomeDataRequest");
		client.sendTCP(new NetGetBiomeDataRequest(x, y, width, height));
		while (currentResults == null) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Log.i("Passed to getBiomeData");
		int[] data = currentResults.data;
		currentResults = null;
		return data;
	}

	@Override
	public void createWorld(long seed, String type) {
		client.sendTCP(new NetCreateWorldRequest(seed));
	}

	@Override
	public VersionInfo getVersion() {
		return VersionInfo.unknown;
	}
}
