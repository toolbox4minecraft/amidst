package amidst.mojangapi.file.nbt.playerloader;

import java.util.Collections;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.logging.Log;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.world.Player;
import amidst.mojangapi.world.PlayerCoordinates;

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

	protected Player createNamedPlayer(String playerName, CompoundTag tag) {
		ListTag posTag = (ListTag) getTagPos(tag);
		List<Tag> posList = posTag.getValue();
		return Player.named(playerName, new PlayerCoordinates(
				(long) (double) (Double) posList.get(0).getValue(),
				(long) (double) (Double) posList.get(1).getValue(),
				(long) (double) (Double) posList.get(2).getValue()));
	}

	protected Player createNamelessPlayer(CompoundTag tag) {
		ListTag posTag = (ListTag) getTagPos(tag);
		List<Tag> posList = posTag.getValue();
		return Player.nameless(new PlayerCoordinates(
				(long) (double) (Double) posList.get(0).getValue(),
				(long) (double) (Double) posList.get(1).getValue(),
				(long) (double) (Double) posList.get(2).getValue()));
	}

	private Tag getTagPos(CompoundTag tag) {
		return tag.getValue().get(NBTTagKeys.TAG_KEY_POS);
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
