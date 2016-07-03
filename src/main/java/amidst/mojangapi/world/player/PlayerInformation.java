package amidst.mojangapi.world.player;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.PlayerInformationRetriever;
import amidst.mojangapi.file.json.player.PlayerJson;
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
	public static PlayerInformation fromUUID(String uuid) {
		PlayerJson player = PlayerInformationRetriever.tryGetPlayerJsonByUUID(uuid);
		WorldIconImage head;
		if (player != null) {
			head = tryGetPlayerHeadBySkinUrl(player);
			if (head != null) {
				return new PlayerInformation(player.getId(), player.getName(), head);
			} else {
				return new PlayerInformation(player.getId(), player.getName(), DEFAULT_HEAD);
			}
		} else {
			return new PlayerInformation(uuid, null, DEFAULT_HEAD);
		}
	}

	@NotNull
	public static PlayerInformation fromName(String name) {
		PlayerJson player = PlayerInformationRetriever.tryGetPlayerJsonByName(name);
		WorldIconImage head;
		if (player != null) {
			head = tryGetPlayerHeadBySkinUrl(player);
			if (head != null) {
				return new PlayerInformation(player.getId(), player.getName(), head);
			} else {
				head = tryGetPlayerHeadByName(name);
				if (head != null) {
					return new PlayerInformation(player.getId(), player.getName(), head);
				} else {
					return new PlayerInformation(player.getId(), player.getName(), DEFAULT_HEAD);
				}
			}
		} else {
			head = tryGetPlayerHeadByName(name);
			if (head != null) {
				return new PlayerInformation(null, name, head);
			} else {
				return new PlayerInformation(null, name, DEFAULT_HEAD);
			}
		}
	}

	private static WorldIconImage tryGetPlayerHeadBySkinUrl(PlayerJson player) {
		try {
			return WorldIconImage.from(PlayerInformationRetriever.tryGetPlayerHeadBySkinUrl(player.getSkinUrl()));
		} catch (MojangApiParsingException e) {
			return null;
		}
	}

	private static WorldIconImage tryGetPlayerHeadByName(String name) {
		return WorldIconImage.from(PlayerInformationRetriever.tryGetPlayerHeadByName(name));
	}

	@NotNull
	public static PlayerInformation theSingleplayerPlayer() {
		return THE_SINGLEPLAYER_PLAYER;
	}

	private final String uuid;
	private final String name;
	private final WorldIconImage head;

	private PlayerInformation(String uuid, String name, WorldIconImage head) {
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

	public String getNameOrUUID() {
		if (name != null) {
			return name;
		} else {
			return uuid;
		}
	}
}
