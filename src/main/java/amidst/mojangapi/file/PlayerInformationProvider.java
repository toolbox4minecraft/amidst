package amidst.mojangapi.file;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.player.PlayerInformation;

@ThreadSafe
public interface PlayerInformationProvider {
	@NotNull
	PlayerInformation getByPlayerUUID(String playerUUID);

	@NotNull
	PlayerInformation getByPlayerName(String playerName);
}
