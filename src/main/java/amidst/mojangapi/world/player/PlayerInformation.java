package amidst.mojangapi.world.player;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.world.icon.WorldIconImage;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

@Immutable
public class PlayerInformation {
	private static final WorldIconImage DEFAULT_HEAD = DefaultWorldIconTypes.PLAYER.getImage();
	private static final PlayerInformation THE_SINGLEPLAYER_PLAYER = new PlayerInformation(
			null,
			"The Singleplayer Player",
			DEFAULT_HEAD);

	@NotNull
	public static PlayerInformation theSingleplayerPlayer() {
		return THE_SINGLEPLAYER_PLAYER;
	}

	private final String uuid;
	private final String name;
	private final WorldIconImage head;

	public PlayerInformation(String uuid, String name, WorldIconImage head) {
		this.uuid = uuid;
		this.name = name;
		this.head = head;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public WorldIconImage getHead() {
		return head;
	}

	public String getNameOrElseUUID() {
		if (name != null) {
			return name;
		} else {
			return uuid;
		}
	}
}
