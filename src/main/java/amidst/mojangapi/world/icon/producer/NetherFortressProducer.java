package amidst.mojangapi.world.icon.producer;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.NetherFortressAlgorithm;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;

@NotThreadSafe
public class NetherFortressProducer extends StructureProducer {
	public NetherFortressProducer(long seed) {
		super(Resolution.NETHER_CHUNK, new NetherFortressAlgorithm(seed),
				new ImmutableWorldIconTypeProvider(
						DefaultWorldIconTypes.NETHER_FORTRESS), true);
	}
}
