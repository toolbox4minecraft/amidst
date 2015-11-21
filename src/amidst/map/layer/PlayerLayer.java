package amidst.map.layer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.object.MapObject;
import amidst.map.object.MapObjectPlayer;
import amidst.minecraft.world.FileWorld.Player;
import amidst.minecraft.world.World;

public class PlayerLayer extends IconLayer {
	private static class Tuple {
		private final MapObject mapObject;
		private Fragment fragment;

		public Tuple(MapObject mapObject, Fragment fragment) {
			this.mapObject = mapObject;
			this.fragment = fragment;
		}
	}

	private Map<Player, Tuple> players = new HashMap<Player, Tuple>();

	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		for (Tuple tuple : players.values()) {
			if (fragment.isInBounds(tuple.mapObject)) {
				tuple.fragment = fragment;
				fragment.addObject(tuple.mapObject);
			}
		}
	}

	@Deprecated
	public void setWorld(World world) {
		if (world.isFileWorld()) {
			initPlayersMap(world.getAsFileWorld().getMovablePlayers());
		} else {
			players.clear();
		}
	}

	private void initPlayersMap(List<Player> playerList) {
		for (Player player : playerList) {
			players.put(player, createTuple(player));
		}
	}

	private Tuple createTuple(Player player) {
		return new Tuple(createMapObject(player), null);
	}

	private MapObjectPlayer createMapObject(Player player) {
		return new MapObjectPlayer(Options.instance.showPlayers, player);
	}

	@Deprecated
	public void updatePlayerPosition(Player player, Fragment newFragment) {
		removeOldMapObject(player);
		addNewMapObject(player, newFragment);
	}

	private void removeOldMapObject(Player player) {
		if (players.containsKey(player)) {
			Tuple tuple = players.get(player);
			if (tuple.fragment != null) {
				tuple.fragment.removeObject(tuple.mapObject);
			}
		}
	}

	private void addNewMapObject(Player player, Fragment newFragment) {
		Tuple tuple = new Tuple(createMapObject(player), newFragment);
		newFragment.addObject(tuple.mapObject);
		players.put(player, tuple);
	}
}
