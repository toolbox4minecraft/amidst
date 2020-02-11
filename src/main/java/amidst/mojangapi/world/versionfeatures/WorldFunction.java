package amidst.mojangapi.world.versionfeatures;

import amidst.mojangapi.world.oracle.BiomeDataOracle;

@FunctionalInterface
public interface WorldFunction<R> {
	public R apply(long seed, BiomeDataOracle biomeDataOracle);
}
