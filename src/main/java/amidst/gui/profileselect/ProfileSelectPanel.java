package amidst.gui.profileselect;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The JPanel that lists the individual profiles installed.
 */
@NotThreadSafe
public class ProfileSelectPanel {
	@NotThreadSafe
	@SuppressWarnings("serial")
	private class Component extends JPanel {
		private static final int INVALID_EMPTY_MESSAGE_WIDTH = -1;

		private int emptyMessageWidth = INVALID_EMPTY_MESSAGE_WIDTH;

		private final String oldEmptyMessage;

		@CalledOnlyBy(AmidstThread.EDT)
		public Component() {
			this.oldEmptyMessage = emptyMessage;
		}

		@CalledOnlyBy(AmidstThread.EDT)
		@Override
		public void paintChildren(Graphics g) {
			super.paintChildren(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.gray);
			for (int i = 1; i <= profileComponents.size(); i++) {
				g2d.drawLine(0, i * 40, getWidth(), i * 40);
			}
		}

		@CalledOnlyBy(AmidstThread.EDT)
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (g instanceof Graphics2D g2d) {
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setColor(Color.white);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				if (profileComponents.isEmpty()) {
					g2d.setColor(Color.gray);
					g2d.setFont(new Font("arial", Font.BOLD, 30));
					if (!oldEmptyMessage.equals(emptyMessage) || emptyMessageWidth == INVALID_EMPTY_MESSAGE_WIDTH) {
						emptyMessageWidth = g2d.getFontMetrics().stringWidth(emptyMessage);
					}
					int x = (getWidth() >> 1) - (emptyMessageWidth >> 1);
					g2d.drawString(emptyMessage, x, 30);
				}
			}
		}
	}

	private static final int INVALID_INDEX = -1;

	private final Setting<String> lastProfileSetting;
	private final Component component;
	private final List<ProfileComponent> profileComponents = new ArrayList<>();

	private ProfileComponent selected = null;
	private String emptyMessage = "Scanning...";

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectPanel(Setting<String> lastProfileSetting) {
		this.lastProfileSetting = lastProfileSetting;

		component = new Component();
		component.setLayout(new MigLayout("ins 0", "", "[]0[]"));
		component.addMouseListener(createMouseListener());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private MouseListener createMouseListener() {
		return new MouseAdapter() {
			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void mousePressed(MouseEvent e) {
				if (!isLoading()) {
					Point mousePosition = e.getPoint();
					select(getSelectedIndexFromYCoordinate(mousePosition));
					if (isLoadButtonClicked(mousePosition)) {
						loadSelectedProfile();
					}
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
					switch (e.getKeyCode()) {
						case KeyEvent.VK_DOWN -> select(profileComponents.indexOf(selected) + 1);
						case KeyEvent.VK_UP -> select(profileComponents.indexOf(selected) - 1);
						case KeyEvent.VK_ENTER -> loadSelectedProfile();
					}
				}
			}
		};
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
		if (selected != null) {
			selected.setSelected(false);
			selected = null;
		}

		int i;
		if (profileComponents.isEmpty()) {
			i = INVALID_INDEX;
		} else if (index < 0) {
			i = 0;
		} else if (index >= profileComponents.size()) {
			i = profileComponents.size() - 1;
		} else {
			i = index;
		}

		if (i != INVALID_INDEX) {
			selected = profileComponents.get(i);
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
