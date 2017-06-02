package amidst.mojangapi.file;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.file.service.SaveDirectoryService;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@Immutable
public class SaveGame {
	private final SaveDirectoryService saveDirectoryService = new SaveDirectoryService();
	private final SaveDirectory saveDirectory;
	private final LevelDatNbt levelDatNbt;

	public SaveGame(SaveDirectory saveDirectory, LevelDatNbt levelDatNbt) {
		this.saveDirectory = saveDirectory;
		this.levelDatNbt = levelDatNbt;
	}

	public long getSeed() {
		return levelDatNbt.getSeed();
	}

	public CoordinatesInWorld getWorldSpawn() {
		return levelDatNbt.getWorldSpawn();
	}

	public WorldType getWorldType() {
		return levelDatNbt.getWorldType();
	}

	public String getGeneratorOptions() {
		return levelDatNbt.getGeneratorOptions();
	}

	public boolean hasSingleplayerPlayer() {
		return levelDatNbt.hasPlayer();
	}

	public boolean hasMultiplayerPlayers() {
		return saveDirectory.hasMultiplayerPlayers();
	}

	public Optional<SaveGamePlayer> tryReadSingleplayerPlayer() {
		return saveDirectoryService
				.tryReadSingleplayerPlayerNbt(saveDirectory)
				.map(p -> new SaveGamePlayer(saveDirectory, p));
	}

	public List<SaveGamePlayer> tryReadMultiplayerPlayers() {
		return saveDirectoryService
				.tryReadMultiplayerPlayerNbts(saveDirectory)
				.stream()
				.map(p -> new SaveGamePlayer(saveDirectory, p))
				.collect(Collectors.toList());
	}

	public List<SaveGamePlayer> tryReadAllPlayers() {
		List<SaveGamePlayer> result = new LinkedList<>();
		result.addAll(tryReadSingleplayerPlayer().map(Collections::singletonList).orElseGet(Collections::emptyList));
		result.addAll(tryReadMultiplayerPlayers());
		return result;
	}
}
