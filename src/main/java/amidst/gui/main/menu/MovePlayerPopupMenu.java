package amidst.gui.main.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import amidst.documentation.NotThreadSafe;
import amidst.gui.main.Actions;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.Player;

@NotThreadSafe
public class MovePlayerPopupMenu {
	private final Actions actions;
	private final MovablePlayerList movablePlayerList;
	private final Coordinates targetCoordinates;

	public MovePlayerPopupMenu(
			Actions actions,
			MovablePlayerList movablePlayerList,
			Coordinates targetCoordinates) {
		this.actions = actions;
		this.movablePlayerList = movablePlayerList;
		this.targetCoordinates = targetCoordinates;
	}

	public void show(Component component, int x, int y) {
		createPlayerMenu().show(component, x, y);
	}

	private JPopupMenu createPlayerMenu() {
		JPopupMenu result = new JPopupMenu();
		for (Player player : movablePlayerList) {
			result.add(createPlayerMenuItem(player, targetCoordinates));
		}
		return result;
	}

	private JMenuItem createPlayerMenuItem(final Player player, final Coordinates targetCoordinates) {
		JMenuItem result = new JMenuItem(player.getPlayerName());
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.movePlayer(player, targetCoordinates);
			}
		});
		return result;
	}
}
