package amidst.mojangapi.world.icon.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;

import static amidst.mojangapi.world.icon.type.DefaultWorldIconTypes.END_GATEWAY;
import static amidst.mojangapi.world.icon.type.DefaultWorldIconTypes.POSSIBLE_END_GATEWAY;;

@ThreadSafe
public class EndGatewayProducer extends WorldIconProducer<List<EndIsland>> {
	private static final int END_GATEWAY_CHANCE = 700;
	private static final Resolution RESOLUTION = Resolution.CHUNK;
	private static final int SIZE = RESOLUTION.getStepsPerFragment();
	
	/* 
	 * We add a buffer room of 16 blocks to go into nearby fragments for if it
	 * moves out of the original one during tryGetValidLocationFromChunk. This
	 * does slow it down a bit but it allows us to put the icons in the right
	 * fragments.
	 */
	private static final int BUFFER_SIZE = (int) RESOLUTION.convertFromWorldToThis(16);
	
	/**
	 * The influence has to be atleast 40 for End Highlands biomes to spawn,
	 * which is where End Gateways are.
	 */
	private static final float REQUIRED_BIOME_INFLUENCE = 40.0F;
	
	/**
	 * Used as a cache only for the spawn gateways.
	 */
	private final EndSpawnGatewayProducer spawnProducer;
	private final long seed;

	public EndGatewayProducer(long seed, EndIslandOracle oracle) {
		this.spawnProducer = new EndSpawnGatewayProducer(oracle);
		this.seed = seed;
	}

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, List<EndIsland> endIslands) {
		for (int xRelativeToFragment = -BUFFER_SIZE; xRelativeToFragment < SIZE + BUFFER_SIZE; xRelativeToFragment++) {
			for (int yRelativeToFragment = -BUFFER_SIZE; yRelativeToFragment < SIZE + BUFFER_SIZE; yRelativeToFragment++) {
				generateAt(corner, consumer, endIslands, xRelativeToFragment, yRelativeToFragment);
			}
		}
		spawnProducer.produce(corner, consumer, null);
	}

	private void generateAt(
			CoordinatesInWorld corner,
			Consumer<WorldIcon> consumer,
			List<EndIsland> endIslands,
			long xRelativeToFragment,
			long yRelativeToFragment) {
		long x = xRelativeToFragment + corner.getXAs(RESOLUTION);
		long y = yRelativeToFragment + corner.getYAs(RESOLUTION);
		CoordinatesInWorld possibleCoordinates = tryGetValidLocationFromChunk(x, y, endIslands, corner);
		if (possibleCoordinates != null) {
				consumer.accept(
						new WorldIcon(
								possibleCoordinates,
								END_GATEWAY.getLabel(),
								END_GATEWAY.getImage(),
								Dimension.END,
								false));
		}
	}
	
	/**
	 * Made with help from
	 * <a href=https://github.com/KaptainWutax/>KaptainWutax</a>.
	 */
	public CoordinatesInWorld tryGetValidLocationFromChunk(long chunkX, long chunkY, List<EndIsland> endIslands, CoordinatesInWorld corner) {
		if((chunkX * chunkX + chunkY * chunkY) > 4096) {
			ChunkRand rand = new ChunkRand();
			long blockX = chunkX << 4;
			long blockY = chunkY << 4;
			
			rand.setDecoratorSeed(seed, (int) blockX, (int) blockY, 0, 3, MCVersion.v1_13);
			
			if(rand.nextInt(END_GATEWAY_CHANCE) == 0) {
				for(EndIsland island : endIslands) {
					float biomeInfluence = island.influenceAtChunk(chunkX, chunkY);
					if(biomeInfluence >= REQUIRED_BIOME_INFLUENCE) {
						long gatewayX = rand.nextInt(16) + blockX;
						long gatewayY = rand.nextInt(16) + blockY;
						CoordinatesInWorld coordinates = new CoordinatesInWorld(gatewayX, gatewayY);
						if(coordinates.isInBoundsOf(corner, Fragment.SIZE)) {
							// While this barely ever is false due to the biome influence check, we do it anyway just to make sure
							float placementInfluence = island.influenceAtBlock(gatewayX, gatewayY);
							if(placementInfluence > 0.0F) {
								return coordinates;
							}
						}
					}
				}
			}
		} else {
		}
		
		return null;
	}
	
	private static class EndSpawnGatewayProducer extends CachedWorldIconProducer {
		private static final int NUMBER_OF_SPAWN_GATEWAYS = 20;
		
		private final EndIslandOracle oracle;
		
		public EndSpawnGatewayProducer(EndIslandOracle oracle) {
			this.oracle = oracle;
		}
		
		@Override
		protected List<WorldIcon> doCreateCache() {
			List<WorldIcon> iconList = new ArrayList<WorldIcon>();
			for(int i = 0; i < NUMBER_OF_SPAWN_GATEWAYS; i++) {
				// Generate inner WorldIcon
				int x = floor(96.0D * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double) i)));
				int y = floor(96.0D * Math.sin(2.0D * (-Math.PI + 0.15707963267948966D * (double) i)));
				CoordinatesInWorld possibleCoordinates = new CoordinatesInWorld(x, y);
				iconList.add(new WorldIcon(
									possibleCoordinates,
									POSSIBLE_END_GATEWAY.getLabel(),
									POSSIBLE_END_GATEWAY.getImage(),
									Dimension.END,
									false
								)
							);
				
				// Generate outer WorldIcon
				// Normalize the coordinates the same way MC would
				double doubleX = 0;
				double doubleY = 0;
				double d0 = (double) ((float) (Math.sqrt((double) x * (double) x + (double) y * (double) y))); // There are this many casts in the MC code
				if (d0 >= 1.0E-4D) {
					doubleX = (double) x / d0;
					doubleY = (double) y / d0;
				}
				// Scale the coordinates
				CoordinatesInWorld coordinates = new CoordinatesInWorld((long) (doubleX * 1024.0D), (long) (doubleY * 1024.0D));
				
				// Check for closest land along vector
				for (int j = 16; !isChunkIslandlessSlow(coordinates) && j-- > 0; coordinates = coordinates.add((long) (doubleX * -16.0D), (long) (doubleY * -16.0D))) {
				}
				
				for (int k = 16; isChunkIslandlessSlow(coordinates) && k-- > 0; coordinates = coordinates.add((long) (doubleX * 16.0D), (long) (doubleY * 16.0D))) {
				}
				
				// Mess with the coords a bit more
				coordinates = findSpawnpoint(coordinates, true);
				coordinates = findHighestBlock(coordinates, 16);
				
				iconList.add(new WorldIcon(
						coordinates,
						POSSIBLE_END_GATEWAY.getLabel(),
						POSSIBLE_END_GATEWAY.getImage(),
						Dimension.END,
						false
					)
				);
			}
			return iconList;
		}
		
		/*
		 * This number is supposed to sort of guess whether end
		 * stone might be at a particular location. 
		 */
		private static final float ISLAND_INFLUENCE_THRESHOLD = -20.0F;

		@SuppressWarnings("unused")
		private boolean isChunkIslandlessFast(CoordinatesInWorld blockCoords) {
			for(EndIsland island : oracle.getAt(blockCoords)) {
				if(island.influenceAtChunk(blockCoords.getX() >> 4, blockCoords.getY() >> 4) >= ISLAND_INFLUENCE_THRESHOLD) {
					return false;
				}
			}
			return true;
		}

		private boolean isChunkIslandlessSlow(CoordinatesInWorld blockCoords) {
			for(EndIsland island : oracle.getAt(blockCoords)) {
				for (long x = blockCoords.getX() & -16; x < (blockCoords.getX() | 15); x++) {
					for (long y = blockCoords.getY() & -16; y < (blockCoords.getY() | 15); y++) {
						if(island.influenceAtBlock(x, y) >= ISLAND_INFLUENCE_THRESHOLD) {
							return false;
						}
					}
				}
			}
			return true;
		}
		
		private CoordinatesInWorld findSpawnpoint(CoordinatesInWorld blockCoords, boolean guaranteeEndStone) {
			for (long y = blockCoords.getY() & -16; y < (blockCoords.getY() | 15); y++) {
				for (long x = blockCoords.getX() & -16; x < (blockCoords.getX() | 15); x++) {
					for (EndIsland island : oracle.getAt(blockCoords)) {
						if (island.influenceAtBlock(x, y) >= (guaranteeEndStone ? 0.0F : ISLAND_INFLUENCE_THRESHOLD)) {
							return new CoordinatesInWorld(x, y);
						}
					}
				}
			}
			return blockCoords;
		}

		private CoordinatesInWorld findHighestBlock(CoordinatesInWorld blockCoords, int radius) {
			return findHighestBlock(blockCoords, -radius, -radius, radius, radius);
		}

		private CoordinatesInWorld findHighestBlock(CoordinatesInWorld blockCoords, int startX, int startY, int endX, int endY) {
			float highestInfluence = -100.0F;
			long highestX = blockCoords.getX();
			long highestY = blockCoords.getY();
			
			for (long x = blockCoords.getX() + startX; x <= blockCoords.getX() + endX; ++x) {
				for (long y = blockCoords.getY() + startY; y <= blockCoords.getY() + endY; ++y) {
					float coordInfluence = oracle.getInfluenceAtBlock(x, y);
					if(coordInfluence > highestInfluence) {
						highestInfluence = coordInfluence;
						highestX = x;
						highestY = y;
					}
				}
			}
			return new CoordinatesInWorld(highestX, highestY);
		}

		private static int floor(double value) {
			int i = (int) value;
			return value < (double) i ? i - 1 : i;
		}
	}
	
}
