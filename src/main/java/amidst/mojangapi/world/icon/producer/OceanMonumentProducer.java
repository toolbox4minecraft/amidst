package amidst.mojangapi.world.icon.producer;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.OceanMonumentLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class OceanMonumentProducer extends StructureProducer {
	public OceanMonumentProducer(long seed, BiomeDataOracle biomeDataOracle) {
		super(Resolution.CHUNK, new OceanMonumentLocationChecker(seed,
				biomeDataOracle), new ImmutableWorldIconTypeProvider(
				DefaultWorldIconTypes.OCEAN_MONUMENT), false);
	}
}
