package amidst.mojangapi.world.icon.producer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.oracle.end.EndIslandList;
import amidst.mojangapi.world.oracle.end.EndIslandOracle;
import amidst.mojangapi.world.oracle.end.LargeEndIsland;
import amidst.mojangapi.world.oracle.end.SmallEndIsland;
import amidst.util.FastRand;
import static amidst.mojangapi.world.icon.type.DefaultWorldIconTypes.END_GATEWAY;
import static amidst.mojangapi.world.icon.type.DefaultWorldIconTypes.POSSIBLE_END_GATEWAY;;

@ThreadSafe
public class EndGatewayProducer extends WorldIconProducer<EndIslandList> {
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
	private final long worldSeed;
	
	private final boolean generateDecoratorGateways;
	private final int featureIndex;
	private final int generationStage;

	public EndGatewayProducer(long worldSeed, int featureIndex, int generationStage, EndIslandOracle oracle) {
		this.spawnProducer = new EndSpawnGatewayProducer(oracle);
		this.worldSeed = worldSeed;
		this.featureIndex = featureIndex;
		this.generationStage = generationStage;
		this.generateDecoratorGateways = true;
	}
	
	public EndGatewayProducer(long worldSeed, EndIslandOracle oracle) {
		this.spawnProducer = new EndSpawnGatewayProducer(oracle);
		this.worldSeed = worldSeed;
		this.featureIndex = 0;
		this.generationStage = 0;
		this.generateDecoratorGateways = false;
	}

	@Override
	public void produce(CoordinatesInWorld corner, Consumer<WorldIcon> consumer, EndIslandList endIslands) {
		if (generateDecoratorGateways) {
			// The buffer only needs to account for positive changes because it's not possible for it to move backwards out of the fragment.
			for (int xRelativeToFragment = -BUFFER_SIZE; xRelativeToFragment < SIZE; xRelativeToFragment++) {
				for (int yRelativeToFragment = -BUFFER_SIZE; yRelativeToFragment < SIZE; yRelativeToFragment++) {
					generateAt(corner, consumer, endIslands, xRelativeToFragment, yRelativeToFragment);
				}
			}
		}
		spawnProducer.produce(corner, consumer, null);
	}

	private void generateAt(
			CoordinatesInWorld corner,
			Consumer<WorldIcon> consumer,
			EndIslandList endIslands,
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
	public CoordinatesInWorld tryGetValidLocationFromChunk(long chunkX, long chunkY, EndIslandList endIslands, CoordinatesInWorld corner) {
		
		if((chunkX * chunkX + chunkY * chunkY) > 4096) {
			long blockX = chunkX << 4;
			long blockY = chunkY << 4;
			
			FastRand rand = new FastRand(worldSeed);
			long a = rand.nextLong() | 1L;
			long b = rand.nextLong() | 1L;
			long populationSeed = (long)(int) blockX * a + (long)(int) blockY * b ^ worldSeed; // we do the long -> int -> long conversion to replicate what minecraft does.
			long decoratorSeed = populationSeed + featureIndex + 10000 * generationStage;
			rand.setSeed(decoratorSeed);
			
			if(rand.nextInt(END_GATEWAY_CHANCE) == 0) {
				for(LargeEndIsland largeIsland : endIslands.getLargeIslands()) {
					float biomeInfluence = largeIsland.influenceAtChunk(chunkX, chunkY);
					if(biomeInfluence >= REQUIRED_BIOME_INFLUENCE) {
						long gatewayX = rand.nextInt(16) + blockX;
						long gatewayY = rand.nextInt(16) + blockY;
						CoordinatesInWorld coordinates = new CoordinatesInWorld(gatewayX, gatewayY);
						if(coordinates.isInBoundsOf(corner, Fragment.SIZE)) {
							// While this barely ever is false due to the biome influence check, we do it anyway just to make sure
							float placementInfluence = largeIsland.influenceAtBlock(gatewayX, gatewayY);
							if(placementInfluence > 0.0F) {
								return coordinates;
							} else {
								List<SmallEndIsland> smallIslands = endIslands.getSmallIslands();
								if(smallIslands != null) {
									// If this check fails, there's a very small chance that it landed on a small island
									for(SmallEndIsland smallIsland : smallIslands) {
										if(smallIsland.isOnIsland(gatewayX, gatewayY)) {
											return coordinates;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private static class EndSpawnGatewayProducer extends CachedWorldIconProducer {
		private static final int NUMBER_OF_SPAWN_GATEWAYS = 20;
		
		private final EndIslandOracle oracle;
		
		public EndSpawnGatewayProducer(EndIslandOracle oracle) {
			this.oracle = oracle;
		}
		
		// I'm sorry for writing this. It's slow, ambiguous, and inaccurate.
		// It's the best we have though, and I'm done with messing with this
		// method.
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
			for(LargeEndIsland island : oracle.getLargeIslandsAt(blockCoords)) {
				if(island.influenceAtChunk(blockCoords.getX() >> 4, blockCoords.getY() >> 4) >= ISLAND_INFLUENCE_THRESHOLD) {
					return false;
				}
			}
			return true;
		}

		private boolean isChunkIslandlessSlow(CoordinatesInWorld blockCoords) {
			EndIslandList endIslands = oracle.getAt(blockCoords);
			
			List<SmallEndIsland> smallIslands = endIslands.getSmallIslands();
			if(smallIslands != null) {
				// Small Islands
				for(SmallEndIsland island : smallIslands) {
					for (long x = blockCoords.getX() & -16; x < (blockCoords.getX() | 15); x++) {
						for (long y = blockCoords.getY() & -16; y < (blockCoords.getY() | 15); y++) {
							if(island.isOnIsland(x, y)) {
								return false;
							}
						}
					}
				}
			}
			
			// Large Islands
			for(LargeEndIsland island : endIslands.getLargeIslands()) {
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
			EndIslandList endIslands = oracle.getAt(blockCoords);
			for (long y = blockCoords.getY() & -16; y < (blockCoords.getY() | 15); y++) {
				for (long x = blockCoords.getX() & -16; x < (blockCoords.getX() | 15); x++) {
					// We do the large end islands first because they tend to be not as high
					for (LargeEndIsland island : endIslands.getLargeIslands()) {
						if (island.influenceAtBlock(x, y) >= (guaranteeEndStone ? 0.0F : ISLAND_INFLUENCE_THRESHOLD)) {
							return new CoordinatesInWorld(x, y);
						}
					}
					
					List<SmallEndIsland> smallIslands = endIslands.getSmallIslands();
					if(smallIslands != null) {
						// We want the lowest small end islands first, so we sort them.
						smallIslands.sort(((Comparator<SmallEndIsland>)(e1, e2) -> Integer.compare(e1.getHeight(), e2.getHeight())).reversed());
						for (SmallEndIsland island : smallIslands) {
							if (island.isOnIsland(x, y)) {
								return new CoordinatesInWorld(x, y);
							}
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
			int highestBlock = 0;
			long highestX = blockCoords.getX();
			long highestY = blockCoords.getY();
			EndIslandList endIslands = oracle.getAt(blockCoords);
			
			for (long x = blockCoords.getX() + startX; x <= blockCoords.getX() + endX; ++x) {
				for (long y = blockCoords.getY() + startY; y <= blockCoords.getY() + endY; ++y) {
					float coordInfluence = EndIslandOracle.getInfluenceAtBlock(x, y, endIslands.getLargeIslands());
					if(coordInfluence > highestInfluence) {
						highestInfluence = coordInfluence;
						highestX = x;
						highestY = y;
					}
				}
			}
			
			List<SmallEndIsland> smallIslands = endIslands.getSmallIslands();
			if(smallIslands != null) {
				for (long x = blockCoords.getX() + startX; x <= blockCoords.getX() + endX; ++x) {
					for (long y = blockCoords.getY() + startY; y <= blockCoords.getY() + endY; ++y) {
						for (SmallEndIsland island : smallIslands) {
							if (island.isOnIsland(x, y)) {
								int coordHeight = island.getHeight();
								if(coordHeight > highestBlock) {
									highestBlock = coordHeight;
									highestX = x;
									highestY = y;
								}
							}
						}
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
