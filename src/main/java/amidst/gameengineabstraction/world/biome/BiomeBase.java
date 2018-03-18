package amidst.gameengineabstraction.world.biome;

import amidst.documentation.GsonConstructor;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.settings.biomeprofile.BiomeColorJson;

public abstract class BiomeBase implements IBiome {

	private String name;
	private volatile int index;
	private BiomeColor defaultColor;
	/** defaultColor from deserialization */
	private BiomeColorJson color;
		
	protected BiomeBase(String name, int index, BiomeColor defaultColor) {
		this.name = name;
		this.index = index;
		this.defaultColor = defaultColor;		
	}
	
	@GsonConstructor
	public BiomeBase() {
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getIndex() {
		return index;
	}
	
	/** Will need to used if instance has been deserialized */
	protected void setIndex(int index) {
		this.index = index;
	}
	
	
	@Override
	public boolean isSpecialBiome() {
		return index >= 128;
	}	

	public boolean validate() {
		return ((defaultColor != null) || (color != null)) &&
			   ((name != null) && (name.length() > 0));
	}
	
	public BiomeColor getDefaultColor() {
		if (defaultColor == null) {
			// Must have been deserialized rather than constructed
			defaultColor = BiomeColor.fromBiomeColorJson(color);			
		}
		return defaultColor;
	}

	@Override
	public String toString() {
		return "[Biome " + name + "]";
	}
}
