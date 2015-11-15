package amidst.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import amidst.logging.Log;

public class SkinLoader {
	private BlockingQueue<MapObjectPlayer> playerQueue = new LinkedBlockingQueue<MapObjectPlayer>();
	private boolean active = false;
	private boolean running = false;

	public void loadSkin(MapObjectPlayer player) {
		try {
			playerQueue.put(player);
		} catch (InterruptedException e) {
			Log.w("Cannot enqueue player");
		}
	}

	public void start() {
		if (!running) {
			active = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					doRun();
				}
			}).start();
		}
	}

	// TODO: call me!
	public void stop() {
		active = false;
	}

	private void doRun() {
		running = true;
		while (active) {
			MapObjectPlayer player = getNextPlayer();
			if (active && player != null) {
				try {
					doLoadSkin(player);
				} catch (MalformedURLException e) {
					error(player, e);
				} catch (IOException e) {
					error(player, e);
				}
			}
		}
		running = false;
	}

	private void error(MapObjectPlayer player, Exception e) {
		Log.w("Cannot load skin for player " + player.getName());
		e.printStackTrace();
	}

	private MapObjectPlayer getNextPlayer() {
		try {
			return playerQueue.poll(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}

	private void doLoadSkin(MapObjectPlayer player)
			throws MalformedURLException, IOException {
		player.setMarker(createMarker(player));
	}

	private BufferedImage createMarker(MapObjectPlayer player)
			throws MalformedURLException, IOException {
		BufferedImage marker = new BufferedImage(20, 20,
				BufferedImage.TYPE_INT_ARGB);
		drawSkinToMarker(marker, getSkin(player));
		return marker;
	}

	private void drawSkinToMarker(BufferedImage pimg, BufferedImage skin) {
		Graphics2D g2d = pimg.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 20, 20);
		g2d.drawImage(skin, 2, 2, 18, 18, 8, 8, 16, 16, null);
		g2d.dispose();
		skin.flush();
	}

	private BufferedImage getSkin(MapObjectPlayer player)
			throws MalformedURLException, IOException {
		return ImageIO.read(getSkinURL(player));
	}

	private URL getSkinURL(MapObjectPlayer player) throws MalformedURLException {
		return new URL("http://s3.amazonaws.com/MinecraftSkins/"
				+ player.getName() + ".png");
	}
}
