package amidst.util;

@FunctionalInterface
public interface BiomeFunction<T> {
	public T apply(int x, int y, short biome);
}
