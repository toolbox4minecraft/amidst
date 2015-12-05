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
		longRunningIOExecutor.invokeLater(new LongRunningIOOperation<Void>() {
			@Override
			public Void execute() {
				loadSkin(player);
				return null;
			}

			@Override
			public void finished(Void result) {
				finishedLoadingPlayerSkin();
			}
		});
	}

	private void finishedLoadingPlayerSkin() {
		application.finishedLoadingPlayerSkin();
	}

	private void loadSkin(Player player) {
		try {
			doLoadSkin(player);
		} catch (MalformedURLException e) {
			error(player, e);
		} catch (IOException e) {
			error(player, e);
		}
	}

	private void error(Player player, Exception e) {
		Log.w("Cannot load skin for player " + player.getPlayerName());
		e.printStackTrace();
	}

	private void doLoadSkin(Player player) throws MalformedURLException,
			IOException {
		player.setSkin(createImage(player));
	}

	private BufferedImage createImage(Player player)
			throws MalformedURLException, IOException {
		BufferedImage image = new BufferedImage(20, 20,
				BufferedImage.TYPE_INT_ARGB);
		drawSkinToImage(getSkin(player), image);
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

	private BufferedImage getSkin(Player player) throws MalformedURLException,
			IOException {
		return ImageIO.read(getSkinURL(player));
	}

	private URL getSkinURL(Player player) throws MalformedURLException {
		return new URL("http://s3.amazonaws.com/MinecraftSkins/"
				+ player.getPlayerName() + ".png");
	}
}
