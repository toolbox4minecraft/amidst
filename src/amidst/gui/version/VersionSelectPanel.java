package amidst.gui.version;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import amidst.Options;

public class VersionSelectPanel {
	private class Listeners implements MouseListener, KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (isLoading) {
				return;
			}
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_DOWN) {
				select(selectedIndex + 1);
			} else if (key == KeyEvent.VK_UP) {
				select(selectedIndex - 1);
			} else if (key == KeyEvent.VK_ENTER) {
				loadSelectedProfile();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (isLoading) {
				return;
			}
			Point mouse = e.getPoint();
			select(getSelectedIndexFromYCoordinate(mouse));
			if (isLoadButtonClicked(mouse)) {
				loadSelectedProfile();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		private int getSelectedIndexFromYCoordinate(Point mouse) {
			return mouse.y / 40;
		}

		private boolean isLoadButtonClicked(Point mouse) {
			return mouse.x > component.getWidth() - 40;
		}
	}

	@SuppressWarnings("serial")
	private class Component extends JPanel {
		private static final int INVALID_EMPTY_MESSAGE_WIDTH = -1;

		private int emptyMessageWidth = INVALID_EMPTY_MESSAGE_WIDTH;
		private String oldEmptyMessage;

		public Component() {
			this.oldEmptyMessage = emptyMessage;
		}

		@Override
		public void paintChildren(Graphics g) {
			super.paintChildren(g);
			Graphics2D g2d = (Graphics2D) g;
			drawSeparatorLines(g2d);
		}

		private void drawSeparatorLines(Graphics2D g2d) {
			g2d.setColor(Color.gray);
			for (int i = 1; i <= versionComponents.size(); i++) {
				g2d.drawLine(0, i * 40, getWidth(), i * 40);
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			drawBackground(g2d);
			if (versionComponents.isEmpty()) {
				drawEmptyMessage(g2d);
			}
		}

		private void drawBackground(Graphics2D g2d) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

		private void drawEmptyMessage(Graphics2D g2d) {
			g2d.setColor(Color.gray);
			g2d.setFont(EMPTY_MESSAGE_FONT);
			updateEmptyMessageWidth(g2d);
			int x = (getWidth() >> 1) - (emptyMessageWidth >> 1);
			g2d.drawString(emptyMessage, x, 30);
		}

		private void updateEmptyMessageWidth(Graphics2D g2d) {
			if (!oldEmptyMessage.equals(emptyMessage)
					|| emptyMessageWidth == INVALID_EMPTY_MESSAGE_WIDTH) {
				emptyMessageWidth = g2d.getFontMetrics().stringWidth(
						emptyMessage);
			}
		}
	}

	private static final Font EMPTY_MESSAGE_FONT = new Font("arial", Font.BOLD,
			30);
	private static final int INVALID_INDEX = -1;

	private Listeners listeners = new Listeners();
	private Component component = new Component();
	private ArrayList<VersionComponent> versionComponents = new ArrayList<VersionComponent>();

	private VersionComponent selected = null;
	private int selectedIndex = INVALID_INDEX;
	private String emptyMessage;
	private boolean isLoading = false;

	public VersionSelectPanel() {
		this("Empty");
	}

	public VersionSelectPanel(String emptyMessage) {
		this.emptyMessage = emptyMessage;
		initComponent();
	}

	private void initComponent() {
		component.setLayout(new MigLayout("ins 0", "", "[]0[]"));
		component.addMouseListener(listeners);
	}

	public void addVersion(VersionComponent version) {
		component.add(version.getComponent(), "growx, pushx, wrap");
		versionComponents.add(version);
	}

	public void select(String fullVersionName) {
		select(getIndex(fullVersionName));
	}

	private int getIndex(String fullVersionName) {
		for (int i = 0; i < versionComponents.size(); i++) {
			if (versionComponents.get(i).getFullVersionName()
					.equals(fullVersionName)) {
				return i;
			}
		}
		return INVALID_INDEX;
	}

	private void select(int index) {
		deselectSelected();
		doSelect(getBoundedIndex(index));
	}

	private void deselectSelected() {
		if (selected != null) {
			selected.setSelected(false);
			selected = null;
			selectedIndex = INVALID_INDEX;
		}
	}

	private int getBoundedIndex(int index) {
		if (versionComponents.isEmpty()) {
			return INVALID_INDEX;
		} else if (index < 0) {
			return 0;
		} else if (index >= versionComponents.size()) {
			return versionComponents.size() - 1;
		} else {
			return index;
		}
	}

	private void doSelect(int index) {
		if (index != INVALID_INDEX) {
			selected = versionComponents.get(index);
			selected.setSelected(true);
			selectedIndex = index;
		}
	}

	private void loadSelectedProfile() {
		if (selected == null || !selected.isReadyToLoad()) {
			return;
		}
		isLoading = true;
		selected.load();
		Options.instance.lastProfile.set(selected.getFullVersionName());
	}

	public void setEmptyMessage(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}

	@Deprecated
	public KeyListener getKeyListener() {
		return listeners;
	}

	public JPanel getComponent() {
		return component;
	}
}
