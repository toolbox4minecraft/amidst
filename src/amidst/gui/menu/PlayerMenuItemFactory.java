package amidst.gui.menu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.map.layer.PlayerLayer;
import amidst.map.object.MapObjectPlayer;

public class PlayerMenuItemFactory {
	private MapViewer mapViewer;
	private PlayerLayer playerLayer;

	public PlayerMenuItemFactory(MapViewer mapViewer, PlayerLayer playerLayer) {
		this.mapViewer = mapViewer;
		this.playerLayer = playerLayer;
	}

	public JMenuItem create(final MapObjectPlayer player) {
		JMenuItem result = new JMenuItem(player.getName());
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerSelected(player);
			}
		});
		return result;
	}

	private void playerSelected(MapObjectPlayer player) {
		Map map = playerLayer.getMap();
		if (player.parentFragment != null) {
			player.parentFragment.removeObject(player);
		}
		Point lastRightClick = mapViewer.lastRightClick;
		if (lastRightClick != null) {
			Point location = map.screenToLocal(lastRightClick);
			player.setPosition(location.x, location.y);
			Fragment fragment = map.getFragmentAt(location);
			fragment.addObject(player);
			player.parentFragment = fragment;
		}
	}
}
