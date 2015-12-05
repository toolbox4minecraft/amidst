package amidst.utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import amidst.Application;
import amidst.LongRunningIOExecutor;
import amidst.LongRunningIOOperation;
import amidst.logging.Log;
import amidst.minecraft.world.Player;

public class SkinLoader {
	private final Application application;
	private final LongRunningIOExecutor longRunningIOExecutor;

	public SkinLoader(Application application,
			LongRunningIOExecutor longRunningIOExecutor) {
		this.application = application;
		this.longRunningIOExecutor = longRunningIOExecutor;
	}

	public void loadSkins(List<Player> players) {
		for (Player player : players) {
			loadSkinLater(player);
		}
	}

	private void loadSkinLater(final Player player) {
		longRunningIOExecutor
				.invokeLater(new LongRunningIOOperation<BufferedImage>() {
					@Override
					public BufferedImage execute() {
						return loadSkin(player);
					}

					@Override
					public void finished(BufferedImage image) {
						finishedLoadingPlayerSkin(player, image);
					}
				});
	}

	private void finishedLoadingPlayerSkin(Player player, BufferedImage image) {
		if (image != null) {
			player.setSkin(image);
			application.finishedLoadingPlayerSkin();
		}
	}

	private BufferedImage loadSkin(Player player) {
		try {
			return createImage(player.getPlayerName());
		} catch (MalformedURLException e) {
			error(player.getPlayerName(), e);
		} catch (IOException e) {
			error(player.getPlayerName(), e);
		}
		return null;
	}

	private void error(String playerName, Exception e) {
		Log.w("Cannot load skin for player " + playerName);
		e.printStackTrace();
	}

	private BufferedImage createImage(String playerName)
			throws MalformedURLException, IOException {
		BufferedImage image = new BufferedImage(20, 20,
				BufferedImage.TYPE_INT_ARGB);
		drawSkinToImage(getSkin(playerName), image);
		return image;
	}

	private void drawSkinToImage(BufferedImage skin, BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 20, 20);
		g2d.drawImage(skin, 2, 2, 18, 18, 8, 8, 16, 16, null);
		g2d.dispose();
		skin.flush();
	}

	private BufferedImage getSkin(String playerName)
			throws MalformedURLException, IOException {
		return ImageIO.read(getSkinURL(playerName));
	}

	private URL getSkinURL(String playerName) throws MalformedURLException {
		return new URL("http://s3.amazonaws.com/MinecraftSkins/" + playerName
				+ ".png");
	}
}
