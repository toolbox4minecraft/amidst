package MoF;

public class PixelInfo {
	private int biome, x, y;
	private MapObject obj;
	private Fragment frag;
	public PixelInfo(int x, int y, int biome) {
		this.x = x;
		this.y = y;
		this.biome = biome;
	}
	public PixelInfo(int x, int y, Fragment frag) {
		this.x = x;
		this.y = y;
		this.frag = frag;
		this.biome = frag.getBiomeAt(x - frag.x*Project.FRAGMENT_SIZE, y - frag.y*Project.FRAGMENT_SIZE);
	}
	public int getChunkX() {
		return (x >> 2);
	}
	public int getChunkY() {
		return (y >> 2);
	}
	public int getBlockX() {
		return (x << 2);
	}
	public int getBlockY() {
		return (y << 2);
	}
	public int getBiome() {
		return biome;
	}
	public void setBiome(int biome) {
		this.biome = biome;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public MapObject getObject() {
		return obj;
	}

	public void setObject(MapObject obj) {
		this.obj = obj;
	}

	public Fragment getFrag() {
		return frag;
	}

	public void setFrag(Fragment frag) {
		this.frag = frag;
	}
}
