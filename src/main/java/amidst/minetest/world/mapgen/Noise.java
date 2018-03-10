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
	private int _seed;
	private int _sx;
	private int _sy;
	private int _sz;
	private float[] _noise_buf = null;
	private float[] _gradient_buf = null;
	private float[] _persist_buf = null;
	private float[] _result = null;
	
	public Noise(NoiseParams np_, int seed, int sx, int sy) throws InvalidNoiseParamsException {
		
		this(np_, seed, sx, sy, 1);
	}

	public Noise(NoiseParams np_, int seed, int sx, int sy, int sz) throws InvalidNoiseParamsException {
		this.np   = np_;
		this._seed = seed;
		this._sx   = sx;
		this._sy   = sy;
		this._sz   = sz;		
		
		allocBuffers();
	}
	
	private void allocBuffers() throws InvalidNoiseParamsException 
	{
		if (_sx < 1) _sx = 1;
		if (_sy < 1) _sy = 1;
		if (_sz < 1) _sz = 1;

		this._noise_buf = null;		
		resizeNoiseBuf(_sz > 1);

		_gradient_buf = null;
		_persist_buf = null;
		_result = null;

		try {
			int bufsize = _sx * _sy * _sz;
			_persist_buf  = null;
			_gradient_buf = new float[bufsize];
			_result       = new float[bufsize];
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
		float num_noise_points_x = _sx * ofactor / np.spread.x;
		float num_noise_points_y = _sy * ofactor / np.spread.y;
		float num_noise_points_z = _sz * ofactor / np.spread.z;

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
			_noise_buf = new float[nlx * nly * nlz];
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
	
	float noise3d(int x, int y, int z, int seed)
	{
		int n = (MAGIC_X * x + MAGIC_Y * y + MAGIC_Z * z
				+ MAGIC_SEED * seed) & 0x7fffffff;
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
			float u = v00 + (v10 - v00) * tx; // linearInterpolation(v00, v10, tx);
			float v = v01 + (v11 - v01) * tx; // linearInterpolation(v01, v11, tx);
			return      u + (v   -   u) * ty; // linearInterpolation(u, v, ty);
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
	
	float triLinearInterpolation(
			float v000, float v100, float v010, float v110,
			float v001, float v101, float v011, float v111,
			float x, float y, float z)
		{
			float tx = x * x * x * (x * (6.f * x - 15.f) + 10.f); // easeCurve(x);
			float ty = y * y * y * (y * (6.f * y - 15.f) + 10.f); // easeCurve(y);
			float tz = z * z * z * (z * (6.f * z - 15.f) + 10.f); // easeCurve(z);
			float u = biLinearInterpolationNoEase(v000, v100, v010, v110, tx, ty);
			float v = biLinearInterpolationNoEase(v001, v101, v011, v111, tx, ty);
			return u + (v - u) * tz; // linearInterpolation(u, v, tz);
		}

		float triLinearInterpolationNoEase(
			float v000, float v100, float v010, float v110,
			float v001, float v101, float v011, float v111,
			float x, float y, float z)
		{
			float u = biLinearInterpolationNoEase(v000, v100, v010, v110, x, y);
			float v = biLinearInterpolationNoEase(v001, v101, v011, v111, x, y);
			return u + (v - u) * z; // linearInterpolation(u, v, z);
		}
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param persistence_map can be null
	 * @return
	 */
	float[] perlinMap2D(float x, float y, float[] persistence_map)
	{
		float f = 1.0f, g = 1.0f;
		int bufsize = _sx * _sy;

		x /= np.spread.x;
		y /= np.spread.y;

		if (persistence_map != null) {
			if (_persist_buf == null) 
				_persist_buf = new float[bufsize];
			for (int i = 0; i != bufsize; i++)
				_persist_buf[i] = 1.0f;
		}

		for (int oct = 0; oct < np.octaves; oct++) {
			gradientMap2D(x * f, y * f,
				f / np.spread.x, f / np.spread.y,
				_seed + np.seed + oct);

			updateResults(g, _persist_buf, persistence_map, bufsize);

			f *= np.lacunarity;
			g *= np.persist;
		}

		if (Math.abs(np.offset - 0.f) > 0.00001 || Math.abs(np.scale - 1.f) > 0.00001) {
			for (int i = 0; i != bufsize; i++)
				_result[i] = _result[i] * np.scale + np.offset;
		}

		return _result;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param persistence_map can be null
	 * @return
	 */
	float[] perlinMap3D(float x, float y, float z, float[] persistence_map)
	{
		float f = 1.0f, g = 1.0f;
		int bufsize = _sx * _sy * _sz;

		x /= np.spread.x;
		y /= np.spread.y;
		z /= np.spread.z;

		if (persistence_map != null) {
			if (_persist_buf == null)
				_persist_buf = new float[bufsize];
			for (int i = 0; i != bufsize; i++)
				_persist_buf[i] = 1.0f;
		}

		for (int oct = 0; oct < np.octaves; oct++) {
			gradientMap3D(x * f, y * f, z * f,
				f / np.spread.x, f / np.spread.y, f / np.spread.z,
				_seed + np.seed + oct);

			updateResults(g, _persist_buf, persistence_map, bufsize);

			f *= np.lacunarity;
			g *= np.persist;
		}

		if (Math.abs(np.offset - 0.f) > 0.00001 || Math.abs(np.scale - 1.f) > 0.00001) {
			for (int i = 0; i != bufsize; i++)
				_result[i] = _result[i] * np.scale + np.offset;
		}

		return _result;
	}
	
	void updateResults(float g, float[] gmap,
			float[] persistence_map, int bufsize)
	{
		// This looks very ugly, but it is 50-70% faster than having
		// conditional statements inside the loop
		if ((np.flags & FLAG_ABSVALUE) > 0) {
			if (persistence_map != null) {
				for (int i = 0; i != bufsize; i++) {
					_result[i] += gmap[i] * Math.abs(_gradient_buf[i]);
					gmap[i] *= persistence_map[i];
				}
			} else {
				for (int i = 0; i != bufsize; i++)
					_result[i] += g * Math.abs(_gradient_buf[i]);
			}
		} else {
			if (persistence_map != null) {
				for (int i = 0; i != bufsize; i++) {
					_result[i] += gmap[i] * _gradient_buf[i];
					gmap[i] *= persistence_map[i];
				}
			} else {
				for (int i = 0; i != bufsize; i++)
					_result[i] += g * _gradient_buf[i];
			}
		}
	}

	void gradientMap2D(
			float x, float y,
			float step_x, float step_y,
			int seed)
	{
		float v00, v01, v10, v11, u, v, orig_u;
		int index, i, j, noisex, noisey;
		int nlx, nly;
		int x0, y0;

		boolean eased = (np.flags & (FLAG_DEFAULTS | FLAG_EASED)) > 0;

		x0 = (int)Math.floor(x);
		y0 = (int)Math.floor(y);
		u = x - (float)x0;
		v = y - (float)y0;
		orig_u = u;

		//calculate noise point lattice
		nlx = (int)(u + _sx * step_x) + 2;
		nly = (int)(v + _sy * step_y) + 2;
		index = 0;
		for (j = 0; j != nly; j++)
			for (i = 0; i != nlx; i++)
				_noise_buf[index++] = noise2d(x0 + i, y0 + j, seed);

		//calculate interpolations
		index  = 0;
		noisey = 0;
		for (j = 0; j != _sy; j++) {
			v00 = _noise_buf[(noisey) * nlx + 0];       // was idx(0, noisey), from #define idx(x, y) ((y) * nlx + (x))
			v10 = _noise_buf[(noisey) * nlx + 1];
			v01 = _noise_buf[(noisey + 1) * nlx + 0];
			v11 = _noise_buf[(noisey + 1) * nlx + 1];

			u = orig_u;
			noisex = 0;
			for (i = 0; i != _sx; i++) {
				if (eased) {
					_gradient_buf[index++] = biLinearInterpolation(v00, v10, v01, v11, u, v);
				} else {
					_gradient_buf[index++] = biLinearInterpolationNoEase(v00, v10, v01, v11, u, v);					
				}

				u += step_x;
				if (u >= 1.0) {
					u -= 1.0;
					noisex++;
					v00 = v10;
					v01 = v11;
					v10 = _noise_buf[(noisey) * nlx + (noisex + 1)];     // was idx(noisex + 1, noisey), from #define idx(x, y) ((y) * nlx + (x))
					v11 = _noise_buf[(noisey + 1) * nlx + (noisex + 1)];
				}
			}

			v += step_y;
			if (v >= 1.0) {
				v -= 1.0;
				noisey++;
			}
		}
	}
	
	void gradientMap3D(
			float x, float y, float z,
			float step_x, float step_y, float step_z,
			int seed)
	{
		float v000, v010, v100, v110;
		float v001, v011, v101, v111;
		float u, v, w, orig_u, orig_v;
		int index, i, j, k, noisex, noisey, noisez;
		int nlx, nly, nlz;
		int x0, y0, z0;

		boolean eased = (np.flags & FLAG_EASED) > 0; // triLinearInterpolation : triLinearInterpolationNoEase;

		x0 = (int)Math.floor(x);
		y0 = (int)Math.floor(y);
		z0 = (int)Math.floor(z);
		u = x - (float)x0;
		v = y - (float)y0;
		w = z - (float)z0;
		orig_u = u;
		orig_v = v;

		//calculate noise point lattice
		nlx = (int)(u + _sx * step_x) + 2;
		nly = (int)(v + _sy * step_y) + 2;
		nlz = (int)(w + _sz * step_z) + 2;
		index = 0;
		for (k = 0; k != nlz; k++)
			for (j = 0; j != nly; j++)
				for (i = 0; i != nlx; i++)
					_noise_buf[index++] = noise3d(x0 + i, y0 + j, z0 + k, seed);

		//calculate interpolations
		index  = 0;
		noisey = 0;
		noisez = 0;
		for (k = 0; k != _sz; k++) {
			v = orig_v;
			noisey = 0;
			for (j = 0; j != _sy; j++) {
				v000 = _noise_buf[(noisez    ) * nly * nlx + (noisey    ) * nlx + 0]; // was idx(0, noisey, noisez), from #define idx(x, y, z) ((z) * nly * nlx + (y) * nlx + (x))
				v100 = _noise_buf[(noisez    ) * nly * nlx + (noisey    ) * nlx + 1];
				v010 = _noise_buf[(noisez    ) * nly * nlx + (noisey + 1) * nlx + 0];
				v110 = _noise_buf[(noisez    ) * nly * nlx + (noisey + 1) * nlx + 1];
				v001 = _noise_buf[(noisez + 1) * nly * nlx + (noisey    ) * nlx + 0];
				v101 = _noise_buf[(noisez + 1) * nly * nlx + (noisey    ) * nlx + 1];
				v011 = _noise_buf[(noisez + 1) * nly * nlx + (noisey + 1) * nlx + 0];
				v111 = _noise_buf[(noisez + 1) * nly * nlx + (noisey + 1) * nlx + 1];

				u = orig_u;
				noisex = 0;
				for (i = 0; i != _sx; i++) {
					if (eased) {
						_gradient_buf[index++] = triLinearInterpolation(
							v000, v100, v010, v110,
							v001, v101, v011, v111,
							u, v, w);
					} else {
						_gradient_buf[index++] = triLinearInterpolationNoEase(
								v000, v100, v010, v110,
								v001, v101, v011, v111,
								u, v, w);						
					}

					u += step_x;
					if (u >= 1.0) {
						u -= 1.0;
						noisex++;
						v000 = v100;
						v010 = v110;
						v100 = _noise_buf[(noisez    ) * nly * nlx + (noisey    ) * nlx + (noisex + 1)]; // was idx(noisex + 1, noisey, noisez), from #define idx(x, y, z) ((z) * nly * nlx + (y) * nlx + (x))
						v110 = _noise_buf[(noisez    ) * nly * nlx + (noisey + 1) * nlx + (noisex + 1)];
						v001 = v101;
						v011 = v111;
						v101 = _noise_buf[(noisez + 1) * nly * nlx + (noisey    ) * nlx + (noisex + 1)];
						v111 = _noise_buf[(noisez + 1) * nly * nlx + (noisey + 1) * nlx + (noisex + 1)];
					}
				}

				v += step_y;
				if (v >= 1.0) {
					v -= 1.0;
					noisey++;
				}
			}

			w += step_z;
			if (w >= 1.0) {
				w -= 1.0;
				noisez++;
			}
		}
	}
	
	
	
}
