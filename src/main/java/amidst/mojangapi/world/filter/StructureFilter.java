package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.WorldIconCollector;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

public class StructureFilter extends BaseFilter {
	final int count;
	final DefaultWorldIconTypes structure;

	public StructureFilter(long worldFilterSize, String structureName, int count) {
		super(worldFilterSize);

		this.structure = DefaultWorldIconTypes.getByName(structureName);
		this.count = count;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean isValid(World world, short[][] region) {
		WorldIconCollector structureCollector = getCollector();
		@SuppressWarnings("rawtypes")
		WorldIconProducer structureProducer = getProducer(world);

		for (long x = 0; x < worldFilterSize; x += 512) {
			for (long y = 0; y < worldFilterSize; y += 512) {
				CoordinatesInWorld subCorner = CoordinatesInWorld.from(x, y).add(corner);
				structureProducer.produce(subCorner, structureCollector, null);
			}
		}

		return structureCollector.get().size() > count;
	}

	@SuppressWarnings("rawtypes")
	private WorldIconProducer getProducer(World world) {
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
			return new TypedWorldIconCollector(structure);
		case STRONGHOLD:
		case VILLAGE:
		case OCEAN_MONUMENT:
		case MINESHAFT:
			return new WorldIconCollector();
		default:
			throw new IllegalArgumentException("Unsupported structure type: " + structure.getName());
		}
	}

	private static class TypedWorldIconCollector extends WorldIconCollector {
		final DefaultWorldIconTypes acceptedStructure;

		TypedWorldIconCollector(DefaultWorldIconTypes acceptedStructure) {
			this.acceptedStructure = acceptedStructure;
		}

		@Override
		public void accept(WorldIcon worldIcon) {
			if (worldIcon.getName().equals(acceptedStructure.getName())) {
				super.accept(worldIcon);
			}
		}
	}
}