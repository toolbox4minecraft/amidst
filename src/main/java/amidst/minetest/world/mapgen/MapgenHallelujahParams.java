package amidst.minetest.world.mapgen;

import javax.vecmath.Vector3f;

import amidst.mojangapi.world.WorldType;

// TODO: be able to import these values from minetest.conf
public class MapgenHallelujahParams extends MapgenParams {
	
	public class CoreSize {
		public int territorySize; 
		public int coresPerTerritory;
		public int maxRadius;
		public int maxThickness;
		public float frequency;
		public boolean requiresNexus;
		public boolean exclusive;
		public float pondWallBuffer;
				
		public CoreSize(int territory_size, int cores_per_territory, int max_radius, int max_thickness, float frequency, float pond_wall_buffer, boolean requires_nexus, boolean exclusive) {
			this.territorySize = territory_size;
			this.coresPerTerritory = cores_per_territory;
			this.maxRadius = max_radius;
			this.maxThickness = max_thickness;
			this.frequency = frequency;
			this.requiresNexus = requires_nexus;
			this.exclusive = exclusive;
			this.pondWallBuffer = pond_wall_buffer;
		}
	}
	
	public CoreSize[] cores = new CoreSize[] {
			new CoreSize(200, 3, 96, 8, 0.1f, 0.03f, true, false),	
			new CoreSize(60,  1, 40, 4, 0.1f, 0.06f, false, true),	
			new CoreSize(30,  3, 16, 3, 0.1f, 0.11f, false, true)	
	};
	
	public short cloudlands_altitude  = 200;
	//public float cloudlands_altitude_amplitude    = 30f; // not needed for 2d map
	public float required_density     = 0.4f;	

	public NoiseParams np_eddyField  = new NoiseParams( -1f, 2,    new Vector3f(350, 350, 350), 1000, (short)2, 0.7f, 2f);
	public NoiseParams np_surfaceMap = new NoiseParams(0.5f, 0.5f, new Vector3f( 40,  40,  40), 1000, (short)4, 0.5f, 2f);
	public NoiseParams np_density    = new NoiseParams(0.7f, 0.3f, new Vector3f( 25,  25,  25), 1000, (short)4, 0.5f, 2f);
		       
   	@Override
   	public String toString() {   		
   		String prefix = "cloudlands_";
		StringBuilder result = new StringBuilder();
		result.append(super.toString());
		result.append(np_eddyField.toString( prefix + "np_eddyField"));
		result.append(np_surfaceMap.toString(prefix + "np_surfaceMap"));
		result.append(np_density.toString(   prefix + "np_density"));
        return result.toString();		
   	}
	       
   	@Override
	public WorldType getWorldType() {
		return WorldType.HALLELUJAH_MOUNTAINS;
	}		       
}
