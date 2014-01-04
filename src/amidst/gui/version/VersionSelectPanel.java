package amidst.gui.version;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class VersionSelectPanel extends JPanel implements MouseListener, KeyListener {
	private String emptyMessage;
	private int emptyMessageWidth;
	private FontMetrics emptyMessageMetric;
	private Font emptyMessageFont = new Font("arial", Font.BOLD, 30);
	private boolean isLoading = false;
	
	private ArrayList<VersionComponent> components = new ArrayList<VersionComponent>();
	private VersionComponent selected = null;
	private int selectedIndex = -1;
	
	public VersionSelectPanel() {
		setLayout(new MigLayout("ins 0", "", "[]0[]"));
		setEmptyMessage("Empty");
		addMouseListener(this);
		
		
	}
	
	public void addVersion(VersionComponent version) {
		add(version, "growx, pushx, wrap");
		components.add(version);
	}
	
	@Override
	public void paintChildren(Graphics g) {
		super.paintChildren(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.gray);
		for (int i = 1; i <= components.size(); i++) {
			g2d.drawLine(0, i * 40, getWidth(), i * 40);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (emptyMessageMetric == null) {
			emptyMessageMetric = g.getFontMetrics(emptyMessageFont);
			emptyMessageWidth = emptyMessageMetric.stringWidth(emptyMessage);
		}
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (components.size() == 0) {
			g.setColor(Color.gray);
			g.setFont(emptyMessageFont);
			g.drawString(emptyMessage, (getWidth() >> 1) - (emptyMessageWidth >> 1), 30);
		}
		
	}

	public void setEmptyMessage(String message) {
		emptyMessage = message;
		if (emptyMessageMetric != null)
			emptyMessageWidth = emptyMessageMetric.stringWidth(emptyMessage);
	}
	
	public void select(String name) {
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i).getVersionName().equals(name)) {
				select(i);
				break;
			}
		}
	}
	
	public void select(VersionComponent component) {
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i) == component) {
				select(i);
				break;
			}
		}
	}
	
	public void select(int index) {
		if (selected != null) {
			selected.setSelected(false);
			selected.repaint();
		}
		
		selected = null;
		
		if (index < components.size()) {
			selected = components.get(index);
			selected.setSelected(true);
			selected.repaint();
			selectedIndex = index;
		}
	}
	
	private void loadSelectedProfile() {
		if ((selected == null) || !selected.isReadyToLoad())
			return;
		isLoading = true;
		selected.load();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent event)  {
		if (isLoading)
			return;
		
		int index = event.getPoint().y / 40;
		select(index);
		
		if (event.getPoint().x > getWidth() - 40)
			loadSelectedProfile();
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (isLoading)
			return;
		int key = event.getKeyCode();
		switch (key) {
			case KeyEvent.VK_DOWN:
				if (selectedIndex < components.size() - 1)
					select(selectedIndex + 1);
				break;
			case KeyEvent.VK_UP:
				if (selectedIndex > 0)
					select(selectedIndex - 1);
				else if (selectedIndex == -1)
					select(0);
				break;
			case KeyEvent.VK_ENTER:
				loadSelectedProfile();
				break;
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
	}
	@Override
	public void keyTyped(KeyEvent event) {
	}
}
