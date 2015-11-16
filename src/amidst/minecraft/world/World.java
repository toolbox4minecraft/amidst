package amidst.minecraft.world;

public abstract class World {
	public boolean isFileWorld() {
		return this instanceof FileWorld;
	}

	public FileWorld getAsFileWorld() {
		return (FileWorld) this;
	}

	public abstract long getSeed();

	public abstract String getSeedText();

	public abstract WorldType getWorldType();
}
