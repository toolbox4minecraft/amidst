package amidst.mojangapi.world.icon.producer;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.VillageLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@NotThreadSafe
public class VillageProducer extends StructureProducer {
	public VillageProducer(long seed, BiomeDataOracle biomeDataOracle) {
		super(Resolution.CHUNK, new VillageLocationChecker(seed,
				biomeDataOracle), new ImmutableWorldIconTypeProvider(
				DefaultWorldIconTypes.VILLAGE), false);
	}
}
