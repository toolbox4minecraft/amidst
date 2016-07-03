package amidst.mojangapi.file.json;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.player.PlayerJson;

@Immutable
public enum PlayerInformationRetriever {
	;

	private static final String SIMPLE_PLAYER_SKIN_URL = "http://s3.amazonaws.com/MinecraftSkins/";

	public static PlayerJson tryGetPlayerJsonByName(String name) {
		try {
			return getPlayerJsonByName(name);
		} catch (IOException | MojangApiParsingException | NullPointerException e) {
			Log.w("unable to load player information by name: " + name);
			return null;
		}
	}

	public static PlayerJson tryGetPlayerJsonByUUID(String uuid) {
		try {
			return getPlayerJsonByUUID(uuid);
		} catch (IOException | MojangApiParsingException | NullPointerException e) {
			Log.w("unable to load player information by uuid: " + uuid);
			return null;
		}
	}

	public static BufferedImage tryGetPlayerHeadByName(String name) {
		try {
			return getPlayerHeadByName(name);
		} catch (IOException | NullPointerException e) {
			Log.w("unable to load player head by name: " + name);
			return null;
		}
	}

	public static BufferedImage tryGetPlayerHeadBySkinUrl(String skinUrl) {
		try {
			return getPlayerHeadBySkinUrl(skinUrl);
		} catch (IOException | NullPointerException e) {
			Log.w("unable to load player head by skin url: " + skinUrl);
			return null;
		}
	}

	private static PlayerJson getPlayerJsonByName(String name) throws IOException, MojangApiParsingException {
		return JsonReader.readPlayerFromUUID(JsonReader.readSimplePlayerFromPlayerName(name).getId());
	}

	private static PlayerJson getPlayerJsonByUUID(String uuid) throws IOException, MojangApiParsingException {
		return JsonReader.readPlayerFromUUID(uuid);
	}

	private static BufferedImage getPlayerHeadByName(String name) throws IOException {
		return extractPlayerHead(new URL(SIMPLE_PLAYER_SKIN_URL + name + ".png"));
	}

	private static BufferedImage getPlayerHeadBySkinUrl(String skinUrl) throws IOException {
		return extractPlayerHead(new URL(skinUrl));
	}

	private static BufferedImage extractPlayerHead(URL url) throws IOException {
		return extractPlayerHead(ImageIO.read(url));
	}

	private static BufferedImage extractPlayerHead(BufferedImage skin) {
		BufferedImage head = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = head.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 20, 20);
		g2d.drawImage(skin, 2, 2, 18, 18, 8, 8, 16, 16, null);
		g2d.dispose();
		skin.flush();
		return head;
	}
}
