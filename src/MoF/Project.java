package MoF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import amidst.Application;
import amidst.map.MapViewer;

// TODO: we should remove this and integrate it into Options
@Deprecated
public class Project {
	private JPanel panel = new JPanel();
	private MapViewer mapViewer;
	private Application application;

	public Project(Application application) {
		this.application = application;
		initMapViewer();
		initPanel();
	}

	private void initMapViewer() {
		mapViewer = new MapViewer(this, application);
	}

	private void initPanel() {
		panel.setLayout(new BorderLayout());
		panel.add(mapViewer.getComponent(), BorderLayout.CENTER);
		panel.setBackground(Color.BLUE);
	}

	@Deprecated
	public KeyListener getKeyListener() {
		return mapViewer.getKeyListener();
	}

	public JPanel getPanel() {
		return panel;
	}

	public MapViewer getMapViewer() {
		return mapViewer;
	}

	public void moveMapTo(long x, long y) {
		mapViewer.centerAt(x, y);
	}

	public void dispose() {
		mapViewer.dispose();
		mapViewer = null;
	}
}
