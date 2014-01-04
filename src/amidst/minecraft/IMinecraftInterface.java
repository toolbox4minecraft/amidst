package amidst.minecraft;

import amidst.version.VersionInfo;

/**
 * Acts as an additional layer of abstraction for interfacing with Minecraft.<br>
 * This allows for other sources of data other than direct reflection against a loaded jar of Minecraft.
 */
public interface IMinecraftInterface {
	public int[] getBiomeData(int x, int y, int width, int height);
	public void createWorld(long seed, String type);
	public VersionInfo getVersion();
}
