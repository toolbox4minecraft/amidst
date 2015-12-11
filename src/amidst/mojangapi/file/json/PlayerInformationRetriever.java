package amidst.mojangapi.file.json;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.file.json.player.PlayerJson;

@Immutable
public enum PlayerInformationRetriever {
	;

	private static final String SIMPLE_PLAYER_SKIN_URL = "http://s3.amazonaws.com/MinecraftSkins/";

	public static BufferedImage getPlayerHeadFromPlayerNameSimple(
			String playerName) {
		return readPlayerHead(getPlayerSkinUrlSimple(playerName));
	}

	public static BufferedImage getPlayerHeadFromPlayerName(String playerName) {
		return readPlayerHead(getPlayerSkinUrl(getPlayerUUID(playerName)));
	}

	public static BufferedImage getPlayerHeadFromUUID(String uuid) {
		return readPlayerHead(getPlayerSkinUrl(uuid));
	}

	public static String getPlayerName(String playerUUID) {
		try {
			PlayerJson playerJson = JsonReader.readPlayerFromUUID(playerUUID);
			if (playerJson != null) {
				return playerJson.getName();
			} else {
				return null;
			}
		} catch (IOException e) {
			Log.w("unable to retrieve player name: " + playerUUID);
			e.printStackTrace();
			return null;
		}
	}

	public static String getPlayerUUID(String playerName) {
		try {
			PlayerJson playerJson = JsonReader
					.readUUIDFromPlayerName(playerName);
			if (playerJson != null) {
				return playerJson.getId();
			} else {
				return null;
			}
		} catch (IOException e) {
			Log.w("unable to retrieve player uuid: " + playerName);
			e.printStackTrace();
			return null;
		}
	}

	private static URL getPlayerSkinUrlSimple(String playerName) {
		try {
			return new URL(SIMPLE_PLAYER_SKIN_URL + playerName + ".png");
		} catch (MalformedURLException e) {
			Log.w("unable to retrieve player skin url simple: " + playerName);
			e.printStackTrace();
			return null;
		}
	}

	private static URL getPlayerSkinUrl(String uuid) {
		if (uuid == null) {
			return null;
		}
		try {
			return new URL(JsonReader.readPlayerFromUUID(uuid)
					.readTexturesProperty().getTextures().getSKIN().getUrl());
		} catch (Exception e) {
			Log.w("unable to retrieve player skin url: " + uuid);
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage readPlayerHead(URL url) {
		if (url == null) {
			return null;
		}
		try {
			return extractHeadFromSkin(ImageIO.read(url));
		} catch (IOException e) {
			Log.w("unable to retrieve player skin: " + url);
			e.printStackTrace();
			return null;
		}
	}

	private static BufferedImage extractHeadFromSkin(BufferedImage skin) {
		BufferedImage head = new BufferedImage(20, 20,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = head.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 20, 20);
		g2d.drawImage(skin, 2, 2, 18, 18, 8, 8, 16, 16, null);
		g2d.dispose();
		skin.flush();
		return head;
	}
}
