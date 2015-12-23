package amidst.mojangapi.world.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.logging.Log;

/**
 * Even though this class is thread-safe, it is possible that the same player
 * information is loaded more than once. This will be the case, if the first
 * request did not finish before the second request arrives. However, this use
 * case is very unlikely to happen for amidst. If it happens, it should not be a
 * problem.
 */
@ThreadSafe
public class PlayerInformationCache {
	private final Map<String, PlayerInformation> byUUID = new ConcurrentHashMap<String, PlayerInformation>();
	private final Map<String, PlayerInformation> byName = new ConcurrentHashMap<String, PlayerInformation>();

	@NotNull
	public PlayerInformation getByUUID(String uuid) {
		uuid = getCleanUUID(uuid);
		PlayerInformation result = byUUID.get(uuid);
		if (result != null) {
			return result;
		} else {
			Log.i("requesting player information for uuid: " + uuid);
			result = PlayerInformation.fromUUID(uuid);
			put(result);
			return result;
		}
	}

	@NotNull
	public PlayerInformation getByName(String name) {
		PlayerInformation result = byName.get(name);
		if (result != null) {
			return result;
		} else {
			Log.i("requesting player information for name: " + name);
			result = PlayerInformation.fromName(name);
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
