package amidst.gui.menu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import MoF.MapViewer;
import amidst.map.Fragment;
import amidst.map.Map;
import amidst.map.MapObjectPlayer;
import amidst.map.layers.PlayerLayer;

public class PlayerMenuItem extends JMenuItem implements ActionListener {
	private PlayerLayer playerLayer;
	private MapObjectPlayer player;
	private MapViewer mapViewer;
	
	public PlayerMenuItem(MapViewer mapViewer, MapObjectPlayer player, PlayerLayer playerLayer) {
		super(player.getName());
		this.playerLayer = playerLayer;
		this.player = player;
		this.mapViewer = mapViewer;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Map map = playerLayer.getMap();
		
		if (player.parentFragment != null) {
			player.parentFragment.removeObject(player);
		}
		Point location = map.screenToLocal(mapViewer.lastRightClick);
		player.setPosition(location.x, location.y);
		Fragment fragment = map.getFragmentAt(location);
		fragment.addObject(player);
		player.parentFragment = fragment;
	}
	
}
