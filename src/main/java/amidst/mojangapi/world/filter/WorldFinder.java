package amidst.mojangapi.world.filter;

import java.io.File;
import java.util.function.Consumer;

import amidst.gui.main.MainWindow;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.threading.WorkerExecutor;
import amidst.threading.worker.ExceptionalWorkerWithResult;

public class WorldFinder {
  MojangApi mojangApi;
  WorldBuilder worldBuilder;
  WorldFilter worldFilter;

  public WorldFinder(MojangApi unsafeMojangApi) throws 
      LocalMinecraftInterfaceCreationException {
    this.worldBuilder = new WorldBuilder(null, new SilentLogger());
    this.mojangApi = unsafeMojangApi.duplicateApiInterface(this.worldBuilder);
    this.worldFilter = new WorldFilter("", 1024);
  }

  public void findRandomWorld(WorldType worldType, WorkerExecutor workerExecutor, MainWindow mainWindow) {
    workerExecutor.run(new ExceptionalWorkerWithResult<World>() {
      @Override
	public World run() throws MinecraftInterfaceException {
        return WorldFinder.this.findRandomWorld(worldType);
      }
    }, new Consumer<World>() {
      @Override
	public void accept(World world) {
        mainWindow.setWorld(world);
      }
    }, new Consumer<Exception>() {
      @Override
      public void accept(Exception e) {
        e.printStackTrace();
        mainWindow.displayException(e);
      }
    });
  }

  private World findRandomWorld(WorldType worldType) throws 
    IllegalStateException, MinecraftInterfaceException {
    World world;
    do {
      WorldSeed worldSeed = WorldSeed.random();
      world = mojangApi.createWorldFromSeed(worldSeed, worldType);
    } while (!worldFilter.isValid(world));
    return world;
  }

  private static class SilentLogger extends SeedHistoryLogger {
    public SilentLogger() {
      super(new File("history.txt"), false);
    }

    @Override
    public synchronized void log(RecognisedVersion recognisedVersion,
      WorldSeed worldSeed) {
      //We don't want to log any of the seeds that don't meet the filter requirements
    }
  }
}
