package amidst.minetest.world.mapgen;

public class Noise {

	public static final int FLAG_DEFAULTS    = 0x01;
	public static final int FLAG_EASED       = 0x02;
	public static final int FLAG_ABSVALUE    = 0x04;
	public static final int FLAG_POINTBUFFER = 0x08;
	public static final int FLAG_SIMPLEX     = 0x10;
	
	public static final int MAGIC_X    = 1619;
	public static final int MAGIC_Y    = 31337;
	public static final int MAGIC_Z    = 52591;
	public static final int MAGIC_SEED = 1013;
	
	public NoiseParams np;
	private int seed;
	private int sx;
	private int sy;
	private int sz;
	private float[] noise_buf = null;
	private float[] gradient_buf = null;
	private float[] persist_buf = null;
	private float[] result = null;
	
	public Noise(NoiseParams np_, int seed, int sx, int sy) throws InvalidNoiseParamsException {
		
		this(np_, seed, sx, sy, 1);
	}

	public Noise(NoiseParams np_, int seed, int sx, int sy, int sz) throws InvalidNoiseParamsException {
		this.np   = np_;
		this.seed = seed;
		this.sx   = sx;
		this.sy   = sy;
		this.sz   = sz;		
		
		allocBuffers();
	}
	
	private void allocBuffers() throws InvalidNoiseParamsException 
	{
		if (sx < 1) sx = 1;
		if (sy < 1) sy = 1;
		if (sz < 1) sz = 1;

		this.noise_buf = null;		
		resizeNoiseBuf(sz > 1);

		gradient_buf = null;
		persist_buf = null;
		result = null;

		try {
			int bufsize = sx * sy * sz;
			persist_buf  = null;
			gradient_buf = new float[bufsize];
			result       = new float[bufsize];
		} catch (OutOfMemoryError e) {
			throw new InvalidNoiseParamsException("out of memory");
		}		
	}

	private void resizeNoiseBuf(boolean is3d) throws InvalidNoiseParamsException
	{
		//if (is3d) throw new UnsupportedOperationException("need to port 3d noise functions");		
		
		//maximum possible spread value factor
		float ofactor = (np.lacunarity > 1.0) ?
			(float)Math.pow(np.lacunarity, np.octaves - 1) :
			np.lacunarity;

		// noise lattice point count
		// (int)(sz * spread * ofactor) is # of lattice points crossed due to length
		float num_noise_points_x = sx * ofactor / np.spread.x;
		float num_noise_points_y = sy * ofactor / np.spread.y;
		float num_noise_points_z = sz * ofactor / np.spread.z;

		// protect against obviously invalid parameters
		if (num_noise_points_x > 1000000000.f ||
			num_noise_points_y > 1000000000.f ||
			num_noise_points_z > 1000000000.f)
			throw new InvalidNoiseParamsException();

		// + 2 for the two initial endpoints
		// + 1 for potentially crossing a boundary due to offset
		int nlx = (int)Math.ceil(num_noise_points_x) + 3;
		int nly = (int)Math.ceil(num_noise_points_y) + 3;
		int nlz = is3d ? (int)Math.ceil(num_noise_points_z) + 3 : 1;

		try {
			noise_buf = new float[nlx * nly * nlz];
		} catch (OutOfMemoryError e) {
			throw new InvalidNoiseParamsException("out of memory");
		}
	}
	
	
	public static float NoisePerlin2D(NoiseParams np, float x, float y, int seed)
	{
		float a = 0;
		float f = 1.0f;
		float g = 1.0f;

		x /= np.spread.x;
		y /= np.spread.y;
		seed += np.seed;

		for (int i = 0; i < np.octaves; i++) {
			float noiseval = noise2d_gradient(x * f, y * f, seed + i,
				(np.flags & (FLAG_DEFAULTS | FLAG_EASED)) > 0);

			if ((np.flags & FLAG_ABSVALUE) > 0)
				noiseval = Math.abs(noiseval);

			a += g * noiseval;
			f *= np.lacunarity;
			g *= np.persist;
		}

		return np.offset + a * np.scale;
	}	
	
	static float noise2d(int x, int y, int seed)
	{
		// n is an unsigned int, which Java does not possess, so 
		// divide and shift operations on it must be performed unsigned 
		//
		// Note that in two's complement arithmetic, the arithmetic operations of 
		// add, subtract, and multiply are bit-wise identical if the two operands 
		// are regarded as both being signed or both being unsigned.
		//
		// unsigned int n = (MAGIC_X * x + MAGIC_Y * y + MAGIC_SEED * seed) & 0x7fffffff;
		// n = (n >> 13) ^ n;
		// n = (n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff;
		// return 1.f - (float)(int)n / 0x40000000;
		int n = (MAGIC_X * x + MAGIC_Y * y + MAGIC_SEED * seed) & 0x7fffffff;
		n = (n >>> 13) ^ n;
		n = (n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff;
		return 1.f - (float)(int)n / 0x40000000; // ?? check this
	}
	
	static float noise2d_gradient(float x, float y, int seed, boolean eased)
	{
		// Calculate the integer coordinates
		int x0 = ((x) < 0.0 ? (int)(x) - 1 : (int)(x)); // x0 = myfloor(x)
		int y0 = ((y) < 0.0 ? (int)(y) - 1 : (int)(y)); // y0 = myfloor(y)
		// Calculate the remaining part of the coordinates
		float xl = x - (float)x0;
		float yl = y - (float)y0;
		// Get values for corners of square
		float v00 = noise2d(x0, y0, seed);
		float v10 = noise2d(x0+1, y0, seed);
		float v01 = noise2d(x0, y0+1, seed);
		float v11 = noise2d(x0+1, y0+1, seed);
		// Interpolate
		if (eased) {
			return biLinearInterpolation(v00, v10, v01, v11, xl, yl);
		}

		return biLinearInterpolationNoEase(v00, v10, v01, v11, xl, yl);
	}	
	
	static float biLinearInterpolation(
			float v00, float v10,
			float v01, float v11,
			float x, float y)
		{
			float tx = x * x * x * (x * (6.f * x - 15.f) + 10.f); // easeCurve(x);
			float ty = y * y * y * (y * (6.f * y - 15.f) + 10.f); // easeCurve(y);
			float u = v00 + (v10 - v00) * x; // linearInterpolation(v00, v10, x);
			float v = v01 + (v11 - v01) * x; // linearInterpolation(v01, v11, x);
			return      u + (v   -   u) * y; // linearInterpolation(u, v, y);
		}


	static float biLinearInterpolationNoEase(
			float v00, float v10,
			float v01, float v11,
			float x, float y)
	{
		float u = v00 + (v10 - v00) * x; // linearInterpolation(v00, v10, x);
		float v = v01 + (v11 - v01) * x; // linearInterpolation(v01, v11, x);
		return      u + (v   -   u) * y; // linearInterpolation(u, v, y);
	}	
}
