package amidst.mojangapi.file.nbt.playerloader;

import java.util.Collections;
import java.util.List;

import amidst.logging.Log;
import amidst.mojangapi.world.Player;

public abstract class PlayerLoader {
	private static final PlayerLoader DUMMY = new PlayerLoader() {
		@Override
		protected List<Player> doLoad() throws Exception {
			return Collections.emptyList();
		}
	};

	public static PlayerLoader dummy() {
		return DUMMY;
	}

	// TODO: gui feedback
	public List<Player> load() {
		try {
			return doLoad();
		} catch (Exception e) {
			Log.w("unable to load players");
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	protected abstract List<Player> doLoad() throws Exception;
}
