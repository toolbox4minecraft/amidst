package amidst.mojangapi.world.filter;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.producer.NameFilteredWorldIconCollector;
import amidst.mojangapi.world.icon.producer.WorldIconCollector;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

@Immutable
public class WorldFilter_Structure extends WorldFilter {
	private final DefaultWorldIconTypes structure;
	private final int count;

	public WorldFilter_Structure(long worldFilterSize, DefaultWorldIconTypes structure, int count) {
		super(worldFilterSize);
		this.structure = structure;
		this.count = count;
	}

	@Override
	public boolean isValid(World world) {
		WorldIconCollector structureCollector = getCollector();
		procudeAndCollect(getProducer(world), structureCollector);
		return structureCollector.get().size() > count;
	}

	private void procudeAndCollect(WorldIconProducer<Void> structureProducer, WorldIconCollector structureCollector) {
		for (long x = 0; x < 2 * worldFilterSize; x += 512) {
			for (long y = 0; y < 2 * worldFilterSize; y += 512) {
				structureProducer.produce(CoordinatesInWorld.from(x, y).add(corner), structureCollector, null);
			}
		}
	}

	private WorldIconProducer<Void> getProducer(World world) {
		switch (structure) {
		case JUNGLE:
		case DESERT:
		case IGLOO:
		case WITCH:
			return world.getTempleProducer();
		case STRONGHOLD:
			return world.getStrongholdProducer();
		case VILLAGE:
			return world.getVillageProducer();
		case OCEAN_MONUMENT:
			return world.getOceanMonumentProducer();
		case MINESHAFT:
			return world.getMineshaftProducer();
		default:
			throw new IllegalArgumentException("Unsupported structure type: " + structure.getName());
		}
	}

	private WorldIconCollector getCollector() {
		switch (structure) {
		case JUNGLE:
		case DESERT:
		case IGLOO:
		case WITCH:
			return new NameFilteredWorldIconCollector(structure.getName());
		case STRONGHOLD:
		case VILLAGE:
		case OCEAN_MONUMENT:
		case MINESHAFT:
			return new WorldIconCollector();
		default:
			throw new IllegalArgumentException("Unsupported structure type: " + structure.getName());
		}
	}
}
