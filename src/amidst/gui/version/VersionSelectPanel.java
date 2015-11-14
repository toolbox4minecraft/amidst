package amidst.gui.version;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
				if (selectedIndex < versionComponents.size() - 1) {
					select(selectedIndex + 1);
				} else {
					select(versionComponents.size() - 1);
				}
			} else if (key == KeyEvent.VK_UP) {
				if (selectedIndex > 0) {
					select(selectedIndex - 1);
				} else if (selectedIndex == -1) {
					select(0);
				}
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
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			if (emptyMessageMetric == null) {
				emptyMessageMetric = g.getFontMetrics(emptyMessageFont);
				emptyMessageWidth = emptyMessageMetric
						.stringWidth(emptyMessage);
			}
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());

			if (versionComponents.size() == 0) {
				g.setColor(Color.gray);
				g.setFont(emptyMessageFont);
				g.drawString(emptyMessage, (getWidth() >> 1)
						- (emptyMessageWidth >> 1), 30);
			}

		}
	}

	private Listeners listeners = new Listeners();
	private Component component = new Component();

	private String emptyMessage;
	private int emptyMessageWidth;
	private FontMetrics emptyMessageMetric;
	private Font emptyMessageFont = new Font("arial", Font.BOLD, 30);
	private boolean isLoading = false;

	private ArrayList<VersionComponent> versionComponents = new ArrayList<VersionComponent>();
	private VersionComponent selected = null;
	private int selectedIndex = -1;

	public VersionSelectPanel() {
		component.setLayout(new MigLayout("ins 0", "", "[]0[]"));
		setEmptyMessage("Empty");
		component.addMouseListener(listeners);

	}

	public void addVersion(VersionComponent version) {
		component.add(version.getComponent(), "growx, pushx, wrap");
		versionComponents.add(version);
	}

	public void setEmptyMessage(String message) {
		emptyMessage = message;
		if (emptyMessageMetric != null) {
			emptyMessageWidth = emptyMessageMetric.stringWidth(emptyMessage);
		}
	}

	public void select(String name) {
		for (int i = 0; i < versionComponents.size(); i++) {
			if (versionComponents.get(i).getFullVersionName().equals(name)) {
				select(i);
				break;
			}
		}
	}

	public void select(VersionComponent component) {
		for (int i = 0; i < versionComponents.size(); i++) {
			if (versionComponents.get(i) == component) {
				select(i);
				break;
			}
		}
	}

	public void select(int index) {
		if (selected != null) {
			selected.setSelected(false);
			selected.repaintComponent();
		}

		selected = null;

		if (index < versionComponents.size()) {
			selected = versionComponents.get(index);
			selected.setSelected(true);
			selected.repaintComponent();
			selectedIndex = index;
		}
	}

	private void loadSelectedProfile() {
		if ((selected == null) || !selected.isReadyToLoad()) {
			return;
		}
		isLoading = true;
		selected.load();
		Options.instance.lastProfile.set(selected.getFullVersionName());
	}

	@Deprecated
	public KeyListener getKeyListener() {
		return listeners;
	}

	public JPanel getComponent() {
		return component;
	}
}
