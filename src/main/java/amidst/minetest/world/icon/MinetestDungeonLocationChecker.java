package amidst.minetest.world.icon;

import javax.vecmath.Vector3f;

import amidst.documentation.ThreadSafe;
import amidst.fragment.IBiomeDataOracle;
import amidst.minetest.world.mapgen.Noise;
import amidst.minetest.world.mapgen.NoiseParams;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;

@ThreadSafe
public class MinetestDungeonLocationChecker implements LocationChecker {

	private IBiomeDataOracle biomeDataOracle;
	private int seed;	
	private NoiseParams nparams_dungeon_density;
	
	public MinetestDungeonLocationChecker(int seed, IBiomeDataOracle biomeDataOracle) {
		this.biomeDataOracle = biomeDataOracle;
		this.seed = seed;		
		
		nparams_dungeon_density = new NoiseParams(0.9f, 0.5f, new Vector3f(500, 500, 500),  0, (short)2,  0.8f, 2.0f);
	}

	@Override
	public boolean isValidLocation(int x, int z) {

		// TODO - find a heuristic for dungeons		
		float nval_density = Noise.NoisePerlin3D(nparams_dungeon_density, x << 2, 0f, z << 2, 0);
		return nval_density >=  1.0f;
	}
}
