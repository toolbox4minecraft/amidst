package amidst.mojangapi.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.service.PlayerInformationService;
import amidst.mojangapi.world.player.PlayerInformation;

/**
 * Even though this class is thread-safe, it is possible that the same player
 * information is loaded more than once. This will be the case, if the first
 * request did not finish before the second request arrives. However, this use
 * case is very unlikely to happen for amidst. If it happens, it should not be a
 * problem.
 */
@ThreadSafe
public class PlayerInformationCache implements PlayerInformationProvider {
	private final Map<String, PlayerInformation> byUUID = new ConcurrentHashMap<>();
	private final Map<String, PlayerInformation> byName = new ConcurrentHashMap<>();
	private final PlayerInformationService playerInformationService = new PlayerInformationService();

	@NotNull
	@Override
	public PlayerInformation getByPlayerUUID(String playerUUID) {
		String cleanUUID = getCleanUUID(playerUUID);
		PlayerInformation result = byUUID.get(cleanUUID);
		if (result != null) {
			return result;
		} else {
			AmidstLogger.info("requesting player information for uuid: " + cleanUUID);
			result = playerInformationService.fromUUID(cleanUUID);
			put(result);
			return result;
		}
	}

	@NotNull
	@Override
	public PlayerInformation getByPlayerName(String playerName) {
		PlayerInformation result = byName.get(playerName);
		if (result != null) {
			return result;
		} else {
			AmidstLogger.info("requesting player information for name: " + playerName);
			result = playerInformationService.fromName(playerName);
			put(result);
			return result;
		}
	}

	private void put(PlayerInformation result) {
		if (result.getUUID() != null) {
			byUUID.put(result.getUUID(), result);
		}
		if (result.getName() != null) {
			byName.put(result.getName(), result);
		}
	}

	/**
	 * The uuid in the filename contains dashes that are not allowed in the url.
	 */
	private String getCleanUUID(String uuid) {
		return uuid.replace("-", "");
	}
}
