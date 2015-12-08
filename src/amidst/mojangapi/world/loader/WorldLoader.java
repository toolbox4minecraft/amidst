package amidst.mojangapi.world.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.logging.Log;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.world.MovablePlayerList;
import amidst.mojangapi.world.World;

public class WorldLoader {
	private final SaveDirectory saveDirectory;

	public WorldLoader(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	public World create(MinecraftInterface minecraftInterface)
			throws FileNotFoundException, IOException {
		boolean isMultiPlayer = saveDirectory.isMultiPlayer();
		LevelDat levelDat = saveDirectory.createLevelDat();
		if (isMultiPlayer) {
			Log.i("Multiplayer world detected.");
			return World.file(
					minecraftInterface,
					levelDat.getSeed(),
					levelDat.getWorldType(),
					levelDat.getGeneratorOptions(),
					isMultiPlayer,
					createMovablePlayerList(minecraftInterface,
							new MultiPlayerPlayerLoader(saveDirectory),
							new MultiPlayerPlayerMover(saveDirectory)));
		} else {
			Log.i("Singleplayer world detected.");
			return World.file(
					minecraftInterface,
					levelDat.getSeed(),
					levelDat.getWorldType(),
					levelDat.getGeneratorOptions(),
					isMultiPlayer,
					createMovablePlayerList(minecraftInterface,
							new SinglePlayerPlayerLoader(saveDirectory),
							new SinglePlayerPlayerMover(saveDirectory)));
		}
	}

	private MovablePlayerList createMovablePlayerList(
			MinecraftInterface minecraftInterface, PlayerLoader playerLoader,
			PlayerMover playerMover) {
		if (minecraftInterface.getRecognisedVersion().isSaveEnabled()) {
			return new MovablePlayerList(playerLoader, playerMover);
		} else {
			return new MovablePlayerList(playerLoader);
		}
	}
}
