package amidst.map.layers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectStronghold;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.version.VersionInfo;

public class StrongholdLayer extends IconLayer {
	public static StrongholdLayer instance;
	
	private static final Biome[] biomesDefault = {
		Biome.desert, 
		Biome.forest, 
		Biome.extremeHills,
		Biome.swampland
	};
	private static final Biome[] biomes1_0 = {
		Biome.desert, 
		Biome.forest,
		Biome.extremeHills, 
		Biome.swampland, 
		Biome.taiga, 
		Biome.icePlains, 
		Biome.iceMountains
	};
	private static final Biome[] biomes1_1 = {
		Biome.desert, 
		Biome.forest, 
		Biome.extremeHills, 
		Biome.swampland, 
		Biome.taiga, 
		Biome.icePlains, 
		Biome.iceMountains, 
		Biome.desertHills, 
		Biome.forestHills, 
		Biome.extremeHillsEdge
	};
	private static final Biome[] biomes12w03a = {
		Biome.desert,
		Biome.forest, 
		Biome.extremeHills,
		Biome.swampland, 
		Biome.taiga, 
		Biome.icePlains, 
		Biome.iceMountains, 
		Biome.desertHills, 
		Biome.forestHills,
		Biome.extremeHillsEdge, 
		Biome.jungle, 
		Biome.jungleHills
	};
	
	private MapObjectStronghold[] strongholds = new MapObjectStronghold[3];
	
	public StrongholdLayer() {
		instance = this;
	}
	
	@Override
	public boolean isVisible() {
		return Options.instance.showStrongholds.get();		
	}
	
	@Override
	public void generateMapObjects(Fragment frag) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int chunkX = x + frag.getChunkX();
				int chunkY = y + frag.getChunkY();
				if (checkChunk(chunkX, chunkY)) { // TODO: This does not need a per-chunk test!
					// FIXME: Possible use of checkChunk causing negative icons to be misaligned!
					frag.addObject(new MapObjectStronghold(x << 4, y << 4).setParent(this));
				}
			}
		}
	}
	 
	public void findStrongholds() {
		Random random = new Random();
		random.setSeed(Options.instance.seed);
		
		
		// TODO: Replace this system!
		Biome[] validBiomes = biomesDefault;
		if (MinecraftUtil.getVersion() == VersionInfo.V1_9pre6 || MinecraftUtil.getVersion() == VersionInfo.V1_0)
			validBiomes = biomes1_0;
		if (MinecraftUtil.getVersion() == VersionInfo.V1_1)
			validBiomes = biomes1_1;
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w03a))
			validBiomes = biomes12w03a;

		List<Biome> biomeArrayList = Arrays.asList(validBiomes);
		
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V13w36a)) {
			biomeArrayList = new ArrayList<Biome>();
			for (int i = 0; i < Biome.biomes.length; i++) {
				if ((Biome.biomes[i] != null) && (Biome.biomes[i].type.value1 > 0f)) {
					biomeArrayList.add(Biome.biomes[i]);
				}
			}
		}
		
		double angle = random.nextDouble() * 3.141592653589793D * 2.0D;
		for (int i = 0; i < 3; i++) {
			double distance = (1.25D + random.nextDouble()) * 32.0D;
			int x = (int)Math.round(Math.cos(angle) * distance);
			int y = (int)Math.round(Math.sin(angle) * distance);


			
			Point strongholdLocation = MinecraftUtil.findValidLocation((x << 4) + 8, (y << 4) + 8, 112, biomeArrayList, random);
			if (strongholdLocation != null) {
				x = strongholdLocation.x >> 4;
				y = strongholdLocation.y >> 4;
			}
			strongholds[i] = new MapObjectStronghold((x << 4), (y << 4));
			angle += 6.283185307179586D / 3.0D;
		}
	}

	public boolean checkChunk(int chunkX, int chunkY) {
		for (int i = 0; i < 3; i++) {
			int strongholdChunkX = strongholds[i].x >> 4;
			int strongholdChunkY = strongholds[i].y >> 4;
			if ((strongholdChunkX == chunkX) && (strongholdChunkY == chunkY))
				return true;
		}
		return false;
	}
	
	public MapObjectStronghold[] getStrongholds() {
		return strongholds;
	}
	
	@Override
	public void reload() {
		findStrongholds();
	}
}
