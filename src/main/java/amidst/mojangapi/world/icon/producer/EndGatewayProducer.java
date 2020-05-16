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
	private static final int REQUIRED_BIOME_INFLUENCE = 40;
	
	/**
	 * Used as a cache only for the spawn gateways.
	 */
	private static final EndSpawnGatewayProducer spawnProducer = new EndSpawnGatewayProducer();
	
	private final long seed;

	public EndGatewayProducer(long seed) {
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

	// TODO: use longs?
	private void generateAt(
			CoordinatesInWorld corner,
			Consumer<WorldIcon> consumer,
			List<EndIsland> endIslands,
			int xRelativeToFragment,
			int yRelativeToFragment) {
		int x = xRelativeToFragment + (int) corner.getXAs(RESOLUTION);
		int y = yRelativeToFragment + (int) corner.getYAs(RESOLUTION);
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
	public CoordinatesInWorld tryGetValidLocationFromChunk(int chunkX, int chunkY, List<EndIsland> endIslands, CoordinatesInWorld corner) {
		if((chunkX * chunkX + chunkY * chunkY) > 4096) {
			ChunkRand rand = new ChunkRand();
			int blockX = chunkX << 4;
			int blockY = chunkY << 4;
			
			rand.setDecoratorSeed(seed, blockX, blockY, 0, 3, MCVersion.v1_13);
			
			if(rand.nextInt(END_GATEWAY_CHANCE) == 0) {
				for(EndIsland island : endIslands) {
					float biomeInfluence = island.influenceAtChunk(chunkX, chunkY);
					if(biomeInfluence >= REQUIRED_BIOME_INFLUENCE) {
						int gatewayX = rand.nextInt(16) + blockX;
						int gatewayY = rand.nextInt(16) + blockY;
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
		private static int NUMBER_OF_SPAWN_GATEWAYS = 20;
		
		@Override
		protected List<WorldIcon> doCreateCache() {
			List<WorldIcon> iconList = new ArrayList<WorldIcon>();
			for(int i = 0; i < NUMBER_OF_SPAWN_GATEWAYS; i++) {
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
			}
			return iconList;
		}
		
		private static int floor(double value) {
			int i = (int) value;
			return value < (double) i ? i - 1 : i;
		}
	}
	
}
