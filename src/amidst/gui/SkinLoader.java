package amidst.gui;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import amidst.mojangapi.file.json.PlayerInformationRetriever;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.Player;
import amidst.threading.Worker;
import amidst.threading.WorkerExecutor;

public class SkinLoader {
	private final Map<String, BufferedImage> cache = new ConcurrentHashMap<String, BufferedImage>();
	private final WorkerExecutor workerExecutor;

	public SkinLoader(WorkerExecutor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	public void loadSkins(MovablePlayerList movablePlayerList,
			Runnable onSkinFinishedLoading) {
		for (Player player : movablePlayerList) {
			if (player.isSkinLoadable()) {
				loadSkinLater(player, onSkinFinishedLoading);
			}
		}
	}

	private void loadSkinLater(final Player player,
			final Runnable onSkinFinishedLoading) {
		workerExecutor.invokeLater(new Worker<BufferedImage>() {
			@Override
			public BufferedImage execute() {
				return loadSkin(player);
			}

			@Override
			public void finished(BufferedImage image) {
				finishedLoading(player, image, onSkinFinishedLoading);
			}
		});
	}

	private void finishedLoading(Player player, BufferedImage image,
			Runnable onSkinFinishedLoading) {
		if (image != null) {
			player.setSkin(image);
			onSkinFinishedLoading.run();
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
