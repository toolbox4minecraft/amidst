package amidst.gui.profileselect;

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
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@NotThreadSafe
public class ProfileSelectPanel {
	@NotThreadSafe
	@SuppressWarnings("serial")
	private class Component extends JPanel {
		private static final int INVALID_EMPTY_MESSAGE_WIDTH = -1;

		private int emptyMessageWidth = INVALID_EMPTY_MESSAGE_WIDTH;
		private String oldEmptyMessage;

		@CalledOnlyBy(AmidstThread.EDT)
		public Component() {
			this.oldEmptyMessage = emptyMessage;
		}

		@CalledOnlyBy(AmidstThread.EDT)
		@Override
		public void paintChildren(Graphics g) {
			super.paintChildren(g);
			Graphics2D g2d = (Graphics2D) g;
			drawSeparatorLines(g2d);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawSeparatorLines(Graphics2D g2d) {
			g2d.setColor(Color.gray);
			for (int i = 1; i <= profileComponents.size(); i++) {
				g2d.drawLine(0, i * 40, getWidth(), i * 40);
			}
		}

		@CalledOnlyBy(AmidstThread.EDT)
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			drawBackground(g2d);
			if (profileComponents.isEmpty()) {
				drawEmptyMessage(g2d);
			}
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawBackground(Graphics2D g2d) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawEmptyMessage(Graphics2D g2d) {
			g2d.setColor(Color.gray);
			g2d.setFont(EMPTY_MESSAGE_FONT);
			updateEmptyMessageWidth(g2d);
			int x = (getWidth() >> 1) - (emptyMessageWidth >> 1);
			g2d.drawString(emptyMessage, x, 30);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void updateEmptyMessageWidth(Graphics2D g2d) {
			if (!oldEmptyMessage.equals(emptyMessage) || emptyMessageWidth == INVALID_EMPTY_MESSAGE_WIDTH) {
				emptyMessageWidth = g2d.getFontMetrics().stringWidth(emptyMessage);
			}
		}
	}

	private static final Font EMPTY_MESSAGE_FONT = new Font("arial", Font.BOLD, 30);
	private static final int INVALID_INDEX = -1;

	private final Setting<String> lastProfileSetting;
	private final Component component;
	private final List<ProfileComponent> profileComponents = new ArrayList<>();

	private ProfileComponent selected = null;
	private String emptyMessage;

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectPanel(Setting<String> lastProfileSetting, String emptyMessage) {
		this.lastProfileSetting = lastProfileSetting;
		this.emptyMessage = emptyMessage;
		this.component = createComponent();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Component createComponent() {
		Component component = new Component();
		component.setLayout(new MigLayout("ins 0", "", "[]0[]"));
		component.addMouseListener(createMouseListener());
		return component;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private MouseListener createMouseListener() {
		return new MouseAdapter() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void mousePressed(MouseEvent e) {
				if (!isLoading()) {
					doMousePressed(e.getPoint());
				}
			}
		};
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public KeyListener createKeyListener() {
		return new KeyAdapter() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void keyPressed(KeyEvent e) {
				if (!isLoading()) {
					doKeyPressed(e.getKeyCode());
				}
			}
		};
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doKeyPressed(int key) {
		if (key == KeyEvent.VK_DOWN) {
			select(profileComponents.indexOf(selected) + 1);
		} else if (key == KeyEvent.VK_UP) {
			select(profileComponents.indexOf(selected) - 1);
		} else if (key == KeyEvent.VK_ENTER) {
			loadSelectedProfile();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doMousePressed(Point mousePosition) {
		select(getSelectedIndexFromYCoordinate(mousePosition));
		if (isLoadButtonClicked(mousePosition)) {
			loadSelectedProfile();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getSelectedIndexFromYCoordinate(Point mousePosition) {
		return mousePosition.y / 40;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isLoadButtonClicked(Point mousePosition) {
		return mousePosition.x > component.getWidth() - 40;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void selectFirst() {
		select(0);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void select(String profileName) {
		select(getIndex(profileName));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getIndex(String profileName) {
		for (int i = 0; i < profileComponents.size(); i++) {
			if (profileComponents.get(i).getProfileName().equals(profileName)) {
				return i;
			}
		}
		return INVALID_INDEX;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void select(int index) {
		deselectSelected();
		doSelect(getBoundedIndex(index));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void deselectSelected() {
		if (selected != null) {
			selected.setSelected(false);
			selected = null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getBoundedIndex(int index) {
		if (profileComponents.isEmpty()) {
			return INVALID_INDEX;
		} else if (index < 0) {
			return 0;
		} else if (index >= profileComponents.size()) {
			return profileComponents.size() - 1;
		} else {
			return index;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doSelect(int index) {
		if (index != INVALID_INDEX) {
			selected = profileComponents.get(index);
			selected.setSelected(true);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void loadSelectedProfile() {
		if (selected != null && selected.isReadyToLoad()) {
			lastProfileSetting.set(selected.getProfileName());
			selected.load();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void addProfile(ProfileComponent profile) {
		profileComponents.add(profile);

		// Sort and re-add all components
		profileComponents.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
		component.removeAll();
		for (ProfileComponent p: profileComponents) {
			component.add(p.getComponent(), "growx, pushx, wrap");
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void setEmptyMessage(String emptyMessage) {
		this.emptyMessage = emptyMessage;
		component.repaint();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public JPanel getComponent() {
		return component;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isLoading() {
		for (ProfileComponent profileComponent : profileComponents) {
			if (profileComponent.isLoading()) {
				return true;
			}
		}
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void resolveAllLater() {
		if (!isLoading()) {
			profileComponents.forEach(ProfileComponent::resolveLater);
		}
	}
}
