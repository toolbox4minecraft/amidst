package amidst.mojangapi.mocking;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.world.player.PlayerInformation;
import amidst.mojangapi.world.player.PlayerInformationCache;

@Immutable
public class ImmutablePlayerInformationCache implements PlayerInformationCache {
	private final PlayerInformation playerInformation;

	public ImmutablePlayerInformationCache(PlayerInformation playerInformation) {
		this.playerInformation = playerInformation;
	}

	@NotNull
	@Override
	public PlayerInformation getByUUID(String uuid) {
		return playerInformation;
	}

	@NotNull
	@Override
	public PlayerInformation getByName(String name) {
		return playerInformation;
	}
}
