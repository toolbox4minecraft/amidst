package amidst.minetest.world.mapgen;

/**
 * Copy of Minetest's implementation of PcgRandom 
 */
public class PcgRandom {	
	public static final int RANDOM_MIN   = -0x7fffffff - 1;
	public static final int RANDOM_MAX   = 0x7fffffff;
	public static final int RANDOM_RANGE = 0xffffffff;

	/**
	 * m_state and m_inc are unsigned long ints, which Java does not possess, so 
	 * divide and shift operations on them must be performed unsigned 
	 *
	 * Note that in two's complement arithmetic, the arithmetic operations of 
	 * add, subtract, and multiply are bit-wise identical if the two operands 
	 * are regarded as both being signed or both being unsigned.
	 */
	private long m_state;
	private long m_inc;
	
	public int range(int min, int max)
	{
		if (max < min)
			throw new RuntimeException("Invalid range (max < min)");

		// We have to cast to s64 because otherwise this could overflow,
		// and signed overflow is undefined behavior.
		int bound = (int)((long)max - (long)min + 1);
		return range(bound) + min;
	}	
	
	public int range(int bound)
	{
		// If the bound is 0, we cover the whole RNG's range
		if (bound == 0)
			return next();

		/*
			This is an optimization of the expression:
			  0x100000000ull % bound
			since 64-bit modulo operations typically much slower than 32.
		
			int threshold = -bound % bound;
		*/
		
		// Skipping that optimization, as it doesn't work in Java with signed 32bit ints
		int threshold = (int)(0x100000000L % (long)bound);
		long r; // long to prevent values above 2^31 being treated as < threshold

		/*
			If the bound is not a multiple of the RNG's range, it may cause bias,
			e.g. a RNG has a range from 0 to 3 and we take want a number 0 to 2.
			Using rand() % 3, the number 0 would be twice as likely to appear.
			With a very large RNG range, the effect becomes less prevalent but
			still present.

			This can be solved by modifying the range of the RNG to become a
			multiple of bound by dropping values above the a threshold.

			In our example, threshold == 4 % 3 == 1, so reject values < 1
			(that is, 0), thus making the range == 3 with no bias.

			This loop may look dangerous, but will always terminate due to the
			RNG's property of uniformity.
		*/
		while ((r = next() & 0xFFFFFFFFL) < threshold)
			;

		return (int)(r % bound);
	}
	
	
	public int lua_next() {
		return range(RANDOM_MIN, RANDOM_MAX);
	}
	
	public int next()
	{
		long oldstate = m_state;
		m_state = oldstate * 6364136223846793005L + m_inc;
		int xorshifted = (int)(((oldstate >>> 18) ^ oldstate) >>> 27);
		int rot = (int)(oldstate >>> 59);		
		//return (xorshifted >>> rot) | (xorshifted << ((-rot) & 31));
		return Integer.rotateRight(xorshifted, rot);
	}	
	
	private void seed(long state, long seq) {
		m_state = 0;
		m_inc = (seq << 1L) | 1L;
		next();
		m_state += state;
		next();
	}
	
	public PcgRandom(long state) {
		// u64 state=0x853c49e6748fea9bULL, u64 seq=0xda3e39cb94b95bdbULL)
		seed(state, 0xda3e39cb94b95bdbL );		
	}
}
