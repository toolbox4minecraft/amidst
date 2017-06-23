package amidst.util;

@FunctionalInterface
public interface BiomePredicate {
	public boolean test(int x, int y, short biome);
}