package amidst.mojangapi.world.player;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;

@ThreadSafe
public interface PlayerInformationCache {
	@NotNull
	PlayerInformation getByUUID(String uuid);

	@NotNull
	PlayerInformation getByName(String name);
}
