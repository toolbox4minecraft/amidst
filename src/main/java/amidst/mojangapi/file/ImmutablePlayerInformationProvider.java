package amidst.mojangapi.file;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.world.player.PlayerInformation;

@Immutable
public class ImmutablePlayerInformationProvider implements PlayerInformationProvider {
	private final PlayerInformation playerInformation;

	public ImmutablePlayerInformationProvider(PlayerInformation playerInformation) {
		this.playerInformation = playerInformation;
	}

	@NotNull
	@Override
	public PlayerInformation getByPlayerUUID(String playerUUID) {
		return playerInformation;
	}

	@NotNull
	@Override
	public PlayerInformation getByPlayerName(String playerName) {
		return playerInformation;
	}
}
