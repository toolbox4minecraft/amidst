package amidst.util;

import amidst.documentation.NotThreadSafe;

/**
 * Reimplementation of {@code java.util.Random} without the thread safety,
 * improving the performance.
 *
 * See the original implementation <a href="http://hg.openjdk.java.net/jdk10/jdk10/jdk/file/777356696811/src/java.base/share/classes/java/util/Random.java">here</a>
 */
@NotThreadSafe
public class FastRand {

    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long ADDEND = 0xBL;
    private static final long MODULUS = 1L << 48;

    private static final double DOUBLE_UNIT = 0x1.0p-53;

    private long seed;

	public FastRand(long seed) {
		this.seed = initialScramble(seed);
	}

	private long initialScramble(long seed) {
		return (seed ^ MULTIPLIER) & MODULUS - 1;
	}

	public void setSeed(long seed) {
		this.seed = initialScramble(seed);
	}

	public long mod(long n) {
		return n & (MODULUS - 1);
	}

	public void advance() {
		seed = this.mod(seed * MULTIPLIER + ADDEND);
	}

	/**
	 * Stolen from <a
	 * href=https://github.com/KaptainWutax/SeedUtils/blob/master/src/main/java/kaptainwutax/seedutils/lcg/LCG.java#L80>here<a>
	 */
	public void advance(int steps) {
        long multiplier = 1;
        long addend = 0;

        long intermediateMultiplier = MULTIPLIER;
        long intermediateAddend = ADDEND;

        for(long k = steps; k != 0; k >>>= 1) {
            if((k & 1) != 0) {
                multiplier *= intermediateMultiplier;
                addend = intermediateMultiplier * addend + intermediateAddend;
            }

            intermediateAddend = (intermediateMultiplier + 1) * intermediateAddend;
            intermediateMultiplier *= intermediateMultiplier;
        }

        multiplier = this.mod(multiplier);
        addend = this.mod(addend);

        seed = this.mod(seed * multiplier + addend);
	}

	private int next(int bits) {
		advance();
		return (int) (seed >>> 48 - bits);
    }

	public int nextInt(int bound) {
		if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");

        int r = next(31);
        int m = bound - 1;

        if ((bound & m) == 0) { // i.e., bound is a power of 2
            r = (int) ((bound * (long) r) >> 31);
        } else {
        	int u = r;
        	while (u - (r = u % bound) + m < 0) {
                u = next(31);
        	}
        }

        return r;
	}

	public long nextLong() {
		 return ((long) (next(32)) << 32) + next(32);
	}

	public float nextFloat() {
		return next(24) / ((float) (1 << 24));
	}

	public double nextDouble() {
		return (((long) (next(26)) << 27) + next(27)) * DOUBLE_UNIT;
	}
}
