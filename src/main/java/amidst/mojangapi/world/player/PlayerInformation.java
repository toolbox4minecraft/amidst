package amidst.mojangapi.world.player;

import java.awt.image.BufferedImage;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.PlayerInformationRetriever;
import amidst.mojangapi.file.json.player.PlayerJson;
import amidst.mojangapi.world.icon.DefaultWorldIconTypes;

@Immutable
public class PlayerInformation {
	private static final BufferedImage DEFAULT_HEAD = DefaultWorldIconTypes.PLAYER
			.getImage();
	private static final PlayerInformation THE_SINGLEPLAYER_PLAYER = new PlayerInformation(
			null, "The Singleplayer Player", DEFAULT_HEAD);

	public static PlayerInformation fromUUID(String uuid) {
		PlayerJson player = PlayerInformationRetriever
				.tryGetPlayerJsonByUUID(uuid);
		BufferedImage head;
		if (player != null) {
			head = PlayerInformationRetriever.tryGetPlayerHeadBySkinUrl(player
					.getSkinUrl());
			if (head != null) {
				return new PlayerInformation(player.getId(), player.getName(),
						head);
			} else {
				return new PlayerInformation(player.getId(), player.getName(),
						DEFAULT_HEAD);
			}
		} else {
			return new PlayerInformation(uuid, null, DEFAULT_HEAD);
		}
	}

	public static PlayerInformation fromName(String name) {
		PlayerJson player = PlayerInformationRetriever
				.tryGetPlayerJsonByName(name);
		BufferedImage head;
		if (player != null) {
			head = PlayerInformationRetriever.tryGetPlayerHeadBySkinUrl(player
					.getSkinUrl());
			if (head != null) {
				return new PlayerInformation(player.getId(), player.getName(),
						head);
			} else {
				head = PlayerInformationRetriever.tryGetPlayerHeadByName(name);
				if (head != null) {
					return new PlayerInformation(player.getId(),
							player.getName(), head);
				} else {
					return new PlayerInformation(player.getId(),
							player.getName(), DEFAULT_HEAD);
				}
			}
		} else {
			head = PlayerInformationRetriever.tryGetPlayerHeadByName(name);
			if (head != null) {
				return new PlayerInformation(null, name, head);
			} else {
				return new PlayerInformation(null, name, DEFAULT_HEAD);
			}
		}
	}

	public static PlayerInformation theSingleplayerPlayer() {
		return THE_SINGLEPLAYER_PLAYER;
	}

	private final String uuid;
	private final String name;
	private final BufferedImage head;

	private PlayerInformation(String uuid, String name, BufferedImage head) {
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

	public BufferedImage getHead() {
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
