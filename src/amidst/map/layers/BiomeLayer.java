package amidst.map.layers;

import MoF.MapViewer;
import amidst.Options;
import amidst.Util;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.ImageLayer;
import amidst.minecraft.Biome;

public class BiomeLayer extends ImageLayer {
	public static BiomeLayer instance;
	protected static int size = Fragment.SIZE >> 2;

	protected boolean[] selectedBiomes = new boolean[Biome.biomes.length];
	private boolean inHighlightMode = false;
	
	public BiomeLayer() {
		super(size);
		instance = this;
		deselectAllBiomes();
	}
	
	public void selectAllBiomes() {
		setSelectedAllBiomes(true);
	}
	public void deselectAllBiomes() {
		setSelectedAllBiomes(false);
	}
	
	public void selectBiome(int id) {
		setSelected(id, true);
	}
	public void deselectBiome(int id) { 
		setSelected(id, false);
	}

	public void setHighlightMode(boolean enabled) {
		inHighlightMode = enabled;
	}
	
	public void toggleBiomeSelect(int id) {
		setSelected(id, !selectedBiomes[id]);
	}
	public void setSelected(int id, boolean value) {
		selectedBiomes[id] = value;
	}
	
	public void setSelectedAllBiomes(boolean value) {
		for (int i = 0; i < selectedBiomes.length; i++)
			selectedBiomes[i] = value;
	}
	
	@Override
	public void drawToCache(Fragment fragment) {
		int[] dataCache = Fragment.getIntArray();
		if (inHighlightMode) {
			for (int i = 0; i < size*size; i++)
				if (!selectedBiomes[fragment.biomeData[i]])
					dataCache[i] = Util.deselectColor(Biome.biomes[fragment.biomeData[i]].color);
				else
					dataCache[i] = Biome.biomes[fragment.biomeData[i]].color;
		} else {
			for (int i = 0; i < size*size; i++)
				dataCache[i] = Biome.biomes[fragment.biomeData[i]].color;
		}

		
		fragment.setImageData(layerId, dataCache);
	}
	
	public static int getBiomeForFragment(Fragment frag, int blockX, int blockY) {
		return frag.biomeData[(blockY >> 2) * Fragment.BIOME_SIZE + (blockX >> 2)];
	}
	
	public static String getBiomeNameForFragment(Fragment frag, int blockX, int blockY) {
		return Biome.biomes[getBiomeForFragment(frag, blockX, blockY)].name;
	}
	public static String getBiomeAliasForFragment(Fragment frag, int blockX, int blockY) {
		return Options.instance.biomeColorProfile.getAliasForId(getBiomeForFragment(frag, blockX, blockY));
	}

	public boolean isBiomeSelected(int id) {
		return selectedBiomes[id];
	}
}
