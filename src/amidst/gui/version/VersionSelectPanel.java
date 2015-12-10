package amidst.gui.version;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import amidst.preferences.StringPreference;

public class VersionSelectPanel {
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

	private final StringPreference lastProfilePreference;
	private final Component component;
	private final List<VersionComponent> versionComponents = new ArrayList<VersionComponent>();

	private VersionComponent selected = null;
	private int selectedIndex = INVALID_INDEX;
	private String emptyMessage;
	private boolean isLoading = false;

	public VersionSelectPanel(StringPreference lastProfilePreference,
			String emptyMessage) {
		this.lastProfilePreference = lastProfilePreference;
		this.emptyMessage = emptyMessage;
		this.component = createComponent();
	}

	private Component createComponent() {
		Component component = new Component();
		component.setLayout(new MigLayout("ins 0", "", "[]0[]"));
		component.addMouseListener(createMouseListener());
		return component;
	}

	private MouseListener createMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (isLoading) {
					return;
				}
				doMousePressed(e.getPoint());
			}
		};
	}

	public KeyListener createKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (isLoading) {
					return;
				}
				doKeyPressed(e.getKeyCode());
			}
		};
	}

	private void doKeyPressed(int key) {
		if (key == KeyEvent.VK_DOWN) {
			select(selectedIndex + 1);
		} else if (key == KeyEvent.VK_UP) {
			select(selectedIndex - 1);
		} else if (key == KeyEvent.VK_ENTER) {
			loadSelectedProfile();
		}
	}

	private void doMousePressed(Point mousePosition) {
		select(getSelectedIndexFromYCoordinate(mousePosition));
		if (isLoadButtonClicked(mousePosition)) {
			loadSelectedProfile();
		}
	}

	private int getSelectedIndexFromYCoordinate(Point mousePosition) {
		return mousePosition.y / 40;
	}

	private boolean isLoadButtonClicked(Point mousePosition) {
		return mousePosition.x > component.getWidth() - 40;
	}

	public void select(String profileName) {
		select(getIndex(profileName));
	}

	private int getIndex(String profileName) {
		for (int i = 0; i < versionComponents.size(); i++) {
			if (versionComponents.get(i).getProfileName().equals(profileName)) {
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
		if (selected != null && selected.isReadyToLoad()) {
			lastProfilePreference.set(selected.getProfileName());
			isLoading = true;
			selected.load();
		}
	}

	public void addVersion(VersionComponent version) {
		component.add(version.getComponent(), "growx, pushx, wrap");
		versionComponents.add(version);
	}

	public void setEmptyMessage(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}

	public JPanel getComponent() {
		return component;
	}
}
