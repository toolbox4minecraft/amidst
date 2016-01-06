package amidst.mojangapi.world.icon;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public class NetherFortressProducer extends StructureProducer {
	public NetherFortressProducer(long seed) {
		super(Resolution.CHUNK, new NetherFortressAlgorithm(seed),
				new ImmutableWorldIconTypeProvider(
						DefaultWorldIconTypes.NETHER_FORTRESS), true);
	}
}
