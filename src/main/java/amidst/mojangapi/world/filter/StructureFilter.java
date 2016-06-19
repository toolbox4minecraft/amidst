package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;
import java.util.List;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.WorldIconCollector;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

public class StructureFilter extends BaseFilter {
	final int count;
	final DefaultWorldIconTypes structure;
	private final int multiWitchHutSearch;

	public StructureFilter(long worldFilterSize, String structureName, int count, String group, long scoreValue) {
		super(worldFilterSize);

		if (structureName.equals("Quad Witch Hut")) {
			this.multiWitchHutSearch = 4;
			this.structure = DefaultWorldIconTypes.getByLabel("Witch Hut");
		} else if (structureName.equals("Triple Witch Hut")) {
			this.multiWitchHutSearch = 3;
			this.structure = DefaultWorldIconTypes.getByLabel("Witch Hut");
		} else if (structureName.equals("Dual Witch Hut")) {
			this.multiWitchHutSearch = 2;
			this.structure = DefaultWorldIconTypes.getByLabel("Witch Hut");
		} else {
			this.multiWitchHutSearch = 0;
			this.structure = DefaultWorldIconTypes.getByLabel(structureName);
		}

		this.count = count;
		this.scoreValue = scoreValue;
		this.group = group;
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
				if (multiWitchHutSearch == 0 && structureCollector.get().size() >= count) {
					return true;
				} 
			}
		}

		if (multiWitchHutSearch > 0) {
			return checkMultiWitchHuts(structureCollector.get()) >= count;
		} else {
			return structureCollector.get().size() >= count;
		}
	}

	/**
	 * Iterate over the collection and then search for groups with a 256 max
	 * distance between the two farthest houses. The method below will pick up
	 * any configuration of huts where the max distance between any two is 256
	 * 
	 * @param structures
	 *            The list of structures to check
	 */
	private int checkMultiWitchHuts(List<WorldIcon> structures) {
		// The max distance allowable is 180 to make sure that
		// diagonals are still within the 256 distance max
		final long maxMultiStructDistance = 180;
		int ret = 0;

		for (WorldIcon s : structures) {
			for (WorldIcon s1 : structures) {
				// check range and for unique structure
				if (s != s1 && IsWithinRange(s, s1, maxMultiStructDistance)) {
					// if for dual witch huts
					if (multiWitchHutSearch == 2) {
						ret++;
					} else {
						for (WorldIcon s2 : structures) {
							// check range and for unique structure
							if (s != s2 && s1 != s2 && IsWithinRange(s, s2, maxMultiStructDistance)
									&& IsWithinRange(s1, s2, maxMultiStructDistance)) {
								// if for triple witch huts
								if (multiWitchHutSearch == 3) {
									ret++;
								} else {
									for (WorldIcon s3 : structures) {
										// check range and for unique structure
										if (s != s3 && s1 != s3 && s2 != s3
												&& IsWithinRange(s, s3, maxMultiStructDistance)
												&& IsWithinRange(s1, s3, maxMultiStructDistance)
												&& IsWithinRange(s2, s3, maxMultiStructDistance)) {
											// add to the return
											// as this is a quad
											ret++;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Checks to see that the passed structures are within the range specified
	 * 
	 * @param s1
	 *            The first structure
	 * @param s2
	 *            The second structure
	 * @param maxDistance
	 *            The maximum distance allowed
	 * @return true if the structures are within the range specified
	 */
	private boolean IsWithinRange(WorldIcon s1, WorldIcon s2, long maxDistance) {
		return Math.abs(s1.getCoordinates().getX() - s2.getCoordinates().getX()) < maxDistance
				&& Math.abs(s1.getCoordinates().getY() - s2.getCoordinates().getY()) < maxDistance;
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
			if (worldIcon.getName().equals(acceptedStructure.getLabel())) {
				super.accept(worldIcon);
			}
		}
	}
}