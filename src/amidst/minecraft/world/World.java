package amidst.minecraft.world;

import java.util.List;

import amidst.map.Fragment;
import amidst.minecraft.world.finder.CachedWorldObjectProducer;
import amidst.minecraft.world.finder.NetherFortressProducer;
import amidst.minecraft.world.finder.OceanMonumentProducer;
import amidst.minecraft.world.finder.PlayerProducer;
import amidst.minecraft.world.finder.SpawnProducer;
import amidst.minecraft.world.finder.StrongholdProducer;
import amidst.minecraft.world.finder.TempleProducer;
import amidst.minecraft.world.finder.VillageProducer;
import amidst.minecraft.world.finder.WorldObject;
import amidst.minecraft.world.finder.WorldObjectProducer;

public abstract class World {
	private final WorldObjectProducer oceanMonumentProducer = new OceanMonumentProducer(
			this);
	private final WorldObjectProducer templeProducer = new TempleProducer(this);
	private final WorldObjectProducer villageProducer = new VillageProducer(
			this);
	private final WorldObjectProducer netherFortressProducer = new NetherFortressProducer(
			this);
	private final CachedWorldObjectProducer playerProducer = new PlayerProducer(
			this);
	private final CachedWorldObjectProducer spawnProducer = new SpawnProducer(
			this);
	private final CachedWorldObjectProducer strongholdProducer = new StrongholdProducer(
			this);
	private final BiomeDataProvider biomeDataProvider = new BiomeDataProvider();

	@Deprecated
	public boolean hasPlayers() {
		return isFileWorld();
	}

	@Deprecated
	public boolean isFileWorld() {
		return this instanceof FileWorld;
	}

	@Deprecated
	public FileWorld getAsFileWorld() {
		return (FileWorld) this;
	}

	public abstract long getSeed();

	public abstract String getSeedText();

	public abstract WorldType getWorldType();

	public WorldObjectProducer getOceanMonumentProducer() {
		return oceanMonumentProducer;
	}

	public WorldObjectProducer getTempleProducer() {
		return templeProducer;
	}

	public WorldObjectProducer getVillageProducer() {
		return villageProducer;
	}

	public WorldObjectProducer getNetherFortressProducer() {
		return netherFortressProducer;
	}

	public CachedWorldObjectProducer getPlayerProducer() {
		return playerProducer;
	}

	public CachedWorldObjectProducer getSpawnProducer() {
		return spawnProducer;
	}

	public CachedWorldObjectProducer getStrongholdProducer() {
		return strongholdProducer;
	}

	public List<WorldObject> getPlayers() {
		return playerProducer.getWorldObjects();
	}

	public WorldObject getSpawn() {
		return spawnProducer.getFirstWorldObject();
	}

	public List<WorldObject> getStrongholds() {
		return strongholdProducer.getWorldObjects();
	}

	public void reloadPlayers() {
		playerProducer.resetCache();
	}

	public void populateBiomeDataArray(Fragment fragment) {
		biomeDataProvider.populateArray(fragment.getCorner(),
				fragment.getBiomeData());
	}

	/**
	 * Use this only to quickly get the biome data of a single point, not to
	 * render the map.
	 */
	@Deprecated
	public short getBiomeDataAt(CoordinatesInWorld coordinates) {
		CoordinatesInWorld corner = coordinates.toFragmentCorner();
		short[][] biomeData = biomeDataProvider.getBiomeDataForFragment(corner);
		int x = (int) coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
		int y = (int) coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
		return biomeData[x][y];
	}
}
