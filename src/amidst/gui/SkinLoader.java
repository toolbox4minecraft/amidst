package amidst.gui;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import amidst.mojangapi.file.json.PlayerInformationRetriever;
import amidst.mojangapi.world.MovablePlayerList;
import amidst.mojangapi.world.Player;
import amidst.threading.Worker;
import amidst.threading.WorkerExecutor;

public class SkinLoader {
	private final Map<String, BufferedImage> cache = new ConcurrentHashMap<String, BufferedImage>();
	private final WorkerExecutor workerExecutor;

	public SkinLoader(WorkerExecutor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	public void loadSkins(MovablePlayerList movablePlayerList,
			Runnable onFinished) {
		for (Player player : movablePlayerList) {
			if (player.isSkinLoadable()) {
				loadSkinLater(player, onFinished);
			}
		}
	}

	private void loadSkinLater(final Player player, final Runnable onFinished) {
		workerExecutor.invokeLater(new Worker<BufferedImage>() {
			@Override
			public BufferedImage execute() {
				return loadSkin(player);
			}

			@Override
			public void finished(BufferedImage image) {
				finishedLoading(player, image, onFinished);
			}
		});
	}

	private void finishedLoading(Player player, BufferedImage image,
			Runnable onFinished) {
		if (image != null) {
			player.setSkin(image);
			onFinished.run();
		}
	}

	private BufferedImage loadSkin(Player player) {
		String playerName = player.getPlayerName();
		if (cache.containsKey(playerName)) {
			return cache.get(playerName);
		}
		BufferedImage skin;
		skin = PlayerInformationRetriever
				.getPlayerHeadFromPlayerNameSimple(playerName);
		if (skin != null) {
			cache.put(playerName, skin);
			return skin;
		}
		skin = PlayerInformationRetriever
				.getPlayerHeadFromPlayerName(playerName);
		if (skin != null) {
			cache.put(playerName, skin);
			return skin;
		}
		return null;
	}
}
