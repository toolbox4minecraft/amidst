package amidst.mojangapi.world.player;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;

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
